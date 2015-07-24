package com.serotonin.bacnet4j.obj;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.enums.Month;
import com.serotonin.bacnet4j.service.confirmed.AddListElementRequest;
import com.serotonin.bacnet4j.service.confirmed.RemoveListElementRequest;
import com.serotonin.bacnet4j.type.constructed.CalendarEntry;
import com.serotonin.bacnet4j.type.constructed.DateRange;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.WeekNDay;
import com.serotonin.bacnet4j.type.constructed.WeekNDay.WeekOfMonth;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.util.TimeSource;

public class CalendarObjectTest extends AbstractTest {
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
    public void test() throws Exception {
        TestTimeSource ts = new TestTimeSource();
        ts.setTime(2115, Calendar.JANUARY, 1, 12, 0, 0);

        CalendarEntry ce = new CalendarEntry(new WeekNDay(Month.UNSPECIFIED, WeekOfMonth.days22to28,
                DayOfWeek.WEDNESDAY)); // The Wednesday during the 4th week of each month.
        SequenceOf<CalendarEntry> dateList = new SequenceOf<CalendarEntry>( //
                new CalendarEntry(new Date(-1, null, -1, DayOfWeek.FRIDAY)), // Every Friday.
                new CalendarEntry(new DateRange(new Date(-1, Month.NOVEMBER, -1, null), new Date(-1, Month.FEBRUARY,
                        -1, null))), // November to February
                ce);

        CalendarObject co = new CalendarObject(0, "cal0", dateList);
        co.setTimeSource(ts);
        d1.addObject(co);

        co.updatePresentValue(); // November to February
        Assert.assertEquals(Boolean.TRUE, co.get(PropertyIdentifier.presentValue));

        ts.setTime(2115, Calendar.MARCH, 2, 12, 0, 0);
        co.updatePresentValue();
        Assert.assertEquals(Boolean.FALSE, co.get(PropertyIdentifier.presentValue));

        ts.setTime(2115, Calendar.MARCH, 8, 12, 0, 0); // A Friday
        co.updatePresentValue();
        Assert.assertEquals(Boolean.TRUE, co.get(PropertyIdentifier.presentValue));

        ts.setTime(2115, Calendar.MAY, 27, 12, 0, 0);
        co.updatePresentValue();
        Assert.assertEquals(Boolean.FALSE, co.get(PropertyIdentifier.presentValue));

        ts.setTime(2115, Calendar.MAY, 22, 12, 0, 0); // The Wednesday during the 4th week of each month.
        co.updatePresentValue();
        Assert.assertEquals(Boolean.TRUE, co.get(PropertyIdentifier.presentValue));

        // Set the time source to a time that does not match the current date list, but 
        // will match a new entry.
        ts.setTime(2115, Calendar.JUNE, 17, 12, 0, 0);
        co.updatePresentValue(); // Uses the above time source.
        Assert.assertEquals(Boolean.FALSE, co.get(PropertyIdentifier.presentValue));

        CalendarEntry newEntry = new CalendarEntry(new Date(-1, Month.JUNE, -1, null));
        AddListElementRequest addReq = new AddListElementRequest(co.getId(), PropertyIdentifier.dateList, null,
                new SequenceOf<CalendarEntry>(newEntry));
        d2.send(rd1, addReq).get();
        Assert.assertEquals(Boolean.TRUE, co.get(PropertyIdentifier.presentValue));

        ts.setTime(2115, Calendar.JULY, 24, 12, 0, 0);
        co.updatePresentValue(); // Uses the above time source.
        Assert.assertEquals(Boolean.TRUE, co.get(PropertyIdentifier.presentValue));

        RemoveListElementRequest remReq = new RemoveListElementRequest(co.getId(), PropertyIdentifier.dateList, null,
                new SequenceOf<CalendarEntry>(ce));
        d2.send(rd1, remReq).get();
        Assert.assertEquals(Boolean.FALSE, co.get(PropertyIdentifier.presentValue));

        // Check that the compensatory time works.
        co.setTimeTolerance(1000 * 60 * 3);
        ts.setTime(2115, Calendar.AUGUST, 8, 23, 58, 0);
        co.updatePresentValue(); // Uses the above time source.
        Assert.assertEquals(Boolean.TRUE, co.get(PropertyIdentifier.presentValue));
    }
}
