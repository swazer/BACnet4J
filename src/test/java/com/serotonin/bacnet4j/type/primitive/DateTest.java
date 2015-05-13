package com.serotonin.bacnet4j.type.primitive;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.enums.Month;

public class DateTest {
    @Test
    public void comparisons() {
        Date date = new Date(2015, Month.APRIL, 15, null);

        assertTrue(date.after(new Date(2014, Month.JANUARY, 5, null)));
        assertTrue(date.after(new Date(2014, Month.JANUARY, 15, null)));
        assertTrue(date.after(new Date(2014, Month.JANUARY, 25, null)));
        assertTrue(date.after(new Date(2014, Month.APRIL, 5, null)));
        assertTrue(date.after(new Date(2014, Month.APRIL, 15, null)));
        assertTrue(date.after(new Date(2014, Month.APRIL, 25, null)));
        assertTrue(date.after(new Date(2014, Month.DECEMBER, 5, null)));
        assertTrue(date.after(new Date(2014, Month.DECEMBER, 15, null)));
        assertTrue(date.after(new Date(2014, Month.DECEMBER, 25, null)));
        assertTrue(date.after(new Date(2015, Month.FEBRUARY, 5, null)));
        assertTrue(date.after(new Date(2015, Month.FEBRUARY, 15, null)));
        assertTrue(date.after(new Date(2015, Month.FEBRUARY, 25, null)));
        assertTrue(date.after(new Date(2015, Month.APRIL, 5, null)));
        assertTrue(date.sameAs(new Date(2015, Month.APRIL, 15, null)));
        assertTrue(date.before(new Date(2015, Month.APRIL, 25, null)));
        assertTrue(date.before(new Date(2015, Month.JUNE, 5, null)));
        assertTrue(date.before(new Date(2015, Month.JUNE, 15, null)));
        assertTrue(date.before(new Date(2015, Month.JUNE, 25, null)));
        assertTrue(date.before(new Date(2016, Month.FEBRUARY, 5, null)));
        assertTrue(date.before(new Date(2016, Month.FEBRUARY, 15, null)));
        assertTrue(date.before(new Date(2016, Month.FEBRUARY, 25, null)));
        assertTrue(date.before(new Date(2016, Month.APRIL, 5, null)));
        assertTrue(date.before(new Date(2016, Month.APRIL, 15, null)));
        assertTrue(date.before(new Date(2016, Month.APRIL, 25, null)));
        assertTrue(date.before(new Date(2016, Month.JUNE, 5, null)));
        assertTrue(date.before(new Date(2016, Month.JUNE, 15, null)));
        assertTrue(date.before(new Date(2016, Month.JUNE, 25, null)));
        assertTrue(date.before(new Date(2016, Month.JUNE, 25, DayOfWeek.FRIDAY)));
    }

