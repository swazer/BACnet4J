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
package com.serotonin.bacnet4j.type.constructed;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.AccessCredentialDisable;
import com.serotonin.bacnet4j.type.enumerated.AccessCredentialDisableReason;
import com.serotonin.bacnet4j.type.enumerated.AccessEvent;
import com.serotonin.bacnet4j.type.enumerated.AccessZoneOccupancyState;
import com.serotonin.bacnet4j.type.enumerated.Action;
import com.serotonin.bacnet4j.type.enumerated.AuthenticationStatus;
import com.serotonin.bacnet4j.type.enumerated.BackupState;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.DeviceStatus;
import com.serotonin.bacnet4j.type.enumerated.DoorAlarmState;
import com.serotonin.bacnet4j.type.enumerated.DoorSecuredStatus;
import com.serotonin.bacnet4j.type.enumerated.DoorStatus;
import com.serotonin.bacnet4j.type.enumerated.DoorValue;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.FileAccessMethod;
import com.serotonin.bacnet4j.type.enumerated.LifeSafetyMode;
import com.serotonin.bacnet4j.type.enumerated.LifeSafetyOperation;
import com.serotonin.bacnet4j.type.enumerated.LifeSafetyState;
import com.serotonin.bacnet4j.type.enumerated.LightingInProgress;
import com.serotonin.bacnet4j.type.enumerated.LightingOperation;
import com.serotonin.bacnet4j.type.enumerated.LightingTransition;
import com.serotonin.bacnet4j.type.enumerated.LockStatus;
import com.serotonin.bacnet4j.type.enumerated.Maintenance;
import com.serotonin.bacnet4j.type.enumerated.NodeType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.Polarity;
import com.serotonin.bacnet4j.type.enumerated.ProgramError;
import com.serotonin.bacnet4j.type.enumerated.ProgramRequest;
import com.serotonin.bacnet4j.type.enumerated.ProgramState;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.enumerated.RestartReason;
import com.serotonin.bacnet4j.type.enumerated.SecurityLevel;
import com.serotonin.bacnet4j.type.enumerated.ShedState;
import com.serotonin.bacnet4j.type.enumerated.SilencedState;
import com.serotonin.bacnet4j.type.enumerated.WriteStatus;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class PropertyStates extends BaseType {
    private static final long serialVersionUID = 1112998027203005048L;
    private static List<Class<? extends Encodable>> classes;
    static {
        classes = new ArrayList<Class<? extends Encodable>>();
        classes.add(Boolean.class); // 0
        classes.add(BinaryPV.class); // 1
        classes.add(EventType.class); // 2
        classes.add(Polarity.class); // 3
        classes.add(ProgramRequest.class); // 4;
        classes.add(ProgramState.class); // 5
        classes.add(ProgramError.class); // 6
        classes.add(Reliability.class); // 7
        classes.add(EventState.class); // 8
        classes.add(DeviceStatus.class); // 9
        classes.add(EngineeringUnits.class); // 10
        classes.add(UnsignedInteger.class); // 11
        classes.add(LifeSafetyMode.class); // 12
        classes.add(LifeSafetyState.class); // 13
        classes.add(RestartReason.class); // 14
        classes.add(DoorAlarmState.class); // 15
        classes.add(Action.class); // 16
        classes.add(DoorSecuredStatus.class); // 17
        classes.add(DoorStatus.class); // 18
        classes.add(DoorValue.class); // 19
        classes.add(FileAccessMethod.class); // 20
        classes.add(LockStatus.class); // 21
        classes.add(LifeSafetyOperation.class); // 22
        classes.add(Maintenance.class); // 23
        classes.add(NodeType.class); // 24
        classes.add(NotifyType.class); // 25
        classes.add(SecurityLevel.class); // 26
        classes.add(ShedState.class); // 27
        classes.add(SilencedState.class); // 28
        classes.add(Encodable.class); // 29 - reserved
        classes.add(AccessEvent.class); // 30
        classes.add(AccessZoneOccupancyState.class); // 31
        classes.add(AccessCredentialDisableReason.class); // 32
        classes.add(AccessCredentialDisable.class); // 33
        classes.add(AuthenticationStatus.class); // 34
        classes.add(Encodable.class); // 35 - undefined
        classes.add(BackupState.class); // 36
        classes.add(WriteStatus.class); // 37
        classes.add(LightingInProgress.class); // 38
        classes.add(LightingOperation.class); // 39
        classes.add(LightingTransition.class); // 40
    }

    private final Choice state;

    public PropertyStates(Encodable state) {
        int type = -1;
        for (int i = 0; i < classes.size(); i++) {
            if (classes.get(i) == state.getClass()) {
                type = i;
                break;
            }
        }

        if (type == -1)
            throw new BACnetRuntimeException("Unhandled property type: " + state.getClass());

        this.state = new Choice(type, state);
    }

    public int getType() {
        return state.getContextId();
    }

    @SuppressWarnings("unchecked")
    public <T extends Encodable> T getState() {
        return (T) state.getDatum();
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, state);
    }

    public PropertyStates(ByteQueue queue) throws BACnetException {
        state = new Choice(queue, classes);
    }

    @Override
    public String toString() {
        return "PropertyStates [state=" + state + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PropertyStates other = (PropertyStates) obj;
        if (state == null) {
            if (other.state != null)
                return false;
        }
        else if (!state.equals(other.state))
            return false;
        return true;
    }
}
