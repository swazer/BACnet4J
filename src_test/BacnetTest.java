import java.util.List;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.event.DeviceEventAdapter;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.acknowledgement.ReadPropertyAck;
import com.serotonin.bacnet4j.service.confirmed.ReadPropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.RequestUtils;

public class BacnetTest {
    static LocalDevice localDevice;

    public static void main(String[] args) throws Exception {
        IpNetwork network = new IpNetwork();
        Transport transport = new DefaultTransport(network);
        //        transport.setTimeout(15000);
        //        transport.setSegTimeout(15000);
        localDevice = new LocalDevice(1234, transport);
        try {
            localDevice.initialize();
            localDevice.getEventHandler().addListener(new Listener());
            localDevice.sendGlobalBroadcast(new WhoIsRequest());

            Thread.sleep(200000);
        }
        finally {
            localDevice.terminate();
        }
    }

    static class Listener extends DeviceEventAdapter {
        @Override
        public void iAmReceived(final RemoteDevice d) {
            System.out.println("IAm received from " + d);
            System.out.println("Segmentation: " + d.getSegmentationSupported());
            d.setSegmentationSupported(Segmentation.noSegmentation);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        getExtendedDeviceInformation(d);
                        System.out.println("Done getting extended information");

                        List oids = ((SequenceOf) RequestUtils.sendReadPropertyAllowNull(localDevice, d,
                                d.getObjectIdentifier(), PropertyIdentifier.objectList)).getValues();
                        System.out.println(oids);
                    }
                    catch (BACnetException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    static void getExtendedDeviceInformation(RemoteDevice d) throws BACnetException {
        ObjectIdentifier oid = d.getObjectIdentifier();

        // Get the device's supported services
        System.out.println("protocolServicesSupported");
        ReadPropertyAck ack = (ReadPropertyAck) localDevice.send(d, new ReadPropertyRequest(oid,
                PropertyIdentifier.protocolServicesSupported));
        d.setServicesSupported((ServicesSupported) ack.getValue());

        System.out.println("objectName");
        ack = (ReadPropertyAck) localDevice.send(d, new ReadPropertyRequest(oid, PropertyIdentifier.objectName));
        d.setName(ack.getValue().toString());

        System.out.println("protocolVersion");
        ack = (ReadPropertyAck) localDevice.send(d, new ReadPropertyRequest(oid, PropertyIdentifier.protocolVersion));
        d.setProtocolVersion((UnsignedInteger) ack.getValue());

        //        System.out.println("protocolRevision");
        //        ack = (ReadPropertyAck) localDevice.send(d, new ReadPropertyRequest(oid, PropertyIdentifier.protocolRevision));
        //        d.setProtocolRevision((UnsignedInteger) ack.getValue());
    }
}
