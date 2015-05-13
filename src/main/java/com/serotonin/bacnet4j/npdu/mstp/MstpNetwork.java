package com.serotonin.bacnet4j.npdu.mstp;

import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.MessageValidationAssertionException;
import com.serotonin.bacnet4j.npdu.NPDU;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.NetworkIdentifier;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class MstpNetwork extends Network {
    private final MstpNode node;

    public MstpNetwork(MstpNode node) {
        this(node, 0);
    }

    public MstpNetwork(MstpNode node, int localNetworkNumber) {
        super(localNetworkNumber);
        this.node = node;
        node.setNetwork(this);
    }

    @Override
    public MaxApduLength getMaxApduLength() {
        return MaxApduLength.UP_TO_480;
    }

    @Override
    public void initialize(Transport transport) throws Exception {
        super.initialize(transport);
        node.initialize();
    }

    @Override
    public void terminate() {
        node.terminate();
    }

    @Override
    public NetworkIdentifier getNetworkIdentifier() {
        return new MstpNetworkIdentifier(node.getCommPortId());
    }

    @Override
    protected OctetString getBroadcastMAC() {
        return MstpNetworkUtils.toOctetString((byte) 0xFF);
    }

    @Override
    public Address[] getAllLocalAddresses() {
        return new Address[] { MstpNetworkUtils.toAddress(getLocalNetworkNumber(), node.getThisStation()) };
    }

    @Override
    public long getBytesOut() {
        return node.getBytesOut();
    }

    @Override
    public long getBytesIn() {
        return node.getBytesIn();
    }

    @Override
    protected void sendNPDU(Address recipient, OctetString router, ByteQueue npdu, boolean broadcast,
            boolean expectsReply) throws BACnetException {
        byte[] data = npdu.popAll();

        OctetString dest = getDestination(recipient, router);
        byte mstpAddress = MstpNetworkUtils.getMstpAddress(dest);

        if (expectsReply) {
            if (node instanceof SlaveNode)
                throw new RuntimeException("Cannot originate a request from a slave node");

            ((MasterNode) node).queueFrame(FrameType.bacnetDataExpectingReply, mstpAddress, data);
        }
        else
            node.setReplyFrame(FrameType.bacnetDataNotExpectingReply, mstpAddress, data);
    }

    public void sendTestRequest(byte destination) {
        if (!(node instanceof MasterNode))
            throw new RuntimeException("Only master nodes can send test requests");
        ((MasterNode) node).queueFrame(FrameType.testRequest, destination, null);
    }

    //
    //
    //
    // Incoming frames
    //
    void receivedFrame(Frame frame) {
        handleIncomingData(new ByteQueue(frame.getData()), MstpNetworkUtils.toOctetString(frame.getSourceAddress()));
    }

    @Override
    protected NPDU handleIncomingDataImpl(ByteQueue queue, OctetString linkService)
            throws MessageValidationAssertionException {
        return parseNpduData(queue, linkService);
    }

    //
    //
    // Convenience methods
    //
    public Address getAddress(byte station) {
        return MstpNetworkUtils.toAddress(getLocalNetworkNumber(), station);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((node == null) ? 0 : node.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MstpNetwork other = (MstpNetwork) obj;
        if (node == null) {
            if (other.node != null)
                return false;
        }
        else if (!node.equals(other.node))
            return false;
        return true;
    }
}
