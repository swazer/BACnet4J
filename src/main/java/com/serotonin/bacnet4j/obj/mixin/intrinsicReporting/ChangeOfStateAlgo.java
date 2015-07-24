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
package com.serotonin.bacnet4j.obj.mixin.intrinsicReporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin.StateTransition;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.PropertyStates;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.notificationParameters.ChangeOfState;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

/**
 * Implements change of state event algorithm.
 * 
 * @author Matthew
 */
public class ChangeOfStateAlgo extends EventAlgorithm {
    static final Logger LOG = LoggerFactory.getLogger(ChangeOfStateAlgo.class);

    private final PropertyIdentifier monitoredValueProperty;
    private final PropertyIdentifier alarmValuesProperty;

    public ChangeOfStateAlgo(BACnetObject bo, PropertyIdentifier monitoredValueProperty,
            PropertyIdentifier alarmValuesProperty) {
        super(bo);

        this.monitoredValueProperty = monitoredValueProperty;
        this.alarmValuesProperty = alarmValuesProperty;
    }

    @Override
    protected StateTransition evaluateEventState() {
        EventState currentState = get(PropertyIdentifier.eventState);
        Encodable monitoredValue = get(monitoredValueProperty);
        Encodable alarmValues = get(alarmValuesProperty);
        UnsignedInteger timeDelay = get(PropertyIdentifier.timeDelay);
        UnsignedInteger timeDelayNormal = get(PropertyIdentifier.timeDelayNormal);
        if (timeDelayNormal == null)
            timeDelayNormal = timeDelay;

        LOG.debug("Current state: {}, monitored value: {}, alarm values: {}", currentState, monitoredValue, alarmValues);

        if (currentState.equals(EventState.normal) && isAlarmValue(monitoredValue, alarmValues))
            return new StateTransition(EventState.offnormal, timeDelay);

        if (currentState.isOffNormal() && !isAlarmValue(monitoredValue, alarmValues))
            return new StateTransition(EventState.normal, timeDelayNormal);

        // It appears that condition c) in 13.3.2 is unnecessary because the only off-normal state this event algorithm 
        // can transition to is off-normal.

        return null;
    }

    private boolean isAlarmValue(Encodable monitoredValue, Encodable alarmValue) {
        if (alarmValue instanceof SequenceOf) {
            @SuppressWarnings("unchecked")
            SequenceOf<Encodable> alarmValues = (SequenceOf<Encodable>) alarmValue;
            return alarmValues.contains(monitoredValue);
        }
        return alarmValue.equals(monitoredValue);
    }

    @Override
    protected EventType getEventType() {
        return EventType.changeOfState;
    }

    @Override
    protected NotificationParameters getEventValues(EventState fromState, EventState toState) {
        return new ChangeOfState(new PropertyStates(get(monitoredValueProperty)),
                (StatusFlags) get(PropertyIdentifier.statusFlags));
    }
}
