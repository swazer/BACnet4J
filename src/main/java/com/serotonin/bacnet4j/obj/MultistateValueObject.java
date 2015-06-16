package com.serotonin.bacnet4j.obj;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.obj.mixin.CommandableMixin;
import com.serotonin.bacnet4j.obj.mixin.CovReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.HasStatusFlagsMixin;
import com.serotonin.bacnet4j.obj.mixin.MultistateMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.ChangeOfStateAlgo;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.FaultStateAlgo;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class MultistateValueObject extends BACnetObject {
    private static final long serialVersionUID = -5335002026964225708L;

    public MultistateValueObject(int instanceNumber, String name, int numberOfStates,
            BACnetArray<CharacterString> stateText, int presentValue, boolean outOfService) {
        super(ObjectType.multiStateValue, instanceNumber, name);

        if (numberOfStates < 1)
            throw new BACnetRuntimeException("numberOfStates cannot be less than 1");

        writePropertyImpl(PropertyIdentifier.eventState, EventState.normal);
        writeProperty(PropertyIdentifier.presentValue, new UnsignedInteger(presentValue));
        writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(true));
        writePropertyImpl(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, true));

        // Mixins
        addMixin(new HasStatusFlagsMixin(this));
        addMixin(new CommandableMixin(this));
        addMixin(new MultistateMixin(this));

        writePropertyImpl(PropertyIdentifier.numberOfStates, new UnsignedInteger(numberOfStates));
        if (stateText != null)
            writeProperty(PropertyIdentifier.stateText, stateText);
        writeProperty(PropertyIdentifier.presentValue, new UnsignedInteger(presentValue));
        if (!outOfService)
            writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(outOfService));
    }

    public void supportIntrinsicReporting(int timeDelay, int notificationClass,
            SequenceOf<UnsignedInteger> alarmValues, SequenceOf<UnsignedInteger> faultValues,
            EventTransitionBits eventEnable, NotifyType notifyType, int timeDelayNormal) {
        // Prepare the object with all of the properties that intrinsic reporting will need.
        // User-defined properties
        writePropertyImpl(PropertyIdentifier.timeDelay, new UnsignedInteger(timeDelay));
        writePropertyImpl(PropertyIdentifier.notificationClass, new UnsignedInteger(notificationClass));
        writePropertyImpl(PropertyIdentifier.alarmValues, alarmValues);
        if (faultValues != null)
            writePropertyImpl(PropertyIdentifier.faultValues, faultValues);
        writePropertyImpl(PropertyIdentifier.eventEnable, eventEnable);
        writePropertyImpl(PropertyIdentifier.notifyType, notifyType);
        writePropertyImpl(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(timeDelayNormal));

        // Now add the mixin.
        ChangeOfStateAlgo eventAlgo = new ChangeOfStateAlgo(this, PropertyIdentifier.presentValue,
                PropertyIdentifier.alarmValues);
        FaultStateAlgo faultAlgo = new FaultStateAlgo(this, PropertyIdentifier.reliability,
                PropertyIdentifier.faultValues);
        addMixin(new IntrinsicReportingMixin(this, eventAlgo, faultAlgo,
                new PropertyIdentifier[] { PropertyIdentifier.presentValue },
                new PropertyIdentifier[] { PropertyIdentifier.presentValue }));
    }

    public void supportCovReporting() {
        supportCovReporting(CovReportingMixin.criteria13_1_4, null);
    }
}
