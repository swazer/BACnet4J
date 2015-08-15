/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.obj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.obj.mixin.CommandableMixin;
import com.serotonin.bacnet4j.obj.mixin.CovReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.CovReportingMixin.CovReportingCriteria;
import com.serotonin.bacnet4j.obj.mixin.HasStatusFlagsMixin;
import com.serotonin.bacnet4j.obj.mixin.PropertyListMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin;
import com.serotonin.bacnet4j.service.acknowledgement.GetAlarmSummaryAck.AlarmSummary;
import com.serotonin.bacnet4j.service.acknowledgement.GetEnrollmentSummaryAck.EnrollmentSummary;
import com.serotonin.bacnet4j.service.acknowledgement.GetEventInformationAck.EventSummary;
import com.serotonin.bacnet4j.service.confirmed.GetEnrollmentSummaryRequest.AcknowledgmentFilter;
import com.serotonin.bacnet4j.service.confirmed.GetEnrollmentSummaryRequest.EventStateFilter;
import com.serotonin.bacnet4j.service.confirmed.GetEnrollmentSummaryRequest.PriorityFilter;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.PropertyReference;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.RecipientProcess;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.Time;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.Utils;

/**
 * @author Matthew
 */
public class BACnetObject implements Serializable {
    private static final long serialVersionUID = 569892306207282576L;

    private final ObjectType objectType;
    protected final Map<PropertyIdentifier, Encodable> properties = new ConcurrentHashMap<PropertyIdentifier, Encodable>();
    private final List<BACnetObjectListener> listeners = new CopyOnWriteArrayList<BACnetObjectListener>();

    private LocalDevice localDevice;

    // Mixins
    private final List<AbstractMixin> mixins = new ArrayList<AbstractMixin>();
    private CommandableMixin commandableMixin;
    private HasStatusFlagsMixin hasStatusFlagsMixin;
    private final PropertyListMixin propertyListMixin;
    private IntrinsicReportingMixin intrinsicReportingMixin;
    private CovReportingMixin changeOfValueMixin;

    public BACnetObject(ObjectType type, int instanceNumber) {
        this(type, instanceNumber, null);
    }

    public BACnetObject(ObjectType type, int instanceNumber, String name) {
        this(new ObjectIdentifier(type, instanceNumber), name);
    }

    public BACnetObject(ObjectIdentifier id) {
        this(id, null);
    }

    public BACnetObject(ObjectIdentifier id, String name) {
        if (id == null)
            throw new IllegalArgumentException("object id cannot be null");
        objectType = id.getObjectType();

        if (name == null)
            name = id.toString();
        properties.put(PropertyIdentifier.objectIdentifier, id);
        properties.put(PropertyIdentifier.objectName, new CharacterString(name));
        properties.put(PropertyIdentifier.objectType, objectType);

        // All objects have a property list.
        propertyListMixin = new PropertyListMixin(this);
        addMixin(propertyListMixin);
        propertyListMixin.update();
    }

    public void setLocalDevice(LocalDevice localDevice) {
        this.localDevice = localDevice;
    }

    //
    //
    // Convenience methods
    //
    public ObjectIdentifier getId() {
        return get(PropertyIdentifier.objectIdentifier);
    }

    public int getInstanceId() {
        return getId().getInstanceNumber();
    }

    public String getObjectName() {
        CharacterString name = get(PropertyIdentifier.objectName);
        if (name == null)
            return null;
        return name.getValue();
    }

    LocalDevice getLocalDevice() {
        return localDevice;
    }

    //
    //
    // Object notifications
    //
    /**
     * Called when the object is added to the device.
     */
    public void addedToDevice() {
        // no op, override as required
    }

    /**
     * Called when the object is removed from the device.
     */
    public void removedFromDevice() {
        // no op, override as required
    }

    //
    //
    // Listeners
    //
    public void addListener(BACnetObjectListener l) {
        listeners.add(l);
    }

    public void removeListener(BACnetObjectListener l) {
        listeners.remove(l);
    }

