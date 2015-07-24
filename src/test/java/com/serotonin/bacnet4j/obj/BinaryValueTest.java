package com.serotonin.bacnet4j.obj;

import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.eventState;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.inactiveText;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectName;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.outOfService;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.presentValue;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.priorityArray;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.propertyList;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.reliability;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.relinquishDefault;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.statusFlags;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.exception.ErrorAPDUException;
import com.serotonin.bacnet4j.type.constructed.PriorityArray;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.util.RequestUtils;

public class BinaryValueTest extends AbstractTest {
    BinaryValueObject bv;

    @Override
    public void before() throws Exception {
        bv = new BinaryValueObject(0, "bvName1", BinaryPV.inactive, true);
        d1.addObject(bv);
    }

    @Test
    public void initialization() throws Exception {
        new BinaryValueObject(0, "bvName1", BinaryPV.inactive, false);
    }

    @Test
    public void name() throws Exception {
        // Ensure that the object has the given name.
        CharacterString name = RequestUtils.getProperty(d2, rd1, bv.getId(), objectName);
        Assert.assertEquals("bvName1", name.getValue());

        // Ensure that the name cannot be changed remotely.
        RequestUtils.writeProperty(d2, rd1, bv.getId(), objectName, new CharacterString("goAheadSetThis"));
        name = RequestUtils.getProperty(d2, rd1, bv.getId(), objectName);
        assertEquals("goAheadSetThis", name.getValue());

        // Ensure that the name can be changed locally.
        bv.writePropertyImpl(objectName, new CharacterString("nowThis"));
        name = RequestUtils.getProperty(d2, rd1, bv.getId(), objectName);
        assertEquals("nowThis", name.getValue());
    }

    @Test
    public void statusFlags() throws Exception {
        StatusFlags statusFlags = RequestUtils.getProperty(d2, rd1, bv.getId(), PropertyIdentifier.statusFlags);
        assertEquals(false, statusFlags.isInAlarm());
        assertEquals(false, statusFlags.isFault());
        assertEquals(false, statusFlags.isOverridden());
        assertEquals(true, statusFlags.isOutOfService());

        // Change the overridden value.
        bv.setOverridden(true);
        statusFlags = RequestUtils.getProperty(d2, rd1, bv.getId(), PropertyIdentifier.statusFlags);
        assertEquals(false, statusFlags.isInAlarm());
        assertEquals(false, statusFlags.isFault());
        assertEquals(true, statusFlags.isOverridden());
        assertEquals(true, statusFlags.isOutOfService());

        // Change the reliability value.
        bv.writeProperty(reliability, Reliability.communicationFailure);
        statusFlags = RequestUtils.getProperty(d2, rd1, bv.getId(), PropertyIdentifier.statusFlags);
        assertEquals(false, statusFlags.isInAlarm());
        assertEquals(true, statusFlags.isFault());
        assertEquals(true, statusFlags.isOverridden());
        assertEquals(true, statusFlags.isOutOfService());
    }

    @Test
    public void propertyList() throws Exception {
        SequenceOf<PropertyIdentifier> pids = RequestUtils.getProperty(d2, rd1, bv.getId(), propertyList);
        assertEquals(4, pids.getCount());
        assertTrue(pids.contains(presentValue));
        assertTrue(pids.contains(statusFlags));
        assertTrue(pids.contains(eventState));
        assertTrue(pids.contains(outOfService));

        bv.writeProperty(inactiveText, new CharacterString("someText"));
        pids = RequestUtils.getProperty(d2, rd1, bv.getId(), propertyList);
        assertEquals(5, pids.getCount());
        assertTrue(pids.contains(presentValue));
        assertTrue(pids.contains(statusFlags));
        assertTrue(pids.contains(eventState));
        assertTrue(pids.contains(outOfService));
        assertTrue(pids.contains(inactiveText));
    }

    @Test
    public void presentValue() throws Exception {
        // The non-commandable tests

        // Default value is inactive
        BinaryPV pv = RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue);
        assertEquals(BinaryPV.inactive, pv);

