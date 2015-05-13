package com.serotonin.bacnet4j.obj.mixin.intrinsicReporting;

import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;

abstract public class FaultAlgorithm {
    private final BACnetObject bo;

    public FaultAlgorithm(BACnetObject bo) {
        this.bo = bo;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Encodable> T get(PropertyIdentifier pid) {
        return (T) bo.get(pid);
    }

    abstract Reliability evaluate(Encodable oldMonitoredValue, Encodable newMonitoredValue);
}
