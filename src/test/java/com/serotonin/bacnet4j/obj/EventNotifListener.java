package com.serotonin.bacnet4j.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.notificationParameters.NotificationParameters;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class EventNotifListener extends DeviceEventAdapter {
    static final Logger LOG = LoggerFactory.getLogger(EventNotifListener.class);

    public final List<Map<String, Object>> notifs = new ArrayList<Map<String, Object>>();

    @Override
    public void eventNotificationReceived(UnsignedInteger processIdentifier, RemoteDevice initiatingDevice,
            ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp, UnsignedInteger notificationClass,
            UnsignedInteger priority, EventType eventType, CharacterString messageText, NotifyType notifyType,
            Boolean ackRequired, EventState fromState, EventState toState, NotificationParameters eventValues) {
        LOG.info("Event notification received.");

        Map<String, Object> notif = new HashMap<String, Object>();
        notif.put("processIdentifier", processIdentifier);
        notif.put("initiatingDevice", initiatingDevice);
        notif.put("eventObjectIdentifier", eventObjectIdentifier);
        notif.put("timeStamp", timeStamp);
        notif.put("notificationClass", notificationClass);
        notif.put("priority", priority);
        notif.put("eventType", eventType);
        notif.put("messageText", messageText);
        notif.put("notifyType", notifyType);
        notif.put("ackRequired", ackRequired);
        notif.put("fromState", fromState);
        notif.put("toState", toState);
        notif.put("eventValues", eventValues);
        notifs.add(notif);
    }
}
