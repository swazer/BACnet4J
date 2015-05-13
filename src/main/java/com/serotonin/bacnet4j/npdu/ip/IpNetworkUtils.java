package com.serotonin.bacnet4j.npdu.ip;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.serotonin.bacnet4j.base.BACnetUtils;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.IpAddressUtils;

public class IpNetworkUtils {
    public static OctetString toOctetString(String dottedString) {
        dottedString = dottedString.trim();
        int colon = dottedString.indexOf(":");
        if (colon == -1)
            throw new IllegalArgumentException("Dotted string missing port number. Expected x.x.x.x:port");

        byte[] ip = BACnetUtils.dottedStringToBytes(dottedString.substring(0, colon));
        int port = Integer.parseInt(dottedString.substring(colon + 1));
        return toOctetString(ip, port);
    }

    public static OctetString toOctetString(byte[] ipAddress, int port) {
        return new OctetString(toBytes(ipAddress, port));
    }

    public static OctetString toOctetString(String dotted, int port) {
        return toOctetString(BACnetUtils.dottedStringToBytes(dotted), port);
    }

    //    public OctetString toOctetString(InetSocketAddress addr) {
    //        return toOctetString(addr.getAddress().getAddress(), addr.getPort());
    //    }

    private static byte[] toBytes(byte[] ipAddress, int port) {
        if (ipAddress.length != 4)
            throw new IllegalArgumentException("IP address must have 4 parts, not " + ipAddress.length);

        byte[] b = new byte[6];
        System.arraycopy(ipAddress, 0, b, 0, ipAddress.length);
        b[ipAddress.length] = (byte) (port >> 8);
        b[ipAddress.length + 1] = (byte) port;
        return b;
    }

    public static InetAddress getInetAddress(OctetString mac) {
        try {
            return InetAddress.getByAddress(getIpBytes(mac));
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static InetSocketAddress getInetSocketAddress(OctetString mac) {
        return InetAddrCache.get(getInetAddress(mac), getPort(mac));
    }

    public static int getPort(OctetString mac) {
        if (mac.getLength() != 6)
            throw new IllegalArgumentException("Not an I/P mac");
        return ((mac.getBytes()[4] & 0xff) << 8) | (mac.getBytes()[5] & 0xff);
    }

    public static String toIpString(OctetString mac) {
        return IpAddressUtils.toIpString(getIpBytes(mac));
    }

    public static String toIpPortString(OctetString mac) {
        return toIpString(mac) + ":" + getPort(mac);
    }

    public static byte[] getIpBytes(OctetString mac) {
        if (mac.getLength() != 6)
            throw new IllegalArgumentException("Not an I/P mac");
        byte[] b = new byte[4];
        System.arraycopy(mac.getBytes(), 0, b, 0, 4);
        return b;
    }

    public static String toString(OctetString mac) {
        return toIpPortString(mac);
    }

    public static Address toAddress(byte[] ipAddress, int port) {
        return toAddress(Address.LOCAL_NETWORK, ipAddress, port);
    }

    /**
     * Convenience constructor for IP addresses remote to this network.
     * 
     * @param network
     * @param ipAddress
     * @param port
     */
    public static Address toAddress(int networkNumber, byte[] ipAddress, int port) {
        byte[] ipMacAddress = new byte[ipAddress.length + 2];
        System.arraycopy(ipAddress, 0, ipMacAddress, 0, ipAddress.length);
        ipMacAddress[ipAddress.length] = (byte) (port >> 8);
        ipMacAddress[ipAddress.length + 1] = (byte) port;
        return new Address(networkNumber, new OctetString(ipMacAddress));
    }

    public static Address toAddress(String host, int port) {
        return toAddress(Address.LOCAL_NETWORK, host, port);
    }

    public static Address toAddress(int networkNumber, String host, int port) {
        return toAddress(networkNumber, InetAddrCache.get(host, port));
    }

    public static Address toAddress(InetSocketAddress addr) {
        return toAddress(Address.LOCAL_NETWORK, addr.getAddress().getAddress(), addr.getPort());
    }

    public static Address toAddress(int networkNumber, InetSocketAddress addr) {
        return toAddress(networkNumber, addr.getAddress().getAddress(), addr.getPort());
    }
}