    //
    //
    // Mixins
    //
    protected final void addMixin(AbstractMixin mixin) {
        mixins.add(mixin);

        if (mixin instanceof HasStatusFlagsMixin)
            hasStatusFlagsMixin = (HasStatusFlagsMixin) mixin;
        else if (mixin instanceof CommandableMixin)
            commandableMixin = (CommandableMixin) mixin;
        else if (mixin instanceof IntrinsicReportingMixin)
            intrinsicReportingMixin = (IntrinsicReportingMixin) mixin;
        else if (mixin instanceof CovReportingMixin)
            changeOfValueMixin = (CovReportingMixin) mixin;
    }

    public void setOverridden(boolean b) {
        if (hasStatusFlagsMixin != null)
            hasStatusFlagsMixin.setOverridden(b);
        if (commandableMixin != null)
            commandableMixin.setOverridden(b);
    }

    public boolean isOverridden() {
        if (hasStatusFlagsMixin != null)
            return hasStatusFlagsMixin.isOverridden();
        if (commandableMixin != null)
            return commandableMixin.isOverridden();
        return false;
    }

    //
    // Commandable
    public void supportCommandable(Encodable relinquishDefault) {
        if (commandableMixin != null)
            commandableMixin.setCommandable(relinquishDefault);
    }

    public boolean isCommandable() {
        if (commandableMixin != null)
            return commandableMixin.isCommandable();
        return false;
    }

    //
    // Intrinsic reporting
    public void acknowledgeAlarm(UnsignedInteger acknowledgingProcessIdentifier, EventState eventStateAcknowledged,
            TimeStamp timeStamp, CharacterString acknowledgmentSource, TimeStamp timeOfAcknowledgment)
                    throws BACnetServiceException {
        if (intrinsicReportingMixin == null)
            throw new BACnetServiceException(ErrorClass.object, ErrorCode.noAlarmConfigured);
        intrinsicReportingMixin.acknowledgeAlarm(acknowledgingProcessIdentifier, eventStateAcknowledged, timeStamp,
                acknowledgmentSource, timeOfAcknowledgment);
    }

    //
    // COVs
    public void supportCovReporting(CovReportingCriteria criteria, Real covIncrement) {
        addMixin(new CovReportingMixin(this, criteria, covIncrement));
    }

    public AlarmSummary getAlarmSummary() {
        if (intrinsicReportingMixin != null)
            return intrinsicReportingMixin.getAlarmSummary();
        return null;
    }

    public EventSummary getEventSummary() {
        if (intrinsicReportingMixin != null)
            return intrinsicReportingMixin.getEventSummary();
        return null;
    }

    public EnrollmentSummary getEnrollmentSummary(AcknowledgmentFilter acknowledgmentFilter,
            RecipientProcess enrollmentFilter, EventStateFilter eventStateFilter, EventType eventTypeFilter,
            PriorityFilter priorityFilter, UnsignedInteger notificationClassFilter) {
        if (intrinsicReportingMixin != null)
            return intrinsicReportingMixin.getEnrollmentSummary(acknowledgmentFilter, enrollmentFilter,
                    eventStateFilter, eventTypeFilter, priorityFilter, notificationClassFilter);
        return null;
    }

    //
    // COV
    public void addCovSubscription(Address from, UnsignedInteger subscriberProcessIdentifier,
            com.serotonin.bacnet4j.type.primitive.Boolean issueConfirmedNotifications, UnsignedInteger lifetime,
            PropertyReference monitoredPropertyIdentifier, Real covIncrement) throws BACnetServiceException {
        if (changeOfValueMixin == null)
            throw new BACnetServiceException(ErrorClass.object, ErrorCode.optionalFunctionalityNotSupported);
        changeOfValueMixin.addCovSubscription(from, subscriberProcessIdentifier, issueConfirmedNotifications, lifetime,
                monitoredPropertyIdentifier, covIncrement);
    }

    public void removeCovSubscription(Address from, UnsignedInteger subscriberProcessIdentifier) {
        if (changeOfValueMixin != null)
            changeOfValueMixin.removeCovSubscription(from, subscriberProcessIdentifier);
    }

