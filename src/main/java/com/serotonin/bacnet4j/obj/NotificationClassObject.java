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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.bacnet4j.NotificationClassListener;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class NotificationClassObject extends BACnetObject {
    private static final long serialVersionUID = -3881883402727466774L;

    private final List<NotificationClassListener> eventListeners = new CopyOnWriteArrayList<NotificationClassListener>();

    public NotificationClassObject(int instanceNumber, String name, int toOffnormalPriority, int toFaultPriority,
            int toNormalPriority, EventTransitionBits ackRequired) {
        this(instanceNumber, name, new BACnetArray<UnsignedInteger>(new UnsignedInteger(toOffnormalPriority),
                new UnsignedInteger(toFaultPriority), new UnsignedInteger(toNormalPriority)), ackRequired);

        writePropertyImpl(PropertyIdentifier.recipientList, new SequenceOf<Destination>());
    }

    public NotificationClassObject(int instanceNumber, String name, BACnetArray<UnsignedInteger> priority,
            EventTransitionBits ackRequired) {
        super(ObjectType.notificationClass, instanceNumber, name);

        writePropertyImpl(PropertyIdentifier.notificationClass, new UnsignedInteger(instanceNumber));
        writePropertyImpl(PropertyIdentifier.priority, priority);
        writePropertyImpl(PropertyIdentifier.ackRequired, ackRequired);
    }

    public void addEventListener(NotificationClassListener l) {
        eventListeners.add(l);
    }

    public void removeEventListener(NotificationClassListener l) {
        eventListeners.remove(l);
    }

    public void fireEventNotification(ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp,
            UnsignedInteger notificationClass, UnsignedInteger priority, EventType eventType,
            CharacterString messageText, NotifyType notifyType, Boolean ackRequired, EventState fromState,
            EventState toState, NotificationParameters eventValues) {
        for (NotificationClassListener l : eventListeners)
            l.event(eventObjectIdentifier, timeStamp, notificationClass, priority, eventType, messageText, notifyType,
                    ackRequired, fromState, toState, eventValues);
    }
}
