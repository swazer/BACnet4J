package com.serotonin.bacnet4j.obj;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.enums.Month;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.CalendarEntry;
import com.serotonin.bacnet4j.type.constructed.DailySchedule;
import com.serotonin.bacnet4j.type.constructed.DateRange;
import com.serotonin.bacnet4j.type.constructed.Destination;
import com.serotonin.bacnet4j.type.constructed.DeviceObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.Recipient;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.SpecialEvent;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.constructed.TimeValue;
import com.serotonin.bacnet4j.type.constructed.WeekNDay;
import com.serotonin.bacnet4j.type.constructed.WeekNDay.WeekOfMonth;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.notificationParameters.ChangeOfReliability;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.Time;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.TimeSource;

public class ScheduleObjectTest extends AbstractTest {
    @Override
    public void before() throws Exception {
        // no op
    }

    class TestTimeSource implements TimeSource {
        GregorianCalendar gc;

        void setTime(int year, int month, int day, int hour, int min, int sec) {
            gc = new GregorianCalendar(year, month, day, hour, min, sec);
        }

        @Override
        public long currentTimeMillis() {
            return gc.getTimeInMillis();
        }
    }

    @Test
    public void fullTest() throws Exception {
        // Not really a full test. The effective period could be better.

        TestTimeSource ts = new TestTimeSource();
        ts.setTime(2115, Calendar.MAY, 1, 12, 0, 0);

        AnalogValueObject av0 = new AnalogValueObject(0, "av0", 98, EngineeringUnits.amperes, false);
        av0.supportCommandable(new Real(-2));
        d2.addObject(av0);

        AnalogValueObject av1 = new AnalogValueObject(1, "av1", 99, EngineeringUnits.amperesPerMeter, false);
        av1.supportCommandable(new Real(-1));
        d1.addObject(av1);

        SequenceOf<CalendarEntry> dateList = new SequenceOf<CalendarEntry>( //
                new CalendarEntry(new Date(-1, null, -1, DayOfWeek.FRIDAY)), // Every Friday.
                new CalendarEntry(new DateRange(new Date(-1, Month.NOVEMBER, -1, null), new Date(-1, Month.FEBRUARY,
                        -1, null))), // November to February
                new CalendarEntry(new WeekNDay(Month.UNSPECIFIED, WeekOfMonth.days22to28, DayOfWeek.WEDNESDAY)) // The Wednesday during the 4th week of each month.
        );

        CalendarObject co = new CalendarObject(0, "cal0", dateList);
        co.setTimeSource(ts);
        d1.addObject(co);

        DateRange effectivePeriod = new DateRange(Date.UNSPECIFIED, Date.UNSPECIFIED);
        BACnetArray<DailySchedule> weeklySchedule = new BACnetArray<DailySchedule>( //
                new DailySchedule(new SequenceOf<TimeValue>(new TimeValue(new Time(8, 0, 0, 0), new Real(10)),
                        new TimeValue(new Time(17, 0, 0, 0), new Real(11)))), //
                new DailySchedule(new SequenceOf<TimeValue>(new TimeValue(new Time(8, 0, 0, 0), new Real(12)),
                        new TimeValue(new Time(17, 0, 0, 0), new Real(13)))), //
                new DailySchedule(new SequenceOf<TimeValue>(new TimeValue(new Time(8, 0, 0, 0), new Real(14)),
                        new TimeValue(new Time(17, 0, 0, 0), new Real(15)))), //
                new DailySchedule(new SequenceOf<TimeValue>(new TimeValue(new Time(9, 0, 0, 0), new Real(16)),
                        new TimeValue(new Time(20, 0, 0, 0), new Real(17)))), //
                new DailySchedule(new SequenceOf<TimeValue>(new TimeValue(new Time(9, 0, 0, 0), new Real(18)),
                        new TimeValue(new Time(21, 30, 0, 0), new Real(19)))), //
                new DailySchedule(new SequenceOf<TimeValue>()), //
                new DailySchedule(new SequenceOf<TimeValue>()));
        SequenceOf<SpecialEvent> exceptionSchedule = new SequenceOf<SpecialEvent>( //
                new SpecialEvent(co.getId(), new SequenceOf<TimeValue>(
                        new TimeValue(new Time(8, 0, 0, 0), new Real(20)), new TimeValue(new Time(22, 0, 0, 0),
                                new Real(21))), new UnsignedInteger(10)), // Calendar
                new SpecialEvent(co.getId(), new SequenceOf<TimeValue>(new TimeValue(new Time(13, 0, 0, 0),
                        new Real(22)), new TimeValue(new Time(14, 0, 0, 0), new Real(23))), new UnsignedInteger(7)), // Calendar
                new SpecialEvent(new CalendarEntry(new Date(-1, null, 8, DayOfWeek.WEDNESDAY)),
                        new SequenceOf<TimeValue>(new TimeValue(new Time(10, 30, 0, 0), new Real(24)), new TimeValue(
                                new Time(17, 0, 0, 0), new Real(25))), new UnsignedInteger(6)) // 7th is a Wednesday
        );
        SequenceOf<DeviceObjectPropertyReference> listOfObjectPropertyReferences = new SequenceOf<DeviceObjectPropertyReference>( //
                new DeviceObjectPropertyReference(av0.getId(), PropertyIdentifier.presentValue, null,
                        rd2.getObjectIdentifier()), //
                new DeviceObjectPropertyReference(av1.getId(), PropertyIdentifier.presentValue, null, null) //
        );

        ScheduleObject<Real> so = new ScheduleObject<Real>(0, "sch0", effectivePeriod, weeklySchedule,
                exceptionSchedule, new Real(8), listOfObjectPropertyReferences, 12, false);
        so.setTimeSource(ts);

        d1.addObject(so);
        Thread.sleep(100); // Let the requests be received.
        Assert.assertEquals(new Real(14), so.get(PropertyIdentifier.presentValue));
        Assert.assertEquals(new Real(14), av0.get(PropertyIdentifier.presentValue));
        Assert.assertEquals(new Real(14), av1.get(PropertyIdentifier.presentValue));

        // Start actual tests.
        testTime(ts, so, av0, av1, Calendar.MAY, 1, 17, 0, 15);
        testTime(ts, so, av0, av1, Calendar.MAY, 2, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 2, 9, 0, 16);
        testTime(ts, so, av0, av1, Calendar.MAY, 2, 20, 0, 17);
        testTime(ts, so, av0, av1, Calendar.MAY, 3, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 3, 13, 0, 22);
        testTime(ts, so, av0, av1, Calendar.MAY, 3, 14, 0, 23);
        testTime(ts, so, av0, av1, Calendar.MAY, 4, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 5, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 6, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 6, 8, 0, 10);
        testTime(ts, so, av0, av1, Calendar.MAY, 6, 17, 0, 11);
        testTime(ts, so, av0, av1, Calendar.MAY, 7, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 7, 8, 0, 12);
        testTime(ts, so, av0, av1, Calendar.MAY, 7, 17, 0, 13);
        testTime(ts, so, av0, av1, Calendar.MAY, 8, 0, 0, 8);
        testTime(ts, so, av0, av1, Calendar.MAY, 8, 10, 30, 24);
        testTime(ts, so, av0, av1, Calendar.MAY, 8, 17, 0, 25);
        testTime(ts, so, av0, av1, Calendar.MAY, 9, 0, 0, 8);
    }

