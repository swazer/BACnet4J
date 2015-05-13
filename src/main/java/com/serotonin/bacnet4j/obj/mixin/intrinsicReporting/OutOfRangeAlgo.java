package com.serotonin.bacnet4j.obj.mixin.intrinsicReporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin.StateTransition;
import com.serotonin.bacnet4j.type.constructed.LimitEnable;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.notificationParameters.OutOfRange;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class OutOfRangeAlgo extends EventAlgorithm {
    static final Logger LOG = LoggerFactory.getLogger(OutOfRangeAlgo.class);

    public OutOfRangeAlgo(BACnetObject bo) {
        super(bo);
    }

    @Override
    protected StateTransition evaluateEventState() {
        EventState currentState = get(PropertyIdentifier.eventState);
        float monitoredValue = ((Real) get(PropertyIdentifier.presentValue)).floatValue();
        float highLimit = ((Real) get(PropertyIdentifier.highLimit)).floatValue();
        float lowLimit = ((Real) get(PropertyIdentifier.lowLimit)).floatValue();
        float deadband = ((Real) get(PropertyIdentifier.deadband)).floatValue();
        LimitEnable limitEnable = get(PropertyIdentifier.limitEnable);
        UnsignedInteger timeDelay = get(PropertyIdentifier.timeDelay);
        UnsignedInteger timeDelayNormal = get(PropertyIdentifier.timeDelayNormal);
        if (timeDelayNormal == null)
            timeDelayNormal = timeDelay;

        LOG.debug("state={}, pv={}, highLimit={}, lowLimit={}, deadband={}", currentState, monitoredValue, highLimit,
                lowLimit, deadband);

        if (currentState.equals(EventState.normal) && limitEnable.isHighLimitEnable() && monitoredValue > highLimit)
            return new StateTransition(EventState.highLimit, timeDelay);

        if (currentState.equals(EventState.normal) && limitEnable.isLowLimitEnable() && monitoredValue < lowLimit)
            return new StateTransition(EventState.lowLimit, timeDelay);

        if (currentState.equals(EventState.highLimit) && !limitEnable.isHighLimitEnable())
            return new StateTransition(EventState.normal, null);

        if (currentState.equals(EventState.highLimit) && limitEnable.isLowLimitEnable() && monitoredValue < lowLimit)
            return new StateTransition(EventState.lowLimit, timeDelay);

        if (currentState.equals(EventState.highLimit) && monitoredValue < (highLimit - deadband))
            return new StateTransition(EventState.normal, timeDelayNormal);

        if (currentState.equals(EventState.lowLimit) && !limitEnable.isLowLimitEnable())
            return new StateTransition(EventState.normal, null);

        if (currentState.equals(EventState.lowLimit) && limitEnable.isHighLimitEnable() && monitoredValue > highLimit)
            return new StateTransition(EventState.highLimit, timeDelay);

        if (currentState.equals(EventState.lowLimit) && monitoredValue > (lowLimit + deadband))
            return new StateTransition(EventState.normal, timeDelayNormal);

        return null;
    }

    @Override
    protected EventType getEventType() {
        return EventType.outOfRange;
    }

    @Override
    protected NotificationParameters getEventValues(EventState fromState, EventState toState) {
        Real exceededLimit;
        if (EventState.lowLimit.equals(toState) //
                || (EventState.lowLimit.equals(fromState) && EventState.normal.equals(toState)))
            exceededLimit = get(PropertyIdentifier.lowLimit);
        else if (EventState.highLimit.equals(toState) //
                || (EventState.highLimit.equals(fromState) && EventState.normal.equals(toState)))
            exceededLimit = get(PropertyIdentifier.highLimit);
        else
            throw new BACnetRuntimeException("Invalid state transition: " + toState + " to " + fromState);

        LOG.debug("Notification parameters: from={}, to={}, exceededLimit={}", fromState, toState, exceededLimit);

        return new OutOfRange( //
                (Real) get(PropertyIdentifier.presentValue), //
                (StatusFlags) get(PropertyIdentifier.statusFlags), //
                (Real) get(PropertyIdentifier.deadband), //
                exceededLimit);
    }
}
