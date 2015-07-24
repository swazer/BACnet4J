package com.serotonin.bacnet4j.type.primitive;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimeTest {
    @Test
    public void comparison() {
        assertFalse(new Time(12, 1, 1, 1).before(new Time(11, 2, 2, 2)));
        assertFalse(new Time(12, 1, 1, 1).before(new Time(12, 0, 2, 2)));
        assertFalse(new Time(12, 1, 1, 1).before(new Time(12, 1, 0, 2)));
        assertFalse(new Time(12, 1, 1, 1).before(new Time(12, 1, 1, 1)));
        assertTrue(new Time(12, 1, 1, 1).before(new Time(12, 1, 1, 2)));
        assertTrue(new Time(12, 1, 1, 1).before(new Time(12, 1, 2, 0)));
        assertTrue(new Time(12, 1, 1, 1).before(new Time(12, 2, 0, 0)));
        assertTrue(new Time(12, 1, 1, 1).before(new Time(13, 0, 0, 0)));
    }
}
