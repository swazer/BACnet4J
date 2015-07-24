package com.serotonin.bacnet4j;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.npdu.test.TestNetwork;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyMultipleAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyMultipleRequest;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyMultipleRequest;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult;
import com.serotonin.bacnet4j.type.constructed.ReadAccessResult.Result;
import com.serotonin.bacnet4j.type.constructed.ReadAccessSpecification;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.WriteAccessSpecification;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.util.sero.ThreadUtils;

/**
 * Primarily this is a test of the DefaultTransport, but also tests aspects of Network and LocalDevice.
 * 
 * @author Matthew
 */
public class MessagingTest {
    @Test
    public void networkTest() throws Exception {
        TestNetwork network1 = new TestNetwork(1, 200);
        LocalDevice d1 = new LocalDevice(1, new DefaultTransport(network1));

        final MutableObject<RemoteDevice> o = new MutableObject<RemoteDevice>();
        d1.getEventHandler().addListener(new DeviceEventAdapter() {
            @Override
            public void iAmReceived(RemoteDevice d) {
                o.setValue(d);
            }
        });
        d1.initialize();

        Address a2 = new Address(new byte[] { 2 });
        TestNetwork network2 = new TestNetwork(a2, 200);
        LocalDevice d2 = new LocalDevice(2, new DefaultTransport(network2));
        d2.initialize();

        d1.sendLocalBroadcast(new WhoIsRequest());

        ThreadUtils.sleep(1000);

        d1.terminate();
        d2.terminate();

        Assert.assertNotNull(o.getValue());
        Assert.assertEquals(a2, o.getValue().getAddress());
    }

    @Test
    public void readRequest() throws Exception {
        // Create the first local device.
        TestNetwork network1 = new TestNetwork(1, 200);
        LocalDevice d1 = new LocalDevice(1, new DefaultTransport(network1));
        d1.initialize();

        // Create the second local device.
        Address a2 = new Address(new byte[] { 2 });
        TestNetwork network2 = new TestNetwork(a2, 200);
        LocalDevice d2 = new LocalDevice(2, new DefaultTransport(network2));
        ObjectIdentifier av0 = new ObjectIdentifier(ObjectType.analogValue, 0);
        d2.addObject(createAnalogValue(0));
        d2.initialize();

        // Create the remote proxy for device 2.
        RemoteDevice r2 = new RemoteDevice(2, a2);
        r2.setSegmentationSupported(Segmentation.segmentedBoth);
        ServicesSupported ss = new ServicesSupported();
        ss.setAll(true);
        r2.setServicesSupported(ss);
        r2.setMaxAPDULengthAccepted(MaxApduLength.UP_TO_1476.getMaxLength());

        // Send an object list request from the first to the second.
        List<ReadAccessSpecification> specs = new ArrayList<ReadAccessSpecification>();
        specs.add(new ReadAccessSpecification(new ObjectIdentifier(ObjectType.device, 2), PropertyIdentifier.objectList));
        ServiceFuture future = d1.send(r2, new ReadPropertyMultipleRequest(new SequenceOf<ReadAccessSpecification>(
                specs)));
        ReadPropertyMultipleAck ack = future.get();

        Assert.assertEquals(1, ack.getListOfReadAccessResults().getCount());
        ReadAccessResult readResult = ack.getListOfReadAccessResults().get(1);
        Assert.assertEquals(d2.getConfiguration().getId(), readResult.getObjectIdentifier());
        Assert.assertEquals(1, readResult.getListOfResults().getCount());
        Result result = readResult.getListOfResults().get(1);
        Assert.assertEquals(PropertyIdentifier.objectList, result.getPropertyIdentifier());
        @SuppressWarnings("unchecked")
        SequenceOf<ObjectIdentifier> idList = (SequenceOf<ObjectIdentifier>) result.getReadResult().getDatum();
        Assert.assertEquals(2, idList.getCount());
        Assert.assertEquals(d2.getConfiguration().getId(), idList.get(1));
        Assert.assertEquals(av0, idList.get(2));

        // Send the same request, but with a null consumer.
        d1.send(r2, new ReadPropertyMultipleRequest(new SequenceOf<ReadAccessSpecification>(specs)), null);
        // Give the request a moment to complete.
        ThreadUtils.sleep(200);

        d1.terminate();
        d2.terminate();
    }

