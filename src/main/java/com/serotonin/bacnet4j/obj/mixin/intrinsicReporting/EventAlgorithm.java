package com.serotonin.bacnet4j.obj.mixin.intrinsicReporting;

import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin.StateTransition;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;

abstract public class EventAlgorithm {
    private final BACnetObject bo;

    public EventAlgorithm(BACnetObject bo) {
        this.bo = bo;
    }

    @SuppressWarnings("unchecked")
    protected final <T extends Encodable> T get(PropertyIdentifier pid) {
        return (T) bo.get(pid);
    }

    abstract StateTransition evaluateEventState();

    abstract EventType getEventType();

    abstract NotificationParameters getEventValues(EventState fromState, EventState toState);
}
