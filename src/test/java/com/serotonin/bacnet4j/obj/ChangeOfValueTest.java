package com.serotonin.bacnet4j.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.exception.ErrorAPDUException;
import com.serotonin.bacnet4j.service.confirmed.SubscribeCOVPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.SubscribeCOVRequest;
import com.serotonin.bacnet4j.type.constructed.CovSubscription;
import com.serotonin.bacnet4j.type.constructed.ObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.PropertyReference;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.constructed.RecipientProcess;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class ChangeOfValueTest extends AbstractTest {
    static final Logger LOG = LoggerFactory.getLogger(ChangeOfValueTest.class);

    @Override
    public void before() throws Exception {
        // no op
    }

    @Test
    public void objectCovErrors() throws Exception {
        try {
            d2.send(rd1,
                    new SubscribeCOVRequest(new UnsignedInteger(4), new ObjectIdentifier(ObjectType.analogValue, 0),
                            new Boolean(true), new UnsignedInteger(1000))).get();
            fail("Should have thrown an exception");
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.object, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.unknownObject, e.getBACnetError().getErrorCode());
        }

        AnalogValueObject av = new AnalogValueObject(0, "av0", 10, EngineeringUnits.amperes, false);
        d1.addObject(av);

        try {
            d2.send(rd1,
                    new SubscribeCOVRequest(new UnsignedInteger(4), av.getId(), new Boolean(true), new UnsignedInteger(
                            1000))).get();
            fail("Should have thrown an exception");
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.object, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.optionalFunctionalityNotSupported, e.getBACnetError().getErrorCode());
        }

        av.supportCovReporting(4);

        d2.send(rd1,
                new SubscribeCOVRequest(new UnsignedInteger(4), av.getId(), new Boolean(true),
                        new UnsignedInteger(1000))).get();
    }

    @Test
    public void propertyCovErrors() throws Exception {
        try {
            d2.send(rd1,
                    new SubscribeCOVPropertyRequest(new UnsignedInteger(4), new ObjectIdentifier(
                            ObjectType.analogValue, 0), new Boolean(true), new UnsignedInteger(1000),
                            new PropertyReference(PropertyIdentifier.accessDoors), null)).get();
            fail("Should have thrown an exception");
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.object, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.unknownObject, e.getBACnetError().getErrorCode());
        }

        AnalogValueObject av = new AnalogValueObject(0, "av0", 10, EngineeringUnits.amperes, false);
        d1.addObject(av);

        try {
            d2.send(rd1,
                    new SubscribeCOVPropertyRequest(new UnsignedInteger(4), new ObjectIdentifier(
                            ObjectType.analogValue, 0), new Boolean(true), new UnsignedInteger(1000),
                            new PropertyReference(PropertyIdentifier.accessDoors), null)).get();
            fail("Should have thrown an exception");
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.object, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.optionalFunctionalityNotSupported, e.getBACnetError().getErrorCode());
        }

        av.supportCovReporting(4);

        try {
            d2.send(rd1,
                    new SubscribeCOVPropertyRequest(new UnsignedInteger(4), new ObjectIdentifier(
                            ObjectType.analogValue, 0), new Boolean(true), new UnsignedInteger(1000),
                            new PropertyReference(PropertyIdentifier.accessDoors), null)).get();
            fail("Should have thrown an exception");
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.object, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.optionalFunctionalityNotSupported, e.getBACnetError().getErrorCode());
        }

        d2.send(rd1,
                new SubscribeCOVPropertyRequest(new UnsignedInteger(4),
                        new ObjectIdentifier(ObjectType.analogValue, 0), new Boolean(true), new UnsignedInteger(1000),
                        new PropertyReference(PropertyIdentifier.presentValue), null)).get();
    }

    @Test
    public void objectCov() throws Exception {
        AnalogValueObject av = new AnalogValueObject(0, "av0", 10, EngineeringUnits.amperes, false);
        av.supportCovReporting(4);
        d1.addObject(av);

        CovNotifListener listener = new CovNotifListener();
        d2.getEventHandler().addListener(listener);

        // Subscribe to changes.
        d2.send(rd1, new SubscribeCOVRequest(new UnsignedInteger(4), av.getId(), new Boolean(true), //
                new UnsignedInteger(2))).get();

        // Ensure the subscription is in the device's list.
        SequenceOf<CovSubscription> deviceList = d1.getConfiguration().getProperty(
                PropertyIdentifier.activeCovSubscriptions);
        assertEquals(1, deviceList.getCount());
        CovSubscription subscription = deviceList.get(1);
        assertEquals(null, subscription.getCovIncrement());
        assertEquals(new Boolean(true), subscription.getIssueConfirmedNotifications());
        assertEquals(new ObjectPropertyReference(av.getId(), null), subscription.getMonitoredPropertyReference());
        assertEquals(new RecipientProcess(new Recipient(rd2.getAddress()), new UnsignedInteger(4)),
                subscription.getRecipient());

        // Subscribing should have caused a notification to be sent.
        Thread.sleep(50);
        assertEquals(1, listener.notifs.size());
        Map<String, Object> notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(4), notif.get("subscriberProcessIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(av.getId(), notif.get("monitoredObjectIdentifier"));
        assertEquals(new SequenceOf<PropertyValue>( //
                new PropertyValue(PropertyIdentifier.presentValue, new Real(10)), //
                new PropertyValue(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false))),
                notif.get("listOfValues"));

        // Write a new value that will trigger a notification.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(20));
        Thread.sleep(100);
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(4), notif.get("subscriberProcessIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(av.getId(), notif.get("monitoredObjectIdentifier"));
        assertEquals(new SequenceOf<PropertyValue>( //
                new PropertyValue(PropertyIdentifier.presentValue, new Real(20)), //
                new PropertyValue(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false))),
                notif.get("listOfValues"));

        // Write a new value that won't trigger a notification.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(21));
        Thread.sleep(100);
        assertEquals(0, listener.notifs.size());

        // Change a value that is not monitored
        av.writePropertyImpl(PropertyIdentifier.objectName, new CharacterString("av0-new-name"));
        Thread.sleep(100);
        assertEquals(0, listener.notifs.size());

        // Change a different value that is monitored
        av.writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(true));
        Thread.sleep(100);
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(new SequenceOf<PropertyValue>( //
                new PropertyValue(PropertyIdentifier.presentValue, new Real(21)), //
                new PropertyValue(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, true))),
                notif.get("listOfValues"));

        // Change until the increment.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(22));
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(23));
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(24));
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(25));
        Thread.sleep(100);
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(new SequenceOf<PropertyValue>( //
                new PropertyValue(PropertyIdentifier.presentValue, new Real(25)), //
                new PropertyValue(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, true))),
                notif.get("listOfValues"));

        // Wait until the subscription expires and write a change that would trigger a notification.
        Thread.sleep(2000);
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(40));
        Thread.sleep(100);
        assertEquals(0, listener.notifs.size());
    }

    @Test
    public void unsubscribe() throws Exception {
        AnalogValueObject av = new AnalogValueObject(0, "av0", 10, EngineeringUnits.amperes, false);
        av.supportCovReporting(4);
        d1.addObject(av);

        CovNotifListener listener = new CovNotifListener();
        d2.getEventHandler().addListener(listener);

        // Subscribe to changes.
        d2.send(rd1,
                new SubscribeCOVRequest(new UnsignedInteger(4), av.getId(), new Boolean(true), new UnsignedInteger(2)))
                .get();

        // Ensure the subscription is in the device's list.
        SequenceOf<CovSubscription> deviceList = d1.getConfiguration().getProperty(
                PropertyIdentifier.activeCovSubscriptions);
        assertEquals(1, deviceList.getCount());
        CovSubscription subscription = deviceList.get(1);
        assertEquals(null, subscription.getCovIncrement());
        assertEquals(new Boolean(true), subscription.getIssueConfirmedNotifications());
        assertEquals(new ObjectPropertyReference(av.getId(), null), subscription.getMonitoredPropertyReference());
        assertEquals(new RecipientProcess(new Recipient(rd2.getAddress()), new UnsignedInteger(4)),
                subscription.getRecipient());

        // Unsubscribe
        d2.send(rd1, new SubscribeCOVRequest(new UnsignedInteger(4), av.getId(), null, null)).get();

        // Ensure the subscription is gone from the device's list.
        deviceList = d1.getConfiguration().getProperty(PropertyIdentifier.activeCovSubscriptions);
        assertEquals(0, deviceList.getCount());
    }

    @Test
    public void propertyCov() throws Exception {
        AnalogValueObject av = new AnalogValueObject(0, "av0", 10, EngineeringUnits.amperes, false);
        av.supportCovReporting(4);
        d1.addObject(av);

        CovNotifListener listener = new CovNotifListener();
        d2.getEventHandler().addListener(listener);

        // Subscribe to changes.
        d2.send(rd1, new SubscribeCOVPropertyRequest(new UnsignedInteger(4), av.getId(), new Boolean(false), //
                new UnsignedInteger(2), new PropertyReference(PropertyIdentifier.statusFlags), null)).get();

        // Ensure the subscription is in the device's list.
        SequenceOf<CovSubscription> deviceList = d1.getConfiguration().getProperty(
                PropertyIdentifier.activeCovSubscriptions);
        assertEquals(1, deviceList.getCount());
        CovSubscription subscription = deviceList.get(1);
        assertEquals(null, subscription.getCovIncrement());
        assertEquals(new Boolean(false), subscription.getIssueConfirmedNotifications());
        assertEquals(new ObjectPropertyReference(av.getId(), PropertyIdentifier.statusFlags),
                subscription.getMonitoredPropertyReference());
        assertEquals(new RecipientProcess(new Recipient(rd2.getAddress()), new UnsignedInteger(4)),
                subscription.getRecipient());

        // Subscribing should have caused a notification to be sent.
        Thread.sleep(50);
        assertEquals(1, listener.notifs.size());
        Map<String, Object> notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(4), notif.get("subscriberProcessIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(av.getId(), notif.get("monitoredObjectIdentifier"));
        assertEquals(new SequenceOf<PropertyValue>( //
                new PropertyValue(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false))),
                notif.get("listOfValues"));

        // Write a new value to a different property. That will not trigger a notification.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(20));
        Thread.sleep(100);
        assertEquals(0, listener.notifs.size());

        // Write a change to the status flags.
        av.writePropertyImpl(PropertyIdentifier.reliability, Reliability.memberFault);
        Thread.sleep(100);
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(4), notif.get("subscriberProcessIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(av.getId(), notif.get("monitoredObjectIdentifier"));
        assertEquals(new SequenceOf<PropertyValue>( //
                new PropertyValue(PropertyIdentifier.statusFlags, new StatusFlags(false, true, false, false))),
                notif.get("listOfValues"));
    }
}
