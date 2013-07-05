package com.serotonin.bacnet4j.test;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.AmbiguousValue;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.Primitive;

public class ScheduleTest {
    static LocalDevice localDevice;

    public static void main(String[] args) throws Exception {
        IpNetwork network = new IpNetwork();
        Transport transport = new Transport(network);
        localDevice = new LocalDevice(1234, transport);

        try {
            localDevice.initialize();
            run();
        }
        finally {
            localDevice.terminate();
        }
    }

    static void run() throws BACnetException {
        RemoteDevice brock = localDevice.findRemoteDevice(new Address("108.9.141.98", 0xbac0), null, 10000);
        getSchedule(brock, 1);
        getSchedule(brock, 2);
        getSchedule(brock, 3);

        RemoteDevice vico = localDevice.findRemoteDevice(new Address(2001, (byte) 58), new OctetString(
                "192.168.0.68:47808"), 76058);
        getSchedule(vico, 78);
    }

    static void getSchedule(RemoteDevice d, int id) throws BACnetException {
        ReadPropertyRequest req = new ReadPropertyRequest(new ObjectIdentifier(ObjectType.schedule, id),
                PropertyIdentifier.presentValue);
        ReadPropertyAck res = localDevice.send(d, req);
        AmbiguousValue av = (AmbiguousValue) res.getValue();
        Primitive primitive = av.convertTo(Primitive.class);
        System.out.println(primitive + " (" + primitive.getClass() + ")");
    }
}
