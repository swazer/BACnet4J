package com.serotonin.bacnet4j.npdu;

import org.junit.Assert;
import org.junit.Test;

import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.apdu.ConfirmedRequest;
import com.serotonin.bacnet4j.apdu.UnconfirmedRequest;
import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.enums.MaxSegments;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedEventNotificationRequest;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedTextMessageRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedEventNotificationRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedPrivateTransferRequest;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedTextMessageRequest;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.DateTime;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.constructed.TimeStamp;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.EventType;
import com.serotonin.bacnet4j.type.enumerated.MessagePriority;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.notificationParameters.ChangeOfBitString;
import com.serotonin.bacnet4j.type.primitive.BitString;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class NetworkPriorityTest {
    @Test
    public void test() throws Exception {
        Assert.assertEquals(3, getNetworkPriorityCEN(5));
        Assert.assertEquals(3, getNetworkPriorityCEN(63));
        Assert.assertEquals(2, getNetworkPriorityCEN(64));
        Assert.assertEquals(2, getNetworkPriorityCEN(127));
        Assert.assertEquals(1, getNetworkPriorityCEN(128));
        Assert.assertEquals(1, getNetworkPriorityCEN(191));
        Assert.assertEquals(0, getNetworkPriorityCEN(192));

        Assert.assertEquals(3, getNetworkPriorityUEN(5));
        Assert.assertEquals(3, getNetworkPriorityUEN(63));
        Assert.assertEquals(2, getNetworkPriorityUEN(64));
        Assert.assertEquals(2, getNetworkPriorityUEN(127));
        Assert.assertEquals(1, getNetworkPriorityUEN(128));
        Assert.assertEquals(1, getNetworkPriorityUEN(191));
        Assert.assertEquals(0, getNetworkPriorityUEN(192));

        Assert.assertEquals(1, getNetworkPriorityCTM(MessagePriority.urgent));
        Assert.assertEquals(0, getNetworkPriorityCTM(MessagePriority.normal));

        Assert.assertEquals(1, getNetworkPriorityUTM(MessagePriority.urgent));
        Assert.assertEquals(0, getNetworkPriorityUTM(MessagePriority.normal));

        Assert.assertEquals(0, getNetworkPriorityOther());
    }

    private int getNetworkPriorityCEN(int eventPriority) throws Exception {
        ConfirmedEventNotificationRequest req = new ConfirmedEventNotificationRequest(new UnsignedInteger(2),
                new ObjectIdentifier(ObjectType.device, 8), new ObjectIdentifier(ObjectType.analogInput, 9),
                new TimeStamp(new DateTime()), new UnsignedInteger(3), new UnsignedInteger(eventPriority),
                EventType.changeOfBitstring, new CharacterString("hi"), NotifyType.event, new Boolean(false),
                EventState.normal, EventState.offnormal, new ChangeOfBitString(new BitString(new boolean[] { false,
                        true, false, true }), new StatusFlags(true, false, false, false)));
        ConfirmedRequest apdu = new ConfirmedRequest(false, false, true, MaxSegments.MORE_THAN_64,
                MaxApduLength.UP_TO_1476, (byte) 45, 0, 5, req);
        return getNetworkPriority(apdu);
    }

    private int getNetworkPriorityUEN(int eventPriority) throws Exception {
        UnconfirmedEventNotificationRequest req = new UnconfirmedEventNotificationRequest(new UnsignedInteger(2),
                new ObjectIdentifier(ObjectType.device, 8), new ObjectIdentifier(ObjectType.analogInput, 9),
                new TimeStamp(new DateTime()), new UnsignedInteger(3), new UnsignedInteger(eventPriority),
                EventType.changeOfBitstring, new CharacterString("hi"), NotifyType.event, new Boolean(false),
                EventState.normal, EventState.offnormal, new ChangeOfBitString(new BitString(new boolean[] { false,
                        true, false, true }), new StatusFlags(true, false, false, false)));

        UnconfirmedRequest apdu = new UnconfirmedRequest(req);

        return getNetworkPriority(apdu);
    }

    private int getNetworkPriorityCTM(MessagePriority priority) throws Exception {
        ConfirmedTextMessageRequest req = new ConfirmedTextMessageRequest(new ObjectIdentifier(ObjectType.device, 8),
                priority, new CharacterString("hi"));
        ConfirmedRequest apdu = new ConfirmedRequest(false, false, true, MaxSegments.MORE_THAN_64,
                MaxApduLength.UP_TO_1476, (byte) 45, 0, 5, req);
        return getNetworkPriority(apdu);
    }

    private int getNetworkPriorityUTM(MessagePriority priority) throws Exception {
        UnconfirmedTextMessageRequest req = new UnconfirmedTextMessageRequest(
                new ObjectIdentifier(ObjectType.device, 8), priority, new CharacterString("hi"));
        UnconfirmedRequest apdu = new UnconfirmedRequest(req);
        return getNetworkPriority(apdu);
    }

    private int getNetworkPriorityOther() throws Exception {
        UnconfirmedPrivateTransferRequest req = new UnconfirmedPrivateTransferRequest(11, 12, new Real(3.14F));
        UnconfirmedRequest apdu = new UnconfirmedRequest(req);
        return getNetworkPriority(apdu);
    }

    private int getNetworkPriority(APDU apdu) throws Exception {
        final ByteQueue queue = new ByteQueue();
        Network network = new Network() {
            @Override
            public void terminate() {
                throw new RuntimeException();
            }

            @Override
            protected void sendNPDU(Address recipient, OctetString router, ByteQueue npdu, boolean broadcast,
                    boolean expectsReply) throws BACnetException {
                queue.push(npdu);
            }

            @Override
            protected NPDU handleIncomingDataImpl(ByteQueue queue, OctetString linkService) throws Exception {
                throw new RuntimeException();
            }

            @Override
            public NetworkIdentifier getNetworkIdentifier() {
                throw new RuntimeException();
            }

            @Override
            public MaxApduLength getMaxApduLength() {
                throw new RuntimeException();
            }

            @Override
            public long getBytesOut() {
                throw new RuntimeException();
            }

            @Override
            public long getBytesIn() {
                throw new RuntimeException();
            }

            @Override
            protected OctetString getBroadcastMAC() {
                throw new RuntimeException();
            }

            @Override
            public Address[] getAllLocalAddresses() {
                throw new RuntimeException();
            }
        };

        network.sendAPDU(new Address(2, new byte[] { 2 }), new OctetString(new byte[] { 5 }), apdu, false);

        queue.pop();
        byte control = queue.pop();

        return control & 0x3;
    }
}
