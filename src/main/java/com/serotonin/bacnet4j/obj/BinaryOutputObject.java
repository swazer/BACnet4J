package com.serotonin.bacnet4j.obj;

import com.serotonin.bacnet4j.obj.mixin.CommandableMixin;
import com.serotonin.bacnet4j.obj.mixin.CovReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.HasStatusFlagsMixin;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.Polarity;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public class BinaryOutputObject extends BACnetObject {
    private static final long serialVersionUID = 8595160980372725336L;

    public BinaryOutputObject(int instanceNumber, String name, BinaryPV presentValue, boolean outOfService,
            Polarity polarity, BinaryPV relinquishDefault) {
        super(ObjectType.binaryOutput, instanceNumber, name);

        writePropertyImpl(PropertyIdentifier.eventState, EventState.normal);
        writePropertyImpl(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(
                outOfService));
        writePropertyImpl(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, outOfService));

        // Mixins
        addMixin(new HasStatusFlagsMixin(this));
        addMixin(new CommandableMixin(this));

        supportCommandable(relinquishDefault);

        writePropertyImpl(PropertyIdentifier.presentValue, presentValue);
        writePropertyImpl(PropertyIdentifier.polarity, polarity);
    }

    public void supportCovReporting() {
        supportCovReporting(CovReportingMixin.criteria13_1_4, null);
    }
}