    private void testTime(TestTimeSource ts, ScheduleObject<Real> so, AnalogValueObject av0, AnalogValueObject av1,
            int month, int day, int hour, int min, float expectedValue) throws Exception {
        ts.setTime(2115, month, day, hour, min, 0);
        so.updatePresentValue();
        Thread.sleep(100); // Let the requests be received.
        Assert.assertEquals(new Real(expectedValue), so.get(PropertyIdentifier.presentValue));
        Assert.assertEquals(new Real(expectedValue), av0.get(PropertyIdentifier.presentValue));
        Assert.assertEquals(new Real(expectedValue), av1.get(PropertyIdentifier.presentValue));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void intrinsicAlarms() throws Exception {
        NotificationClassObject nc = new NotificationClassObject(7, "nc7", 100, 5, 200, new EventTransitionBits(false,
                false, false));
        d1.addObject(nc);

        SequenceOf<Destination> recipients = nc.get(PropertyIdentifier.recipientList);
        recipients.add(new Destination(new Recipient(rd2.getAddress()), new UnsignedInteger(10), new Boolean(true),
                new EventTransitionBits(true, true, true)));

        // Create an event listener on d2 to catch the event notifications.
        EventNotifListener listener = new EventNotifListener();
        d2.getEventHandler().addListener(listener);

        AnalogValueObject av1 = new AnalogValueObject(1, "av1", 99, EngineeringUnits.amperesPerMeter, false);
        av1.supportCommandable(new Real(-1));
        d1.addObject(av1);

        SequenceOf<SpecialEvent> exceptionSchedule = new SequenceOf<SpecialEvent>( //
                new SpecialEvent(new CalendarEntry(new Date(-1, null, -1, DayOfWeek.WEDNESDAY)),
                        new SequenceOf<TimeValue>(), new UnsignedInteger(6)) // Wednesdays
        );
        SequenceOf<DeviceObjectPropertyReference> listOfObjectPropertyReferences = new SequenceOf<DeviceObjectPropertyReference>( //
                new DeviceObjectPropertyReference(av1.getId(), PropertyIdentifier.presentValue, null, null) //
        );
        ScheduleObject<Real> so = new ScheduleObject<Real>(0, "sch0",
                new DateRange(Date.UNSPECIFIED, Date.UNSPECIFIED), null, exceptionSchedule, new Real(8),
                listOfObjectPropertyReferences, 12, false);
        d1.addObject(so);
        so.supportIntrinsicReporting(7, new EventTransitionBits(true, true, true), NotifyType.alarm);

        // Ensure that initializing the intrinsic reporting didn't fire any notifications.
        assertEquals(0, listener.notifs.size());

        // Write a fault reliability value.
        so.writePropertyImpl(PropertyIdentifier.reliability, Reliability.memberFault);
        assertEquals(EventState.fault, so.getProperty(PropertyIdentifier.eventState));
        Thread.sleep(100);
        // Ensure that a proper looking event notification was received.
        assertEquals(1, listener.notifs.size());
        Map<String, Object> notif = listener.notifs.remove(0);
        assertEquals(new UnsignedInteger(10), notif.get("processIdentifier"));
        assertEquals(rd1, notif.get("initiatingDevice"));
        assertEquals(so.getId(), notif.get("eventObjectIdentifier"));
        assertEquals(((BACnetArray<TimeStamp>) so.getProperty(PropertyIdentifier.eventTimeStamps)).get(EventState.fault
                .getTransitionIndex()), notif.get("timeStamp"));
        assertEquals(new UnsignedInteger(7), notif.get("notificationClass"));
        assertEquals(new UnsignedInteger(5), notif.get("priority"));
        assertEquals(EventType.changeOfReliability, notif.get("eventType"));
        assertEquals(null, notif.get("messageText"));
        assertEquals(NotifyType.alarm, notif.get("notifyType"));
        assertEquals(new Boolean(false), notif.get("ackRequired"));
        assertEquals(EventState.normal, notif.get("fromState"));
        assertEquals(EventState.fault, notif.get("toState"));
        assertEquals(new ChangeOfReliability(Reliability.memberFault, new StatusFlags(true, true, false, false),
                new SequenceOf<PropertyValue>()), notif.get("eventValues"));
    }
}
