package com.serotonin.bacnet4j.type.constructed;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.enums.Month;
import com.serotonin.bacnet4j.type.constructed.WeekNDay.WeekOfMonth;
import com.serotonin.bacnet4j.type.primitive.Date;

public class WeekNDayTest {
    @Test
    public void monthMatchTest() {
        WeekNDay spec = new WeekNDay(Month.JUNE, WeekOfMonth.any, DayOfWeek.UNSPECIFIED);
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.MONTH) == Calendar.JUNE;
            }
        });
    }

    @Test
    public void weekOfMonthMatchTest() {
        WeekNDay spec = new WeekNDay(Month.UNSPECIFIED, WeekOfMonth.last7Days, DayOfWeek.UNSPECIFIED);
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                int day = gc.get(Calendar.DATE);
                int lastDay = gc.getActualMaximum(Calendar.DATE);
                return day >= (lastDay - 6) && day <= lastDay;
            }
        });
    }

    @Test
    public void dayOfWeekMatchTest() {
        WeekNDay spec = new WeekNDay(Month.UNSPECIFIED, WeekOfMonth.any, DayOfWeek.THURSDAY);
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY;
            }
        });
    }

    @Test
    public void mixMatchTest() {
        WeekNDay spec = new WeekNDay(Month.JUNE, WeekOfMonth.days1to7, DayOfWeek.SUNDAY);
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                if (gc.get(Calendar.MONTH) != Calendar.JUNE)
                    return false;
                if (gc.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY)
                    return false;
                int day = gc.get(Calendar.DATE);
                return day >= 1 && day <= 7;
            }
        });
    }

    static interface Matcher {
        boolean match(GregorianCalendar gc);
    }

    // Tests run through about 246 years. Much bigger and we get a Y2K-type error when the year is 2155. (I.e. the 
    // year value hits 255.)
    private static final int ITERATIONS = 93136;

    private void test(WeekNDay spec, Matcher matcher) {
        GregorianCalendar gc = new GregorianCalendar(1900, Calendar.JANUARY, 1, 12, 0);
        for (int i = 0; i < ITERATIONS; i++) {
            Date date = new Date(gc);
            boolean expected = matcher.match(gc);
            boolean match = spec.matches(date);
            if (expected != match)
                fail("Match failure on " + gc.getTime() + ", expected=" + expected + ", actual=" + match);
            gc.add(Calendar.DATE, 1);
        }
    }
}
