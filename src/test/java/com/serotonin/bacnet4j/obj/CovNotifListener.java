package com.serotonin.bacnet4j.obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class CovNotifListener extends DeviceEventAdapter {
    static final Logger LOG = LoggerFactory.getLogger(CovNotifListener.class);

    public final List<Map<String, Object>> notifs = new ArrayList<Map<String, Object>>();

    @Override
    public void covNotificationReceived(UnsignedInteger subscriberProcessIdentifier, RemoteDevice initiatingDevice,
            ObjectIdentifier monitoredObjectIdentifier, UnsignedInteger timeRemaining,
            SequenceOf<PropertyValue> listOfValues) {
        LOG.info("COV notification received.");

        Map<String, Object> notif = new HashMap<String, Object>();
        notif.put("subscriberProcessIdentifier", subscriberProcessIdentifier);
        notif.put("initiatingDevice", initiatingDevice);
        notif.put("monitoredObjectIdentifier", monitoredObjectIdentifier);
        notif.put("timeRemaining", timeRemaining);
        notif.put("listOfValues", listOfValues);
        notifs.add(notif);
    }
}
