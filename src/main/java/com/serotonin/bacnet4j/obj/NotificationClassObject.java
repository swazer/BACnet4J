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
