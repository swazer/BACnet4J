package com.serotonin.bacnet4j.discovery;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class WhoIs {
    public static void main(String[] args) throws Exception {
        IpNetwork network = new IpNetwork();
        Transport transport = new Transport(network);
        LocalDevice localDevice = new LocalDevice(45677, transport);
        localDevice.getEventHandler().addListener(new Listener());

        try {
            localDevice.initialize();

            //            RemoteDevice r = localDevice.findRemoteDevice(new Address(36, (byte) 1), new OctetString("89.101.141.54"),
            //                    1001);

            // CBM
            //            RemoteDevice r = localDevice.findRemoteDevice(new Address(36, (byte) 1), new OctetString("89.101.141.54"),
            //                    121);

            // CBT
            //            RemoteDevice r = localDevice.findRemoteDevice(new Address(36, (byte) 2), new OctetString("89.101.141.54"),
            //                    122);

            // CBR
            //            RemoteDevice r = localDevice.findRemoteDevice(new Address("89.101.141.54", IpNetwork.DEFAULT_PORT), null,
            //                    123);

            int count = 10;
            while (count-- > 0) {
                localDevice
                        .sendGlobalBroadcast(new WhoIsRequest(new UnsignedInteger(76058), new UnsignedInteger(76058)));
                Thread.sleep(3000);
            }

            //System.out.println(r);
        }
        finally {
            localDevice.terminate();
        }
    }

    static class Listener extends DeviceEventAdapter {
        @Override
        public void iAmReceived(RemoteDevice d) {
            System.out.println(d);
        }
    }
}