        // When overridden, the present value is not settable
        bv.setOverridden(true);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
        try {
            RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
            assertTrue("Should have gotten an error APDU", false);
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.property, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.writeAccessDenied, e.getBACnetError().getErrorCode());
        }

        // ... even when it is out of service.
        bv.setOverridden(true);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(true));
        try {
            RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
            assertTrue("Should have gotten an error APDU", false);
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.property, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.writeAccessDenied, e.getBACnetError().getErrorCode());
        }

        // When not overridden, the present value is not settable while not out of service.
        bv.setOverridden(false);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
        try {
            RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
            assertTrue("Should have gotten an error APDU", false);
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.property, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.writeAccessDenied, e.getBACnetError().getErrorCode());
        }

        // ... but it is when the object is out of service.
        bv.setOverridden(false);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(true));
        RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
        pv = RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue);
        assertEquals(BinaryPV.active, pv);
    }

    @Test
    public void commandablePresentValue() throws Exception {
        // The commandable tests
        bv.supportCommandable(BinaryPV.inactive);

        // Default value is inactive
        BinaryPV pv = RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue);
        assertEquals(BinaryPV.inactive, pv);

        // When overridden, the present value is not settable
        bv.setOverridden(true);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));
        try {
            RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
            assertTrue("Should have gotten an error APDU", false);
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.property, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.writeAccessDenied, e.getBACnetError().getErrorCode());
        }

        // ... even when it is out of service.
        bv.setOverridden(true);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(true));
        try {
            RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
            assertTrue("Should have gotten an error APDU", false);
        }
        catch (ErrorAPDUException e) {
            assertEquals(ErrorClass.property, e.getBACnetError().getErrorClass());
            assertEquals(ErrorCode.writeAccessDenied, e.getBACnetError().getErrorCode());
        }

        // When not overridden, the present value is writable while not out of service.
        bv.setOverridden(false);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(true));
        RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active);
        pv = RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue);
        assertEquals(BinaryPV.active, pv);
        assertEquals(BinaryPV.active, bv.properties.get(presentValue));

        // When not overridden and in service, the present value is commandable
        bv.setOverridden(false);
        bv.writePropertyImpl(outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(false));

        // Set a value at priority 16.
        RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.inactive);
        // Ensure the priority array looks right.
        assertEquals(new PriorityArray().put(16, BinaryPV.inactive), bv.getProperty(priorityArray));
        // Ensure the present value looks right.
        assertEquals(BinaryPV.inactive, bv.getProperty(presentValue));
        // Ensure the present value looks right when read via service.
        assertEquals(BinaryPV.inactive, RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue));

        // Set a value at priority 15.
        RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, BinaryPV.active, 15);
        // Ensure the priority array looks right.
        assertEquals(new PriorityArray().put(15, BinaryPV.active).put(16, BinaryPV.inactive),
                bv.getProperty(priorityArray));
        // Ensure the present value looks right.
        assertEquals(BinaryPV.active, bv.getProperty(presentValue));
        // Ensure the present value looks right when read via service.
        assertEquals(BinaryPV.active, RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue));

        // Relinquish at 16
        RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, new Null());
        // Ensure the priority array looks right.
        assertEquals(new PriorityArray().put(15, BinaryPV.active), bv.getProperty(priorityArray));
        // Ensure the present value looks right.
        assertEquals(BinaryPV.active, bv.getProperty(presentValue));
        // Ensure the present value looks right when read via service.
        assertEquals(BinaryPV.active, RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue));

        // Relinquish at priority 15.
        RequestUtils.writeProperty(d2, rd1, bv.getId(), presentValue, new Null(), 15);
        // Ensure the priority array looks right.
        assertEquals(new PriorityArray(), bv.getProperty(priorityArray));
        // Ensure the present value looks right.
        assertEquals(BinaryPV.inactive, bv.getProperty(presentValue));
        // Ensure the present value looks right when read via service.
        assertEquals(BinaryPV.inactive, RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue));

        // Change the relinquish default
        RequestUtils.writeProperty(d2, rd1, bv.getId(), relinquishDefault, BinaryPV.active);
        // Ensure the relinquish default looks right.
        assertEquals(BinaryPV.active, bv.getProperty(relinquishDefault));
        // Ensure the present value looks right.
        assertEquals(BinaryPV.active, bv.getProperty(presentValue));
        // Ensure the present value looks right when read via service.
        assertEquals(BinaryPV.active, RequestUtils.getProperty(d2, rd1, bv.getId(), presentValue));
    }
}
