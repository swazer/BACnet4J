package com.serotonin.bacnet4j;

import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

/**
 * An internal (proprietary) mechanism for listening internal for event/alarm notifications via intrinsic
 * reporting. To use, implement this interface and add as a listener within a NotificationClassObject.
 * 
 * @author Matthew
 */
public interface NotificationClassListener {
    /**
     * Calls to this method are made in internal threads that should not be blocked. If blocking is required it is the
     * user code's responsibility to spawn a new thread in which to do so.
     */
    void event(ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp, UnsignedInteger notificationClass,
            UnsignedInteger priority, EventType eventType, CharacterString messageText, NotifyType notifyType,
            Boolean ackRequired, EventState fromState, EventState toState, NotificationParameters eventValues);
}
