package com.serotonin.bacnet4j.type.constructed;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.enums.Month;
import com.serotonin.bacnet4j.type.primitive.Date;

public class DateRangeTest {
    @Test
    public void casesTest() {
        DateRange spec = new DateRange(new Date(-1, Month.NOVEMBER, -1, null), new Date(-1, Month.FEBRUARY, -1, null));

        assertEquals(false, spec.matches(new Date(2014, Month.OCTOBER, 30, null)));
        assertEquals(false, spec.matches(new Date(2014, Month.OCTOBER, 31, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.NOVEMBER, 1, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.NOVEMBER, 2, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.NOVEMBER, 30, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.DECEMBER, 1, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.JANUARY, 31, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.FEBRUARY, 1, null)));
        assertEquals(true, spec.matches(new Date(2014, Month.FEBRUARY, 28, null)));
        assertEquals(false, spec.matches(new Date(2014, Month.FEBRUARY, 29, null)));
        assertEquals(false, spec.matches(new Date(2014, Month.MARCH, 1, null)));
        assertEquals(false, spec.matches(new Date(2014, Month.MARCH, 2, null)));

        spec = new DateRange(new Date(2015, Month.APRIL, 5, null), new Date(2015, Month.APRIL, 5, null));

        assertEquals(false, spec.matches(new Date(2015, Month.APRIL, 3, null))); // false
        assertEquals(false, spec.matches(new Date(2015, Month.APRIL, 4, null))); // false
        assertEquals(true, spec.matches(new Date(2015, Month.APRIL, 5, null))); // true
        assertEquals(false, spec.matches(new Date(2015, Month.APRIL, 6, null))); // false
        assertEquals(false, spec.matches(new Date(2015, Month.APRIL, 7, null))); // false

        spec = new DateRange(new Date(-1, null, -1, null), new Date(-1, null, -1, null));

        assertEquals(true, spec.matches(new Date(2015, Month.APRIL, 5, null))); // true
    }

    @Test
    public void allMatchTest() {
        DateRange spec = new DateRange(Date.UNSPECIFIED, Date.UNSPECIFIED);
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return true;
            }
        });
    }

    @Test
    public void specificMatchTest() {
        DateRange spec = new DateRange(new Date(2014, Month.NOVEMBER, 13, null), new Date(2015, Month.MARCH, 2, null));
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                int year = gc.get(Calendar.YEAR);
                int month = gc.get(Calendar.MONTH);
                int day = gc.get(Calendar.DATE);

                if (year < 2014)
                    return false;
                if (year == 2014 && month < Calendar.NOVEMBER)
                    return false;
                if (year == 2014 && month == Calendar.NOVEMBER && day < 13)
                    return false;

                if (year > 2015)
                    return false;
                if (year == 2015 && month > Calendar.MARCH)
                    return false;
                if (year == 2015 && month == Calendar.MARCH && day > 2)
                    return false;

                return true;
            }
        });
    }

    @Test
    public void yearMatchTest() {
        DateRange spec = new DateRange(new Date(2010, null, -1, null), new Date(2015, null, -1, null));
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                int year = gc.get(Calendar.YEAR);
                return year >= 2010 && year <= 2015;
            }
        });
    }

    @Test
    public void monthMatchTest() {
        DateRange spec = new DateRange(new Date(-1, Month.NOVEMBER, -1, null), new Date(-1, Month.FEBRUARY, -1, null));
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                int month = gc.get(Calendar.MONTH);
                return month == Calendar.NOVEMBER || month == Calendar.DECEMBER || month == Calendar.JANUARY
                        || month == Calendar.FEBRUARY;
            }
        });
    }

    @Test
    public void oneDayMatchTest() {
        DateRange spec = new DateRange(new Date(2016, Month.NOVEMBER, 4, null),
                new Date(2016, Month.NOVEMBER, 4, null));
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                int year = gc.get(Calendar.YEAR);
                int month = gc.get(Calendar.MONTH);
                int day = gc.get(Calendar.DATE);
                return year == 2016 && month == Calendar.NOVEMBER && day == 4;
            }
        });
    }

    static interface Matcher {
        boolean match(GregorianCalendar gc);
    }

    // Tests run through about 245 years. Much bigger and we get a Y2K-type error when the year is 2155. (I.e. the 
    // year value hits 255.)
    // Note that the year 1900 is known to produce incorrect results, and is excluded from these tests.
    private static final int ITERATIONS = 92771;

    private void test(DateRange spec, Matcher matcher) {
        GregorianCalendar gc = new GregorianCalendar(1901, Calendar.JANUARY, 1, 12, 0);
        for (int i = 0; i < ITERATIONS; i++) {
            Date date = new Date(gc);
            boolean expected = matcher.match(gc);
            boolean match = spec.matches(date);
            if (expected != match)
                Assert.fail("Match failure on " + gc.getTime() + ", expected=" + expected + ", actual=" + match);
            gc.add(Calendar.DATE, 1);
        }
    }
}
