/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.npdu.ip;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import com.serotonin.bacnet4j.base.BACnetUtils;
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

public class IpNetwork extends Network implements Runnable {
    public static final byte BVLC_TYPE = (byte) 0x81;
    public static final String DEFAULT_BROADCAST_IP = "255.255.255.255";
    public static final int DEFAULT_PORT = 0xBAC0; // == 47808
    public static final String DEFAULT_BIND_IP = "0.0.0.0";

    private static final int MESSAGE_LENGTH = 2048;

    //    LocalDevice localDevice;

    private final int port;
    private final String localBindAddress;
    private final String broadcastIp;

    // Runtime
    private Thread thread;
    private DatagramSocket socket;
    private OctetString broadcastMAC;
    private long bytesOut;
    private long bytesIn;

    public IpNetwork() {
        this(DEFAULT_BROADCAST_IP);
    }

    public IpNetwork(String broadcastIp) {
        this(broadcastIp, DEFAULT_PORT);
    }

    public IpNetwork(String broadcastIp, int port) {
        this(broadcastIp, port, DEFAULT_BIND_IP);
    }

    public IpNetwork(String broadcastIp, int port, String localBindAddress) {
        this(broadcastIp, port, localBindAddress, 0);
    }

    public IpNetwork(String broadcastIp, int port, String localBindAddress, int localNetworkNumber) {
        super(localNetworkNumber);
        this.broadcastIp = broadcastIp;
        this.port = port;
        this.localBindAddress = localBindAddress;
    }

    @Override
    public NetworkIdentifier getNetworkIdentifier() {
        return new IpNetworkIdentifier(port, localBindAddress);
    }

    @Override
    public MaxApduLength getMaxApduLength() {
        return MaxApduLength.UP_TO_1476;
    }

    public int getPort() {
        return port;
    }

    public String getLocalBindAddress() {
        return localBindAddress;
    }

    public String getBroadcastIp() {
        return broadcastIp;
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

        if (localBindAddress.equals("0.0.0.0"))
            socket = new DatagramSocket(port);
        else
            socket = new DatagramSocket(port, InetAddress.getByName(localBindAddress));
        socket.setBroadcast(true);

        //        broadcastAddress = new Address(broadcastIp, port, new Network(0xffff, new byte[0]));
        broadcastMAC = IpNetworkUtils.toOctetString(broadcastIp, port);

        thread = new Thread(this, "BACnet4J IP socket listener");
        thread.start();
    }

    @Override
    public void terminate() {
        if (socket != null)
            socket.close();
    }

    @Override
    protected OctetString getBroadcastMAC() {
        return broadcastMAC;
    }

    public Address getBroadcastAddress(int port) {
        return IpNetworkUtils.toAddress(broadcastIp, port);
    }

    /**
     * Sends a foreign device registration request to addr. On successful registration (AKN), we are added
     * in the foreign device table FDT.
     * 
     * @param addr
     *            The address of the device where our device wants to be registered as foreign device
     * @param timeToLive
     *            The time until we are automatically removed out of the FDT.
     * @throws BACnetException
     */
    public void sendRegisterForeignDeviceMessage(InetSocketAddress addr, int timeToLive) throws BACnetException {
        ByteQueue queue = new ByteQueue();
        queue.push(BVLC_TYPE);
        queue.push(0x05); // Register foreign device
        queue.pushU2B(6); // Length
        queue.pushU2B(timeToLive); // TTL
        sendPacket(addr, queue.popAll());
    }

    @Override
    protected void sendNPDU(Address recipient, OctetString router, ByteQueue npdu, boolean broadcast,
            boolean expectsReply) throws BACnetException {
        ByteQueue queue = new ByteQueue();

        // BACnet virtual link layer detail
        queue.push(BVLC_TYPE);

        // Original-Unicast-NPDU, or Original-Broadcast-NPDU
        queue.push(broadcast ? 0xb : 0xa);

        // Length
        queue.pushU2B(queue.size() + npdu.size() + 2);

        // Combine the queues
        queue.push(npdu);

        OctetString dest = getDestination(recipient, router);
        InetSocketAddress isa = IpNetworkUtils.getInetSocketAddress(dest);

        sendPacket(isa, queue.popAll());
    }

