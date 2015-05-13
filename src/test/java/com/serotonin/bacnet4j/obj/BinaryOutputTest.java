package com.serotonin.bacnet4j.obj;

import static com.serotonin.bacnet4j.type.enumerated.BinaryPV.active;
import static com.serotonin.bacnet4j.type.enumerated.BinaryPV.inactive;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.presentValue;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.priorityArray;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.PriorityArray;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.Polarity;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;

public class BinaryOutputTest extends AbstractTest {
    static final Logger LOG = LoggerFactory.getLogger(BinaryOutputTest.class);

    BinaryOutputObject obj;

    @Override
    public void before() throws Exception {
        obj = new BinaryOutputObject(0, "boName1", BinaryPV.inactive, true, Polarity.normal, BinaryPV.inactive);
        obj.addListener(new BACnetObjectListener() {
            @Override
            public void propertyChange(PropertyIdentifier pid, Encodable oldValue, Encodable newValue) {
                LOG.debug("{} changed from {} to {}", pid, oldValue, newValue);
            }
        });
        d1.addObject(obj);
    }

    @Test
    public void initialization() throws Exception {
        new BinaryOutputObject(0, "boName1", BinaryPV.inactive, false, Polarity.normal, BinaryPV.inactive);
    }

    @Test
    public void annexI() throws Exception {
        obj.writePropertyImpl(PropertyIdentifier.minimumOnTime, new UnsignedInteger(1)); // 2 seconds
        obj.writePropertyImpl(PropertyIdentifier.minimumOffTime, new UnsignedInteger(2)); // 4 seconds
        obj.writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(false));

        PriorityArray pa = obj.getProperty(priorityArray);

        // See Annex I for a description of this process.
        // a)
        LOG.debug("a");
        assertEquals(new PriorityArray(), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // b) starts min on for 2s
        LOG.debug("b");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, active, 9);
        assertEquals(new PriorityArray().put(6, active).put(9, active), pa);
        assertEquals(active, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // c)
        LOG.debug("c");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, inactive, 7);
        assertEquals(new PriorityArray().put(6, active).put(7, inactive).put(9, active), pa);
        assertEquals(active, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // d)
        LOG.debug("d");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, new Null(), 9);
        assertEquals(new PriorityArray().put(6, active).put(7, inactive), pa);
        assertEquals(active, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // e), f) Wait for the timer to expire. Starts min off timer for 4s
        Thread.sleep(1100);
        LOG.debug("e,f");
        assertEquals(new PriorityArray().put(6, inactive).put(7, inactive), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Going off on our own now...
        // Write inactive into 10, and relinquish 7
        LOG.debug("A");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, inactive, 10);
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, new Null(), 7);
        assertEquals(new PriorityArray().put(6, inactive).put(10, inactive), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Wait for the timer to expire. 
        Thread.sleep(2100);
        LOG.debug("B");
        assertEquals(new PriorityArray().put(10, inactive), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Relinquish at 10. No timer should be active, and the array should be empty.
        LOG.debug("C");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, new Null(), 10);
        assertEquals(new PriorityArray(), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Write active to 9. Starts min on timer for 2s
        LOG.debug("D");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, active, 9);
        assertEquals(new PriorityArray().put(6, active).put(9, active), pa);
        assertEquals(active, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Write inactive to 5. Cancels current timer and starts new off timer for 4s
        LOG.debug("E");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, inactive, 5);
        assertEquals(new PriorityArray().put(5, inactive).put(6, inactive).put(9, active), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Relinquish at 5. Timer remains active.
        Thread.sleep(1500);
        LOG.debug("F");
        RequestUtils.writeProperty(d2, rd1, obj.getId(), presentValue, new Null(), 5);
        assertEquals(new PriorityArray().put(6, inactive).put(9, active), pa);
        assertEquals(inactive, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Wait for the timer to expire. Starts min on timer for 2s
        Thread.sleep(600);
        LOG.debug("G");
        assertEquals(new PriorityArray().put(6, active).put(9, active), pa);
        assertEquals(active, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));

        // Wait for the timer to expire.
        Thread.sleep(1100);
        LOG.debug("H");
        assertEquals(new PriorityArray().put(9, active), pa);
        assertEquals(active, RequestUtils.getProperty(d2, rd1, obj.getId(), presentValue));
    }
}
