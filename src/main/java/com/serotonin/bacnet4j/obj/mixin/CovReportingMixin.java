package com.serotonin.bacnet4j.obj.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.obj.AbstractMixin;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedCovNotificationRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedCovNotificationRequest;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.CovSubscription;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.PropertyReference;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.constructed.RecipientProcess;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

/**
 * Could add support for COV_Period...
 * 
 * @author Matthew
 */
public class CovReportingMixin extends AbstractMixin {
    private final CovReportingCriteria criteria;
    private final List<ObjectCovSubscription> covSubscriptions = new ArrayList<ObjectCovSubscription>();

    public CovReportingMixin(BACnetObject bo, CovReportingCriteria criteria, Real covIncrement) {
        super(bo);
        this.criteria = criteria;
        if (covIncrement != null)
            writePropertyImpl(PropertyIdentifier.covIncrement, covIncrement);
    }

    @Override
    protected boolean validateProperty(PropertyValue value) throws BACnetServiceException {
        if (PropertyIdentifier.covIncrement.equals(value.getPropertyIdentifier())) {
            Real covIncrement = (Real) value.getValue();
            if (covIncrement.floatValue() < 0)
                throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
        }

        return false;
    }

    @Override
    protected void afterWriteProperty(PropertyIdentifier pid, Encodable oldValue, Encodable newValue) {
        if (pid.isOneOf(criteria.monitoredProperties)) {
            long now = System.currentTimeMillis();
            synchronized (covSubscriptions) {
                List<ObjectCovSubscription> expired = null;
                for (ObjectCovSubscription subscription : covSubscriptions) {
                    if (subscription.hasExpired(now)) {
                        if (expired == null)
                            expired = new ArrayList<ObjectCovSubscription>();
                        expired.add(subscription);
                    }
                    else {
                        boolean send = true;
                        if (pid.equals(criteria.incrementProperty))
                            send = incrementChange(subscription, newValue);
                        if (send) {
                            if (pid.equals(subscription.getMonitoredProperty()))
                                sendPropertyNotification(subscription, now, pid);
                            else if (subscription.getMonitoredProperty() == null)
                                sendObjectNotification(subscription, now);
                        }
                    }
                }

                if (expired != null)
                    covSubscriptions.removeAll(expired);
            }
        }
    }

    public void addCovSubscription(Address from, UnsignedInteger subscriberProcessIdentifier,
            com.serotonin.bacnet4j.type.primitive.Boolean issueConfirmedNotifications, UnsignedInteger lifetime,
            PropertyReference monitoredPropertyIdentifier, Real covIncrement) throws BACnetServiceException {
        synchronized (covSubscriptions) {
            ObjectCovSubscription sub = findCovSubscription(from, subscriberProcessIdentifier);

            if (sub == null) {
                // Ensure that this object is valid for COV notifications.
                if (monitoredPropertyIdentifier != null) {
                    // Don't allow a subscription on a sequence index
                    if (monitoredPropertyIdentifier.getPropertyArrayIndex() != null)
                        throw new BACnetServiceException(ErrorClass.object, ErrorCode.optionalFunctionalityNotSupported);

                    // Make sure that the requested property is one of the supported properties.
                    if (!monitoredPropertyIdentifier.getPropertyIdentifier().isOneOf(criteria.monitoredProperties))
                        throw new BACnetServiceException(ErrorClass.object, ErrorCode.optionalFunctionalityNotSupported);
                }

                sub = new ObjectCovSubscription(from, subscriberProcessIdentifier, //
                        monitoredPropertyIdentifier == null ? null
                                : monitoredPropertyIdentifier.getPropertyIdentifier());

                covSubscriptions.add(sub);
            }

            sub.setIssueConfirmedNotifications(issueConfirmedNotifications.booleanValue());
            sub.setExpiryTime(lifetime.intValue());
            sub.setCovIncrement(covIncrement);

            // Remove from device list.
            RecipientProcess rp = new RecipientProcess(new Recipient(from), subscriberProcessIdentifier);
            removeFromDeviceList(rp);

            // Add to the device list.
            ObjectPropertyReference opr = new ObjectPropertyReference(
                    (ObjectIdentifier) get(PropertyIdentifier.objectIdentifier),
                    monitoredPropertyIdentifier == null ? null : monitoredPropertyIdentifier.getPropertyIdentifier(),
                    monitoredPropertyIdentifier == null ? null : monitoredPropertyIdentifier.getPropertyArrayIndex());
            CovSubscription cs = new CovSubscription(rp, opr, issueConfirmedNotifications, lifetime, covIncrement);
            SequenceOf<CovSubscription> deviceList = getLocalDevice().getConfiguration().get(
                    PropertyIdentifier.activeCovSubscriptions);
            deviceList.add(cs);

            // "Immediately" send a notification
            final ObjectCovSubscription subscription = sub;
            getLocalDevice().getTimer().schedule(new TimerTask() {
                @Override
                public void run() {
                    long now = System.currentTimeMillis();
                    if (subscription.getMonitoredProperty() != null)
                        sendPropertyNotification(subscription, now, subscription.getMonitoredProperty());
                    else
                        sendObjectNotification(subscription, now);
                }
            }, 20);
        }
    }

