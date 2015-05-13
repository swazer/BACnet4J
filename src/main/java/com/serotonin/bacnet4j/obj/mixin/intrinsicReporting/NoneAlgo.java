package com.serotonin.bacnet4j.obj.mixin.intrinsicReporting;

import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin.StateTransition;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;

/**
 * Implements change of state event algorithm.
 * 
 * @author Matthew
 */
public class NoneAlgo extends EventAlgorithm {
    public NoneAlgo(BACnetObject bo) {
        super(bo);
    }

    @Override
    protected StateTransition evaluateEventState() {
        return null;
    }

    @Override
    protected EventType getEventType() {
        return EventType.none;
    }

    @Override
    protected NotificationParameters getEventValues(EventState fromState, EventState toState) {
        return null;
    }
}