    private void sendPacket(InetSocketAddress addr, byte[] data) throws BACnetException {
        try {
            DatagramPacket packet = new DatagramPacket(data, data.length, addr);
            socket.send(packet);
            bytesOut += data.length;
        }
        catch (Exception e) {
            throw new BACnetException(e);
        }
    }

    //
    // For receiving
    @Override
    public void run() {
        byte[] buffer = new byte[MESSAGE_LENGTH];
        DatagramPacket p = new DatagramPacket(buffer, buffer.length);

        while (!socket.isClosed()) {
            try {
                socket.receive(p);

                bytesIn += p.getLength();
                ByteQueue queue = new ByteQueue(p.getData(), 0, p.getLength());
                OctetString link = IpNetworkUtils.toOctetString(p.getAddress().getAddress(), p.getPort());

                handleIncomingData(queue, link);

                // Reset the packet.
                p.setData(buffer);
            }
            catch (IOException e) {
                // no op. This happens if the socket gets closed by the destroy method.
            }
        }
    }

    @Override
    protected NPDU handleIncomingDataImpl(ByteQueue queue, OctetString linkService) throws Exception {
        // Initial parsing of IP message.
        // BACnet/IP
        if (queue.pop() != BVLC_TYPE)
            throw new MessageValidationAssertionException("Protocol id is not BACnet/IP (0x81)");

        byte function = queue.pop();

        int length = BACnetUtils.popShort(queue);
        if (length != queue.size() + 4)
            throw new MessageValidationAssertionException("Length field does not match data: given=" + length
                    + ", expected=" + (queue.size() + 4));

        NPDU npdu = null;
        // Answer to foreign device registration
        if (function == 0x0) {
            int result = BACnetUtils.popShort(queue);
            if (result != 0)
                throw new BACnetException("Foreign device registration not successful! result: " + result);
        }
        else if (function == 0x4) {
            // A forward. Use the address/port as the link service address.
            byte[] addr = new byte[6];
            queue.pop(addr);
            npdu = parseNpduData(queue, new OctetString(addr));
        }
        else if (function == 0xa || function == 0xb)
            npdu = parseNpduData(queue, linkService);
        else
            throw new MessageValidationAssertionException("Unhandled BVLC function type: 0x"
                    + Integer.toHexString(function & 0xff));

        return npdu;
    }

    //
    //
    // Convenience methods
    //
    public Address getAddress(InetAddress inetAddress) {
        try {
            return IpNetworkUtils.toAddress(getLocalNetworkNumber(), inetAddress.getAddress(), port);
        }
        catch (Exception e) {
            // Should never happen, so just wrap in a RuntimeException
            throw new RuntimeException(e);
        }
    }

    public static InetAddress getDefaultLocalInetAddress() throws UnknownHostException, SocketException {
        for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
            for (InetAddress addr : Collections.list(iface.getInetAddresses())) {
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress())
                    return addr;
            }
        }

        return InetAddress.getLocalHost();
    }

    @Override
    public Address[] getAllLocalAddresses() {
        try {
            ArrayList<Address> result = new ArrayList<Address>();
            for (NetworkInterface iface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                for (InetAddress addr : Collections.list(iface.getInetAddresses())) {
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress())
                        result.add(getAddress(addr));
                }
            }

            return result.toArray(new Address[result.size()]);
        }
        catch (Exception e) {
            // Should never happen, so just wrap in a RuntimeException
            throw new RuntimeException(e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((broadcastIp == null) ? 0 : broadcastIp.hashCode());
        result = prime * result + ((localBindAddress == null) ? 0 : localBindAddress.hashCode());
        result = prime * result + port;
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
        IpNetwork other = (IpNetwork) obj;
        if (broadcastIp == null) {
            if (other.broadcastIp != null)
                return false;
        }
        else if (!broadcastIp.equals(other.broadcastIp))
            return false;
        if (localBindAddress == null) {
            if (other.localBindAddress != null)
                return false;
        }
        else if (!localBindAddress.equals(other.localBindAddress))
            return false;
        if (port != other.port)
            return false;
        return true;
    }
}