    @Test
    public void segmentedResponse() throws Exception {
        // Create the first local device.
        TestNetwork network1 = new TestNetwork(1, 200);
        LocalDevice d1 = new LocalDevice(1, new DefaultTransport(network1));
        d1.initialize();

        // Create the second local device.
        Address a2 = new Address(new byte[] { 2 });
        TestNetwork network2 = new TestNetwork(a2, 200);
        LocalDevice d2 = new LocalDevice(2, new DefaultTransport(network2));
        for (int i = 0; i < 1000; i++)
            d2.addObject(createAnalogValue(i));

        d2.initialize();

        // Create the remote proxy for device 2.
        RemoteDevice r2 = new RemoteDevice(2, a2);
        r2.setSegmentationSupported(Segmentation.segmentedBoth);
        ServicesSupported ss = new ServicesSupported();
        ss.setAll(true);
        r2.setServicesSupported(ss);
        r2.setMaxAPDULengthAccepted(MaxApduLength.UP_TO_1476.getMaxLength());

        // Send an object list request from the first to the second.
        List<ReadAccessSpecification> specs = new ArrayList<ReadAccessSpecification>();
        specs.add(new ReadAccessSpecification(new ObjectIdentifier(ObjectType.device, 2), PropertyIdentifier.objectList));
        ServiceFuture future = d1.send(r2, new ReadPropertyMultipleRequest(new SequenceOf<ReadAccessSpecification>(
                specs)));
        ReadPropertyMultipleAck ack = future.get();

        Assert.assertEquals(1, ack.getListOfReadAccessResults().getCount());
        ReadAccessResult readResult = ack.getListOfReadAccessResults().get(1);
        Assert.assertEquals(d2.getConfiguration().getId(), readResult.getObjectIdentifier());
        Assert.assertEquals(1, readResult.getListOfResults().getCount());
        Result result = readResult.getListOfResults().get(1);
        Assert.assertEquals(PropertyIdentifier.objectList, result.getPropertyIdentifier());
        @SuppressWarnings("unchecked")
        SequenceOf<ObjectIdentifier> idList = (SequenceOf<ObjectIdentifier>) result.getReadResult().getDatum();
        Assert.assertEquals(1001, idList.getCount());
        Assert.assertEquals(d2.getConfiguration().getId(), idList.get(1));
        //        Assert.assertEquals(av0, idList.get(2));

        // Send the same request, but with a null consumer.
        d1.send(r2, new ReadPropertyMultipleRequest(new SequenceOf<ReadAccessSpecification>(specs)), null);
        // Give the request a moment to complete.
        ThreadUtils.sleep(200);

        d1.terminate();
        d2.terminate();
    }

    @Test
    public void writeRequest() throws Exception {
        // Create the first local device.
        TestNetwork network1 = new TestNetwork(1, 20);
        LocalDevice d1 = new LocalDevice(1, new DefaultTransport(network1));
        d1.initialize();

        // Create the second local device.
        Address a2 = new Address(new byte[] { 2 });
        TestNetwork network2 = new TestNetwork(a2, 30);
        LocalDevice d2 = new LocalDevice(2, new DefaultTransport(network2));
        ObjectIdentifier av0 = new ObjectIdentifier(ObjectType.analogValue, 0);
        d2.addObject(createAnalogValue(0));
        d2.initialize();

        // Create the remote proxy for device 2.
        RemoteDevice r2 = new RemoteDevice(2, a2);
        r2.setSegmentationSupported(Segmentation.segmentedBoth);
        ServicesSupported ss = new ServicesSupported();
        ss.setAll(true);
        r2.setServicesSupported(ss);
        r2.setMaxAPDULengthAccepted(MaxApduLength.UP_TO_1476.getMaxLength());

        // Send a write request from the first to the second.
        d1.send(r2, new WritePropertyRequest(av0, PropertyIdentifier.presentValue, null, new Real(3.14F), null));

        ServiceFuture future = d1.send(r2, new ReadPropertyRequest(av0, PropertyIdentifier.presentValue));
        ReadPropertyAck ack = future.get();

        Assert.assertEquals(av0, ack.getEventObjectIdentifier());
        Assert.assertEquals(null, ack.getPropertyArrayIndex());
        Assert.assertEquals(PropertyIdentifier.presentValue, ack.getPropertyIdentifier());
        Assert.assertEquals(new Real(3.14F), ack.getValue());

        // Send the same request, but with a null consumer.
        d1.send(r2, new ReadPropertyRequest(av0, PropertyIdentifier.presentValue), null);
        // Give the request a moment to complete.
        ThreadUtils.sleep(200);

        d1.terminate();
        d2.terminate();
    }