    //
    //
    // Get property
    //
    @SuppressWarnings("unchecked")
    public final <T extends Encodable> T getProperty(PropertyIdentifier pid) throws BACnetServiceException {
        // Check that the requested property is valid for the object. This will throw an exception if the
        // property doesn't belong.
        ObjectProperties.getPropertyTypeDefinitionRequired(objectType, pid);

        // Do some property-specific checking here.
        if (PropertyIdentifier.localTime.equals(pid))
            return (T) new Time();
        if (PropertyIdentifier.localDate.equals(pid))
            return (T) new Date();

        // Give the mixins notice that the property is being read.
        for (AbstractMixin mixin : mixins)
            mixin.beforeReadProperty(pid);

        return (T) get(pid);
    }

    /**
     * This method should only be used internally. Services should use the getProperty method.
     */
    @SuppressWarnings("unchecked")
    public <T extends Encodable> T get(PropertyIdentifier pid) {
        return (T) properties.get(pid);
    }

    public final Encodable getPropertyRequired(PropertyIdentifier pid) throws BACnetServiceException {
        Encodable p = getProperty(pid);
        if (p == null)
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.unknownProperty);
        return p;
    }

    public final Encodable getProperty(PropertyIdentifier pid, UnsignedInteger propertyArrayIndex)
            throws BACnetServiceException {
        Encodable result = getProperty(pid);
        if (propertyArrayIndex == null)
            return result;

        if (!(result instanceof SequenceOf<?>))
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.propertyIsNotAnArray);

        SequenceOf<?> array = (SequenceOf<?>) result;
        int index = propertyArrayIndex.intValue();
        if (index == 0)
            return new UnsignedInteger(array.getCount());

        if (index > array.getCount())
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.invalidArrayIndex);

        return array.get(index);
    }

    public final Encodable getPropertyRequired(PropertyIdentifier pid, UnsignedInteger propertyArrayIndex)
            throws BACnetServiceException {
        Encodable p = getProperty(pid, propertyArrayIndex);
        if (p == null)
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.unknownProperty);
        return p;
    }

    //
    //
    // Set property
    //
    public BACnetObject writeProperty(PropertyIdentifier pid, Encodable value) {
        try {
            writeProperty(new PropertyValue(pid, value));
        }
        catch (BACnetServiceException e) {
            throw new BACnetRuntimeException(e);
        }
        return this;
    }

    public BACnetObject writeProperty(PropertyIdentifier pid, int indexBase1, Encodable value) {
        try {
            writeProperty(new PropertyValue(pid, new UnsignedInteger(indexBase1), value, null));
        }
        catch (BACnetServiceException e) {
            throw new BACnetRuntimeException(e);
        }
        return this;
    }

    /**
     * Entry point for writing a property via services. Provides validation and writing using mixins.
     * 
     * @param value
     * @throws BACnetServiceException
     */
    public void writeProperty(PropertyValue value) throws BACnetServiceException {
        PropertyIdentifier pid = value.getPropertyIdentifier();

        if (PropertyIdentifier.objectIdentifier.equals(pid))
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
        if (PropertyIdentifier.objectType.equals(pid))
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
        if (PropertyIdentifier.priorityArray.equals(pid))
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);

        // Validation - run through the mixins
        boolean handled = false;
        for (AbstractMixin mixin : mixins) {
            handled = mixin.validateProperty(value);
            if (handled)
                break;
        }
        if (!handled) {
            // Default behaviour is to validate against the object property definitions.
            PropertyTypeDefinition def = ObjectProperties.getPropertyTypeDefinitionRequired(objectType,
                    value.getPropertyIdentifier());
            if (value.getPropertyArrayIndex() == null) {
                // Expecting to write to a non-list property.
                //if (value.getValue() instanceof Null && !def.isOptional())
                //    throw new BACnetServiceException(ErrorClass.property, ErrorCode.invalidDataType,
                //            "Null provided, but the value is not optional");

                if (def.isSequence()) {
                    // Replacing an entire array. Validate each element of the given array.
                    @SuppressWarnings("unchecked")
                    SequenceOf<Encodable> seq = (SequenceOf<Encodable>) value.getValue();
                    for (Encodable e : seq) {
                        if (e == null || !def.getClazz().isAssignableFrom(e.getClass()))
                            throw new BACnetServiceException(ErrorClass.property, ErrorCode.invalidDataType,
                                    "expected " + def.getClazz() + ", received=" + (e == null ? "null" : e.getClass()));
                    }
                }
                else if (!def.getClazz().isAssignableFrom(value.getValue().getClass()))
                    // Validate the given data type.
                    throw new BACnetServiceException(ErrorClass.property, ErrorCode.invalidDataType,
                            "expected " + def.getClazz() + ", received=" + value.getValue().getClass());
            }
            else {
                // Expecting to write to an array element.
                if (!def.isSequence())
                    throw new BACnetServiceException(ErrorClass.property, ErrorCode.propertyIsNotAnArray);
                if (!def.getClazz().isAssignableFrom(value.getValue().getClass()))
                    throw new BACnetServiceException(ErrorClass.property, ErrorCode.invalidDataType);
            }
        }

        // Writing
        handled = false;
        for (AbstractMixin mixin : mixins) {
            handled = mixin.writeProperty(value);
            if (handled)
                break;
        }
        if (!handled) {
            // Default is to just set the property.
            if (value.getPropertyArrayIndex() != null) {
                // Set the value in a list or array.
                int indexBase1 = value.getPropertyArrayIndex().intValue();
                @SuppressWarnings("unchecked")
                SequenceOf<Encodable> list = (SequenceOf<Encodable>) properties.get(pid);

                if (value.getValue() instanceof Null) {
                    if (list != null) {
                        //Encodable oldValue = list.get(indexBase1);
                        list.remove(indexBase1);
                        //fireSubscriptions(pid, oldValue, null);
                    }
                }
                else {
                    if (list == null)
                        list = new SequenceOf<Encodable>();
                    list.set(indexBase1, value.getValue());
                    writePropertyImpl(pid, list);
                }
            }
            else {
                // Set the value of a property
                if (value.getValue() instanceof Null) {
                    properties.remove(pid);
                    //Encodable oldValue = properties.remove(pid);
                    //fireSubscriptions(pid, oldValue, null);
                }
                else
                    writePropertyImpl(pid, value.getValue());
            }
        }
    }

    /**
     * Entry point for changing a property circumventing mixin support. Used primarily for object configuration and
     * property writes from mixins themselves. Calls mixin "after write" methods and fires COV subscriptions.
     * 
     * @param pid
     * @param value
     * @return
     */
    public BACnetObject writePropertyImpl(PropertyIdentifier pid, Encodable value) {
        Encodable oldValue = properties.get(pid);
        properties.put(pid, value);

        // After writing.
        for (AbstractMixin mixin : mixins)
            mixin.afterWriteProperty(pid, oldValue, value);

        if (!Utils.equals(value, oldValue)) {
            // Notify listeners
            for (BACnetObjectListener l : listeners)
                l.propertyChange(pid, oldValue, value);
        }

        // Special handling to update the property list 
        if (oldValue == null && !PropertyIdentifier.propertyList.equals(pid))
            propertyListMixin.update();

        return this;
    }

    //
    //
    // Other
    //
    public void validate() throws BACnetServiceException {
        // Ensure that all required properties have values.
        List<PropertyTypeDefinition> defs = ObjectProperties.getRequiredPropertyTypeDefinitions(objectType);
        for (PropertyTypeDefinition def : defs) {
            if (getProperty(def.getPropertyIdentifier()) == null)
                throw new BACnetServiceException(ErrorClass.property, ErrorCode.other,
                        "Required property not set: " + def.getPropertyIdentifier());
        }
    }

    @Override
    public int hashCode() {
        ObjectIdentifier id = getId();
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        ObjectIdentifier id = getId();
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final BACnetObject other = (BACnetObject) obj;
        if (id == null) {
            if (other.getId() != null)
                return false;
        }
        else if (!id.equals(other.getId()))
            return false;
        return true;
    }
}