    public void removeCovSubscription(Address from, UnsignedInteger subscriberProcessIdentifier) {
        synchronized (covSubscriptions) {
            ObjectCovSubscription sub = findCovSubscription(from, subscriberProcessIdentifier);
            if (sub != null)
                covSubscriptions.remove(sub);

            removeFromDeviceList(new RecipientProcess(new Recipient(from), subscriberProcessIdentifier));
        }
    }

    private ObjectCovSubscription findCovSubscription(Address from, UnsignedInteger subscriberProcessIdentifier) {
        for (ObjectCovSubscription sub : covSubscriptions) {
            if (sub.getAddress().equals(from)
                    && sub.getSubscriberProcessIdentifier().equals(subscriberProcessIdentifier))
                return sub;
        }
        return null;
    }

    private void removeFromDeviceList(RecipientProcess rp) {
        SequenceOf<CovSubscription> deviceList = getLocalDevice().getConfiguration().get(
                PropertyIdentifier.activeCovSubscriptions);
        for (CovSubscription cs : deviceList) {
            if (cs.getRecipient().equals(rp)) {
                deviceList.remove(cs);
                break;
            }
        }
    }

    void sendObjectNotification(ObjectCovSubscription subscription, long now) {
        SequenceOf<PropertyValue> values = new SequenceOf<PropertyValue>();
        for (PropertyIdentifier pid : criteria.propertiesReported) {
            Encodable value = get(pid);
            if (pid.equals(criteria.incrementProperty))
                subscription.setLastCovIncrementValue(value);
            values.add(new PropertyValue(pid, value));
        }
        sendNotification(subscription, now, values);
    }

    void sendPropertyNotification(ObjectCovSubscription subscription, long now, PropertyIdentifier pid) {
        Encodable value = get(pid);
        if (pid.equals(criteria.incrementProperty))
            subscription.setLastCovIncrementValue(value);
        sendNotification(subscription, now, new SequenceOf<PropertyValue>(new PropertyValue(pid, value)));
    }

    private void sendNotification(ObjectCovSubscription subscription, long now, SequenceOf<PropertyValue> values) {
        ObjectIdentifier deviceId = getLocalDevice().getConfiguration().getId();
        ObjectIdentifier id = get(PropertyIdentifier.objectIdentifier);
        UnsignedInteger timeLeft = new UnsignedInteger(subscription.getTimeRemaining(now));

        if (subscription.isIssueConfirmedNotifications()) {
            ConfirmedCovNotificationRequest req = new ConfirmedCovNotificationRequest( //
                    subscription.getSubscriberProcessIdentifier(), deviceId, id, timeLeft, values);
            getLocalDevice().send(subscription.getAddress(), req, null);
        }
        else {
            UnconfirmedCovNotificationRequest req = new UnconfirmedCovNotificationRequest(
                    subscription.getSubscriberProcessIdentifier(), deviceId, id, timeLeft, values);
            getLocalDevice().send(subscription.getAddress(), req);
        }
    }

    //
    //
    // COV reporting criteria
    //
    static class CovReportingCriteria {
        final PropertyIdentifier[] monitoredProperties;
        final PropertyIdentifier[] propertiesReported;
        final PropertyIdentifier incrementProperty;

        public CovReportingCriteria(PropertyIdentifier[] monitoredProperties, PropertyIdentifier[] propertiesReported,
                PropertyIdentifier incrementProperty) {
            this.monitoredProperties = monitoredProperties;
            this.propertiesReported = propertiesReported;
            this.incrementProperty = incrementProperty;
        }
    }

    // For: Analog Input, Analog Output, Analog Value, Large Analog Value, Integer Value, Positive Integer Value,
    // Lighting Output
    public static final CovReportingCriteria criteria13_1_3 = new CovReportingCriteria( //
            new PropertyIdentifier[] { PropertyIdentifier.presentValue, PropertyIdentifier.statusFlags }, //
            new PropertyIdentifier[] { PropertyIdentifier.presentValue, PropertyIdentifier.statusFlags }, //
            PropertyIdentifier.presentValue);

    // For: Binary Input, Binary Output, Binary Value, Life Safety Point, Life Safety Zone, Multi-state Input,
    // Multi-state Output, Multi-state Value, OctetString Value, CharacterString Value, Time Value, DateTime Value,
    // Date Value, Time Pattern Value, Date Pattern Value, DateTime Pattern Value
    public static final CovReportingCriteria criteria13_1_4 = new CovReportingCriteria( //
            new PropertyIdentifier[] { PropertyIdentifier.presentValue, PropertyIdentifier.statusFlags }, //
            new PropertyIdentifier[] { PropertyIdentifier.presentValue, PropertyIdentifier.statusFlags }, //
            null);

    boolean incrementChange(ObjectCovSubscription subscription, Encodable value) {
        Encodable lastValue = subscription.getLastCovIncrementValue();
        if (lastValue == null)
            return true;

        Encodable covIncrement = subscription.getCovIncrement();
        if (covIncrement == null)
            covIncrement = get(PropertyIdentifier.covIncrement);

        double increment, last, newValue;
        if (value instanceof Real) {
            increment = ((Real) covIncrement).floatValue();
            last = ((Real) lastValue).floatValue();
            newValue = ((Real) value).floatValue();
        }
        else
            throw new BACnetRuntimeException("Unhandled type: " + value.getClass());

        double diff = newValue - last;
        if (diff < 0)
            diff = -diff;
        if (diff >= increment)
            return true;

        return false;
    }
}
