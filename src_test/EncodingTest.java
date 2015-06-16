import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.confirmed.WritePropertyRequest;
import com.serotonin.bacnet4j.service.unconfirmed.WhoIsRequest;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.constructed.ActionCommand;
import com.serotonin.bacnet4j.type.constructed.ActionList;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class EncodingTest {
    public static void main(String[] args) throws Exception {
        //        send();
        encode();
    }

    static void send() throws Exception {
        LocalDevice localDevice = new LocalDevice(123, new DefaultTransport(new IpNetwork()));

        localDevice.initialize();

        try {
            localDevice.sendGlobalBroadcast(new WhoIsRequest());
            Thread.sleep(1500);

            RemoteDevice d = localDevice.getRemoteDevices().get(0);

            ActionCommand ac = new ActionCommand( //
                    new ObjectIdentifier(ObjectType.device, 234), //
                    new ObjectIdentifier(ObjectType.analogValue, 0), //
                    PropertyIdentifier.presentValue, //
                    null, new Real(3.14F), null, null, Boolean.FALSE, Boolean.FALSE);
            ActionList al = new ActionList(new SequenceOf<ActionCommand>(ac));
            WritePropertyRequest req = new WritePropertyRequest( //
                    new ObjectIdentifier(ObjectType.command, 0), //
                    PropertyIdentifier.action, new UnsignedInteger(0), al, null);
            localDevice.send(d, req);
        }
        finally {
            localDevice.terminate();
        }
    }

    static void encode() throws Exception {
        ActionCommand ac = new ActionCommand( //
                new ObjectIdentifier(ObjectType.device, 234), //
                new ObjectIdentifier(ObjectType.analogInput, 0), //
                PropertyIdentifier.presentValue, //
                null, /* new Real(3.14F) */new Null(), null, null, Boolean.FALSE, Boolean.FALSE);
        ActionList al = new ActionList(new SequenceOf<ActionCommand>(ac));
        WritePropertyRequest req = new WritePropertyRequest( //
                new ObjectIdentifier(ObjectType.command, 0), //
                PropertyIdentifier.action, new UnsignedInteger(1), al, null);

        ByteQueue queue = new ByteQueue();
        req.write(queue);

        System.out.println(queue);

        ConfirmedRequestService reqIn = ConfirmedRequestService.createConfirmedRequestService(
                WritePropertyRequest.TYPE_ID, queue);
        System.out.println(reqIn);
    }
}
