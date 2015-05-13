package com.serotonin.bacnet4j.obj;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class MultistateValueTest extends AbstractTest {
    @Override
    public void before() throws Exception {
        // no op
    }

    @Test
    public void initialization() throws Exception {
        MultistateValueObject mv = new MultistateValueObject(0, "mv0", 7, null, 1, false);
        assertEquals(new StatusFlags(false, false, false, false), mv.get(PropertyIdentifier.statusFlags));
    }

    @Test
    public void stateText() throws Exception {
        try {
            new MultistateValueObject(0, "mv0", 7, new BACnetArray<CharacterString>(new CharacterString("a")), 1, true);
            fail("Should have thrown an exception");
        }
        catch (BACnetRuntimeException e) {
            BACnetServiceException e1 = (BACnetServiceException) e.getCause();
            assertEquals(ErrorClass.property, e1.getErrorClass());
            assertEquals(ErrorCode.inconsistentConfiguration, e1.getErrorCode());
        }

        MultistateValueObject mv = new MultistateValueObject(0, "mv0", 7, null, 1, true);
        d1.addObject(mv);

        try {
            mv.writeProperty(new PropertyValue(PropertyIdentifier.stateText, new BACnetArray<CharacterString>(
                    new CharacterString("a"))));
            fail("Should have thrown an exception");
        }
        catch (BACnetServiceException e) {
            assertEquals(ErrorClass.property, e.getErrorClass());
            assertEquals(ErrorCode.inconsistentConfiguration, e.getErrorCode());
        }

        mv.writeProperty(new PropertyValue(PropertyIdentifier.stateText, new BACnetArray<CharacterString>(
                new CharacterString("a"), new CharacterString("b"), new CharacterString("c"), new CharacterString("d"),
                new CharacterString("e"), new CharacterString("f"), new CharacterString("g"))));

        mv.writeProperty(new PropertyValue(PropertyIdentifier.numberOfStates, new UnsignedInteger(6)));
        assertEquals(
                new BACnetArray<CharacterString>(new CharacterString("a"), new CharacterString("b"),
                        new CharacterString("c"), new CharacterString("d"), new CharacterString("e"),
                        new CharacterString("f")), mv.get(PropertyIdentifier.stateText));

        mv.writeProperty(new PropertyValue(PropertyIdentifier.numberOfStates, new UnsignedInteger(8)));
        assertEquals(new BACnetArray<CharacterString>(new CharacterString("a"), new CharacterString("b"),
                new CharacterString("c"), new CharacterString("d"), new CharacterString("e"), new CharacterString("f"),
                new CharacterString(""), new CharacterString("")), mv.get(PropertyIdentifier.stateText));
    }
}
