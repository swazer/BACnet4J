import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;

public class FindDevice {
    static LocalDevice localDevice;

    public static void main(String[] args) throws Exception {
        IpNetwork network = new IpNetwork();
        Transport transport = new Transport(network);
        //        transport.setTimeout(15000);
        //        transport.setSegTimeout(15000);
        localDevice = new LocalDevice(1234, transport);
        try {
            localDevice.initialize();

            Address address = new Address(50, new OctetString(new byte[] { 0, 0, (byte) 0xc3, 0x52 }));
            OctetString linkService = new OctetString("216.138.232.134");
            RemoteDevice d = localDevice.findRemoteDevice(address, linkService, 50002);
            System.out.println(d);

            //            Thread.sleep(200000);
        }
        finally {
            localDevice.terminate();
        }
    }
}
