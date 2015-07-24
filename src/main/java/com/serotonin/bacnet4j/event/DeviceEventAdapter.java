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
package com.serotonin.bacnet4j.event;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.RemoteObject;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.confirmed.ReinitializeDeviceRequest.ReinitializedStateOfDevice;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.Choice;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.Sequence;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

/**
 * A default class for easy implementation of the DeviceEventListener interface. Instead of having to implement all of
 * the defined methods, listener classes can override this and only implement the desired methods.
 * 
 * @author Matthew Lohbihler
 */
public class DeviceEventAdapter implements DeviceEventListener {
    @Override
    public void listenerException(Throwable e) {
        // Override as required
        e.printStackTrace();
    }

    @Override
    public boolean allowPropertyWrite(final Address from, BACnetObject obj, PropertyValue pv) {
        return true;
    }

    @Override
    public void iAmReceived(final RemoteDevice d) {
        // Override as required
    }

    @Override
    public void propertyWritten(final Address from, final BACnetObject obj, final PropertyValue pv) {
        // Override as required
    }

    @Override
    public void iHaveReceived(final RemoteDevice d, final RemoteObject o) {
        // Override as required
    }

    @Override
    public void covNotificationReceived(final UnsignedInteger subscriberProcessIdentifier,
            final RemoteDevice initiatingDevice, final ObjectIdentifier monitoredObjectIdentifier,
            final UnsignedInteger timeRemaining, final SequenceOf<PropertyValue> listOfValues) {
        // Override as required
    }

    @Override
    public void eventNotificationReceived(final UnsignedInteger processIdentifier, final RemoteDevice initiatingDevice,
            final ObjectIdentifier eventObjectIdentifier, final TimeStamp timeStamp,
            final UnsignedInteger notificationClass, final UnsignedInteger priority, final EventType eventType,
            final CharacterString messageText, final NotifyType notifyType,
            final com.serotonin.bacnet4j.type.primitive.Boolean ackRequired, final EventState fromState,
            final EventState toState, final NotificationParameters eventValues) {
        // Override as required
    }

    @Override
    public void textMessageReceived(final RemoteDevice textMessageSourceDevice, final Choice messageClass,
            final MessagePriority messagePriority, final CharacterString message) {
        // Override as required
    }

    @Override
    public void privateTransferReceived(final Address from, final UnsignedInteger vendorId,
            final UnsignedInteger serviceNumber, final Sequence serviceParameters) {
        // Override as required
    }

    @Override
    public void reinitializeDevice(final Address from, final ReinitializedStateOfDevice reinitializedStateOfDevice) {
        // Override as required
    }

    @Override
    public void synchronizeTime(final Address from, final DateTime dateTime, final boolean utc) {
        // Override as required
    }
}