    @Test
    public void segmentedRequest() throws Exception {
        // Create the first local device.
        TestNetwork network1 = new TestNetwork(1, 20);
        LocalDevice d1 = new LocalDevice(1, new DefaultTransport(network1));
        d1.initialize();

        // Create the second local device.
        Address a2 = new Address(new byte[] { 2 });
        TestNetwork network2 = new TestNetwork(a2, 25);
        LocalDevice d2 = new LocalDevice(2, new DefaultTransport(network2));
        for (int i = 0; i < 1000; i++)
            d2.addObject(createAnalogValue(i));
        d2.initialize();

        // Create the remote proxy for device 2.
        RemoteDevice r2 = new RemoteDevice(2, a2);
        r2.setSegmentationSupported(Segmentation.segmentedBoth);
        ServicesSupported ss = new ServicesSupported();
        ss.setAll(true);
        r2.setServicesSupported(ss);
        r2.setMaxAPDULengthAccepted(MaxApduLength.UP_TO_1476.getMaxLength());

        List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
        propertyValues.add(new PropertyValue(PropertyIdentifier.presentValue, new Real(2.28F)));
        propertyValues.add(new PropertyValue(PropertyIdentifier.units, EngineeringUnits.btus));
        List<WriteAccessSpecification> specs = new ArrayList<WriteAccessSpecification>();
        for (int i = 0; i < 1000; i++)
            specs.add(new WriteAccessSpecification(new ObjectIdentifier(ObjectType.analogValue, i),
                    new SequenceOf<PropertyValue>(propertyValues)));
        d1.send(r2, new WritePropertyMultipleRequest(new SequenceOf<WriteAccessSpecification>(specs))).get();

        // Send the same request, but with a null consumer.
        d1.send(r2, new WritePropertyMultipleRequest(new SequenceOf<WriteAccessSpecification>(specs)), null);
        // Give the request a moment to complete.
        ThreadUtils.sleep(200);

        ReadPropertyAck ack = d1.send(r2,
                new ReadPropertyRequest(new ObjectIdentifier(ObjectType.analogValue, 567), PropertyIdentifier.units))
                .get();

        Assert.assertEquals(new ObjectIdentifier(ObjectType.analogValue, 567), ack.getEventObjectIdentifier());
        Assert.assertEquals(null, ack.getPropertyArrayIndex());
        Assert.assertEquals(PropertyIdentifier.units, ack.getPropertyIdentifier());
        Assert.assertEquals(EngineeringUnits.btus, ack.getValue());

        d1.terminate();
        d2.terminate();
    }

    private BACnetObject createAnalogValue(int id) {
        return new BACnetObject(ObjectType.analogValue, id) //
                .writeProperty(PropertyIdentifier.presentValue, new Real(3.14F)) //
                .writeProperty(PropertyIdentifier.units, EngineeringUnits.noUnits) //
                .writeProperty(PropertyIdentifier.outOfService, new Boolean(false)) //
                .writeProperty(PropertyIdentifier.eventState, EventState.normal) //
                .writeProperty(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, false)) //
        ;
    }
}
