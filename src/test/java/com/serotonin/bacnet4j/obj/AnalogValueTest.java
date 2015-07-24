package com.serotonin.bacnet4j.obj;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.LimitEnable;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.notificationParameters.OutOfRange;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class AnalogValueTest extends AbstractTest {
    static final Logger LOG = LoggerFactory.getLogger(AnalogValueTest.class);

    @Override
    public void before() throws Exception {
        av = new AnalogValueObject(0, "av0", 50, EngineeringUnits.amperes, false);
        d1.addObject(av);

        nc = new NotificationClassObject(7, "nc7", 100, 5, 200, new EventTransitionBits(false, false, false));
        d1.addObject(nc);
    }

    AnalogValueObject av;
    NotificationClassObject nc;

    @SuppressWarnings("unchecked")
    @Test
    public void intrinsicReporting() throws Exception {
        SequenceOf<Destination> recipients = nc.get(PropertyIdentifier.recipientList);
        recipients.add(new Destination(new Recipient(rd2.getAddress()), new UnsignedInteger(10), new Boolean(true),
                new EventTransitionBits(true, true, true)));

        //        nc.addEventListener(new NotificationClassListener() {
        //            @Override
        //            public void event(ObjectIdentifier eventObjectIdentifier, TimeStamp timeStamp,
        //                    UnsignedInteger notificationClass, UnsignedInteger priority, EventType eventType,
        //                    CharacterString messageText, NotifyType notifyType, Boolean ackRequired, EventState fromState,
        //                    EventState toState, NotificationParameters eventValues) {
        //                LOG.info("eventObjectIdentifier={}, timeStamp={}, notificationClass={}, priority={}, " //
        //                        + "eventType={}, messageText={}, notifyType={}, ackRequired={}, fromState={}, toState={}, " //
        //                        + "eventValues={}", eventObjectIdentifier, timeStamp, notificationClass, priority, eventType,
        //                        messageText, notifyType, ackRequired, fromState, toState, eventValues);
        //
        //            }
        //        });

        // Create an event listener on d2 to catch the event notifications.
        EventNotifListener listener = new EventNotifListener();
        d2.getEventHandler().addListener(listener);

        av.supportIntrinsicReporting(1, 7, 100, 20, 5, new LimitEnable(true, true), new EventTransitionBits(true, true,
                true), NotifyType.alarm, 2);
        // Ensure that initializing the intrinsic reporting didn't fire any notifications.
        assertEquals(0, listener.notifs.size());

        // Write a different normal value.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(60));
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState)); // Still normal at this point.
        Thread.sleep(1100);
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState)); // Still normal at this point.
        // Ensure that no notifications are sent.
        assertEquals(0, listener.notifs.size());

        // Set an out of range value and then set back to normal before the time delay.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(110));
        Thread.sleep(500);
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState)); // Still normal at this point.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(90));
        Thread.sleep(600);
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState)); // Still normal at this point.

        // Do a real state change. Write an out of range value. After 1 seconds the alarm will be raised.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(10));
        Thread.sleep(500);
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState)); // Still normal at this point.
        Thread.sleep(600);
        assertEquals(EventState.lowLimit, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(new StatusFlags(true, false, false, false), av.getProperty(PropertyIdentifier.statusFlags));

        // Ensure that a proper looking event notification was received.
        assertEquals(1, listener.notifs.size());
        Map<String, Object> notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(10), notif.get("processIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(av.getId(), notif.get("eventObjectIdentifier"));
        assertEquals(
                ((BACnetArray<TimeStamp>) av.getProperty(PropertyIdentifier.eventTimeStamps)).get(EventState.offnormal
                        .getTransitionIndex()), notif.get("timeStamp"));
        assertEquals(new UnsignedInteger(7), notif.get("notificationClass"));
        assertEquals(new UnsignedInteger(100), notif.get("priority"));
        assertEquals(EventType.outOfRange, notif.get("eventType"));
        assertEquals(null, notif.get("messageText"));
        assertEquals(NotifyType.alarm, notif.get("notifyType"));
        assertEquals(new Boolean(false), notif.get("ackRequired"));
        assertEquals(EventState.normal, notif.get("fromState"));
        assertEquals(EventState.lowLimit, notif.get("toState"));
        assertEquals(
                new OutOfRange(new Real(10), new StatusFlags(true, false, false, false), new Real(5), new Real(20)),
                notif.get("eventValues"));

        // Disable low limit checking. Will return to normal immediately.
        av.writePropertyImpl(PropertyIdentifier.limitEnable, new LimitEnable(false, true));
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState));
        Thread.sleep(100);
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(10), notif.get("processIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(av.getId(), notif.get("eventObjectIdentifier"));
        assertEquals(
                ((BACnetArray<TimeStamp>) av.getProperty(PropertyIdentifier.eventTimeStamps)).get(EventState.normal
                        .getTransitionIndex()), notif.get("timeStamp"));
        assertEquals(new UnsignedInteger(7), notif.get("notificationClass"));
        assertEquals(new UnsignedInteger(200), notif.get("priority"));
        assertEquals(EventType.outOfRange, notif.get("eventType"));
        assertEquals(null, notif.get("messageText"));
        assertEquals(NotifyType.alarm, notif.get("notifyType"));
        assertEquals(new Boolean(false), notif.get("ackRequired"));
        assertEquals(EventState.lowLimit, notif.get("fromState"));
        assertEquals(EventState.normal, notif.get("toState"));
        assertEquals(new OutOfRange(new Real(10), new StatusFlags(false, false, false, false), new Real(5),
                new Real(20)), notif.get("eventValues"));

        // Re-enable low limit checking. Will return to low-limit after 1 second.
        av.writePropertyImpl(PropertyIdentifier.limitEnable, new LimitEnable(true, true));
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState));
        Thread.sleep(1100);
        assertEquals(EventState.lowLimit, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(EventType.outOfRange, notif.get("eventType"));
        assertEquals(EventState.normal, notif.get("fromState"));
        assertEquals(EventState.lowLimit, notif.get("toState"));
        assertEquals(
                new OutOfRange(new Real(10), new StatusFlags(true, false, false, false), new Real(5), new Real(20)),
                notif.get("eventValues"));

        // Go to a high limit. Will change to high-limit after 1 second.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(110));
        assertEquals(EventState.lowLimit, av.getProperty(PropertyIdentifier.eventState));
        Thread.sleep(1100);
        assertEquals(EventState.highLimit, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(EventState.lowLimit, notif.get("fromState"));
        assertEquals(EventState.highLimit, notif.get("toState"));
        assertEquals(new OutOfRange(new Real(110), new StatusFlags(true, false, false, false), new Real(5), new Real(
                100)), notif.get("eventValues"));

        // Reduce to within the deadband. No notification.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(95));
        assertEquals(EventState.highLimit, av.getProperty(PropertyIdentifier.eventState));
        Thread.sleep(1100);
        assertEquals(EventState.highLimit, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(0, listener.notifs.size());

        // Reduce to below the deadband. Return to normal after 2 seconds.
        av.writePropertyImpl(PropertyIdentifier.presentValue, new Real(94));
        assertEquals(EventState.highLimit, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(0, listener.notifs.size());
        Thread.sleep(1500);
        assertEquals(EventState.highLimit, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(0, listener.notifs.size());
        Thread.sleep(600);
        assertEquals(EventState.normal, av.getProperty(PropertyIdentifier.eventState));
        assertEquals(1, listener.notifs.size());
        notif = listener.notifs.remove(0);
        assertEquals(EventState.highLimit, notif.get("fromState"));
        assertEquals(EventState.normal, notif.get("toState"));
        assertEquals(new OutOfRange(new Real(94), new StatusFlags(false, false, false, false), new Real(5), new Real(
                100)), notif.get("eventValues"));
    }
}