    @Test
    public void calculations1() {
        //
        // LeastMatchOnOrBefore
        //
        Date spec = new Date(-1, Month.NOVEMBER, -1, null);
        assertSameAs(new Date(2014, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrBefore( //
                new Date(2015, Month.JANUARY, 3, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrBefore( //
                new Date(2014, Month.DECEMBER, 1, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrBefore( //
                new Date(2014, Month.NOVEMBER, 7, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrBefore( //
                new Date(2014, Month.NOVEMBER, 1, null)));
        assertSameAs(new Date(2013, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrBefore( //
                new Date(2014, Month.OCTOBER, 31, null)));

        spec = new Date(-1, null, -1, null);
        assertSameAs(Date.MINIMUM_DATE, spec.calculateLeastMatchOnOrBefore(new Date(2015, Month.JANUARY, 3, null)));

        spec = new Date(2015, Month.JANUARY, 4, null);
        assertEquals(null, spec.calculateLeastMatchOnOrBefore(new Date(2015, Month.JANUARY, 3, null)));

        spec = new Date(2015, Month.JANUARY, 4, null);
        assertEquals(null, spec.calculateLeastMatchOnOrBefore(Date.MINIMUM_DATE));

        spec = new Date(-1, Month.JANUARY, -1, null);
        assertSameAs(Date.MINIMUM_DATE, Date.MINIMUM_DATE.calculateLeastMatchOnOrBefore(Date.MINIMUM_DATE));

        //
        // GreatestMatchOnOrBefore
        //
        spec = new Date(-1, Month.NOVEMBER, -1, null);
        assertSameAs(new Date(2014, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrBefore( //
                new Date(2015, Month.JANUARY, 3, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrBefore( //
                new Date(2014, Month.DECEMBER, 1, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrBefore( //
                new Date(2014, Month.NOVEMBER, 30, null)));
        assertSameAs(new Date(2013, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrBefore( //
                new Date(2014, Month.NOVEMBER, 29, null)));
        assertSameAs(new Date(2013, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrBefore( //
                new Date(2014, Month.NOVEMBER, 1, null)));
        assertSameAs(new Date(2013, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrBefore( //
                new Date(2014, Month.OCTOBER, 31, null)));

        spec = new Date(-1, null, -1, null);
        assertEquals(null, spec.calculateGreatestMatchOnOrBefore(new Date(2015, Month.JANUARY, 3, null)));

        //
        // LeastMatchOnOrAfter
        //
        spec = new Date(-1, Month.NOVEMBER, -1, null);
        assertSameAs(new Date(2015, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrAfter( //
                new Date(2015, Month.JANUARY, 3, null)));
        assertSameAs(new Date(2015, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrAfter( //
                new Date(2014, Month.DECEMBER, 1, null)));
        assertSameAs(new Date(2015, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrAfter( //
                new Date(2014, Month.NOVEMBER, 30, null)));
        assertSameAs(new Date(2015, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrAfter( //
                new Date(2014, Month.NOVEMBER, 29, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrAfter( //
                new Date(2014, Month.NOVEMBER, 1, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 1, null), spec.calculateLeastMatchOnOrAfter( //
                new Date(2014, Month.OCTOBER, 31, null)));

        spec = new Date(-1, null, -1, null);
        assertEquals(null, spec.calculateLeastMatchOnOrAfter(new Date(2015, Month.JANUARY, 3, null)));

        //
        // GreatestMatchOnOrAfter
        //
        spec = new Date(-1, Month.NOVEMBER, -1, null);
        assertSameAs(new Date(2015, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrAfter( //
                new Date(2015, Month.JANUARY, 3, null)));
        assertSameAs(new Date(2015, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrAfter( //
                new Date(2014, Month.DECEMBER, 1, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrAfter( //
                new Date(2014, Month.NOVEMBER, 30, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrAfter( //
                new Date(2014, Month.NOVEMBER, 29, null)));
        assertSameAs(new Date(2014, Month.NOVEMBER, 30, null), spec.calculateGreatestMatchOnOrAfter( //
                new Date(2014, Month.OCTOBER, 31, null)));

        spec = new Date(-1, null, -1, null);
        assertSameAs(Date.MAXIMUM_DATE, spec.calculateGreatestMatchOnOrAfter(new Date(2015, Month.JANUARY, 3, null)));
    }

    private void assertSameAs(Date expected, Date actual) {
        if (!expected.sameAs(actual))
            fail("Expected=" + expected + ", actual=" + actual);
    }

    @Test
    public void yearMatchTest() {
        Date spec = new Date(2015, Month.UNSPECIFIED, -1, DayOfWeek.UNSPECIFIED); // Just 2015
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.YEAR) == 2015;
            }
        });
    }

    @Test
    public void monthMatchTest() {
        Date spec = new Date(-1, Month.JUNE, -1, DayOfWeek.UNSPECIFIED); // Just June
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.MONTH) == Calendar.JUNE;
            }
        });
    }

    @Test
    public void dayMatchTest() {
        Date spec = new Date(-1, Month.UNSPECIFIED, 17, DayOfWeek.UNSPECIFIED); // The 17th of each month
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.DATE) == 17;
            }
        });
    }

    @Test
    public void dayOfWeekMatchTest() {
        Date spec = new Date(-1, Month.UNSPECIFIED, -1, DayOfWeek.FRIDAY); // Every friday
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
            }
        });
    }

    @Test
    public void mix1MatchTest() {
        Date spec = new Date(-1, Month.UNSPECIFIED, 32, DayOfWeek.MONDAY); // When the last day of the month is a Monday
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY //
                        && gc.get(Calendar.DATE) == gc.getActualMaximum(Calendar.DATE);
            }
        });
    }

    @Test
    public void mix2MatchTest() {
        Date spec = new Date(-1, Month.FEBRUARY, 29, DayOfWeek.UNSPECIFIED); // Leap days
        test(spec, new Matcher() {
            @Override
            public boolean match(GregorianCalendar gc) {
                return gc.get(Calendar.MONTH) == Calendar.FEBRUARY && gc.get(Calendar.DATE) == 29;
            }
        });
    }

    static interface Matcher {
        boolean match(GregorianCalendar gc);
    }

    // Tests run through about 246 years. Much bigger and we get a Y2K-type error when the year is 2155. (I.e. the 
    // year value hits 255.)
    private static final int ITERATIONS = 93136;

    private void test(Date spec, Matcher matcher) {
        GregorianCalendar gc = new GregorianCalendar(1900, Calendar.JANUARY, 1, 12, 0);
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
