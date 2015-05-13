package com.serotonin.bacnet4j.npdu.test;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

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
import com.serotonin.bacnet4j.util.sero.ThreadUtils;

public class TestNetwork extends Network implements Runnable {
    public static final OctetString BROADCAST = new OctetString(new byte[0]);

    private static Map<Address, TestNetwork> instances = new ConcurrentHashMap<Address, TestNetwork>();

    private final Address address;
    private final int sendDelay;

    private volatile boolean running = true;
    private Thread thread;

    private final Queue<SendData> queue = new ConcurrentLinkedQueue<SendData>();
    private long bytesOut;
    private long bytesIn;

    public TestNetwork(int address, int sendDelay) {
        this(new Address(new byte[] { (byte) address }), sendDelay);
    }

    public TestNetwork(Address address, int sendDelay) {
        this.address = address;
        this.sendDelay = sendDelay;
    }

    @Override
    public NetworkIdentifier getNetworkIdentifier() {
        return new TestNetworkIdentifier();
    }

    @Override
    public MaxApduLength getMaxApduLength() {
        return MaxApduLength.UP_TO_1476;
    }

    @Override
    public long getBytesOut() {
        return bytesOut;
    }

    @Override
    public long getBytesIn() {
        return bytesIn;
    }

    @Override
    public void initialize(Transport transport) throws Exception {
        super.initialize(transport);

        // Set the timeout/retries settings in the transport to something debugger friendly.
        transport.setTimeout(1000 * 60); // one minute
        transport.setRetries(0); // no retries, there's no network here after all
        transport.setSegTimeout(1000 * 10); // 10 seconds.

        thread = new Thread(this, "BACnet4J test network");
        thread.start();

        instances.put(address, this);
    }

    @Override
    public void terminate() {
        instances.remove(address);

        running = false;
        ThreadUtils.notifySync(queue);
        if (thread != null)
            ThreadUtils.join(thread);
    }

    @Override
    protected OctetString getBroadcastMAC() {
        return BROADCAST;
    }

    @Override
    public Address[] getAllLocalAddresses() {
        return new Address[] { address };
    }

    @Override
    protected void sendNPDU(Address recipient, OctetString router, ByteQueue npdu, boolean broadcast,
            boolean expectsReply) throws BACnetException {
        SendData d = new SendData();
        d.recipient = recipient;
        d.data = npdu.popAll();

        queue.add(d);
        ThreadUtils.notifySync(queue);
    }

    @Override
    public void run() {
        while (running) {
            SendData d = queue.poll();

            if (d == null)
                ThreadUtils.waitSync(queue, 20);
            else {
                // Pause before handing off the message.
                ThreadUtils.sleep(sendDelay);

                if (d.recipient.equals(getLocalBroadcastAddress()) || d.recipient.equals(Address.GLOBAL)) {
                    for (TestNetwork network : instances.values())
                        receive(network, d.data);
                }
                else {
                    TestNetwork network = instances.get(d.recipient);
                    if (network != null)
                        receive(network, d.data);
                }
            }
        }
    }

    private void receive(TestNetwork network, byte[] data) {
        network.handleIncomingData(new ByteQueue(data), address.getMacAddress());
    }

    @Override
    protected NPDU handleIncomingDataImpl(ByteQueue queue, OctetString linkService)
            throws MessageValidationAssertionException {
        return parseNpduData(queue, linkService);
    }

    static class SendData {
        Address recipient;
        byte[] data;
    }
}
