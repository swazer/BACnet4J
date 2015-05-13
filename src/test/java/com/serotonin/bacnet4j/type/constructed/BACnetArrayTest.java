package com.serotonin.bacnet4j.type.constructed;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.type.primitive.CharacterString;

public class BACnetArrayTest {
    @Test
    public void arrayTest() {
        BACnetArray<CharacterString> arr = new BACnetArray<CharacterString>(3);
        assertEquals(3, arr.getCount());

        arr.set(1, new CharacterString("A"));
        arr.set(3, new CharacterString("C"));
        assertEquals(3, arr.getCount());
        assertEquals(arr.get(1), new CharacterString("A"));
        assertEquals(arr.get(2), null);
        assertEquals(arr.get(3), new CharacterString("C"));

        try {
            arr.remove(2);
            Assert.fail("Should have failed");
        }
        catch (BACnetRuntimeException e) {
            // no op
        }

        try {
            arr.add(new CharacterString("D"));
            Assert.fail("Should have failed");
        }
        catch (BACnetRuntimeException e) {
            // no op
        }
    }
}
