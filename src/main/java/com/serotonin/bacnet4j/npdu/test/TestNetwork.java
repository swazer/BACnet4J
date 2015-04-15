package com.serotonin.bacnet4j.npdu.test;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.NetworkIdentifier;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;
import com.serotonin.bacnet4j.util.sero.ThreadUtils;

public class TestNetwork extends Network implements Runnable {
    public static final Address BROADCAST = new Address(new OctetString(new byte[0]));

    private static Map<Address, TestNetwork> instances = new ConcurrentHashMap<Address, TestNetwork>();

    private final Address address;
    private final int sendDelay;

    private volatile boolean running = true;
    private Thread thread;

    private final Queue<SendData> queue = new ConcurrentLinkedQueue<SendData>();
    private long bytesOut;
    private long bytesIn;

    public TestNetwork(Address address, int sendDelay) {
        this.address = address;
        this.sendDelay = sendDelay;
        instances.put(address, this);
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

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void terminate() {
        running = false;
        ThreadUtils.notifySync(queue);
        if (thread != null)
            ThreadUtils.join(thread);
    }

    @Override
    public Address getLocalBroadcastAddress() {
        return BROADCAST;
    }

    @Override
    public Address[] getAllLocalAddresses() {
        return new Address[] { address };
    }

    @Override
    public void sendAPDU(Address recipient, OctetString linkService, APDU apdu, boolean broadcast)
            throws BACnetException {
        SendData d = new SendData();
        d.recipient = recipient;

        ByteQueue bq = new ByteQueue();
        apdu.write(bq);
        d.data = bq.popAll();

        queue.add(d);
        ThreadUtils.notifySync(queue);
    }

    @Override
    public void checkSendThread() {
        // no op
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

                try {
                    if (d.recipient.equals(BROADCAST) || d.recipient.equals(Address.GLOBAL)) {
                        for (TestNetwork network : instances.values())
                            receive(network, d.data);
                    }
                    else {
                        TestNetwork network = instances.get(d.recipient);
                        if (network != null)
                            receive(network, d.data);
                    }
                }
                catch (BACnetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void receive(TestNetwork network, byte[] data) throws BACnetException {
        ServicesSupported servicesSupported;
        try {
            servicesSupported = network.getTransport().getLocalDevice().getServicesSupported();
        }
        catch (BACnetServiceException e) {
            throw new RuntimeException(e);
        }
        APDU apdu = APDU.createAPDU(servicesSupported, new ByteQueue(data));
        network.getTransport().incoming(apdu, address, null);
    }

    static class SendData {
        Address recipient;
        byte[] data;
    }
}
