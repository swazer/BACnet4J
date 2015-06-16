package com.serotonin.bacnet4j.npdu.ipv6;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import com.serotonin.bacnet4j.npdu.ip.InetAddrCache;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;

public class Ipv6NetworkUtils {
    public static OctetString toOctetString(byte[] addr, int port) {
        if (addr.length != 16)
            throw new IllegalArgumentException("Address must have 16 parts, not " + addr.length);

        byte[] b = new byte[18];
        System.arraycopy(addr, 0, b, 0, 16);
        b[16] = (byte) (port >> 8);
        b[17] = (byte) port;

        return new OctetString(b);
    }

    public static Address toAddress(int networkNumber, byte[] addr, int port) {
        return new Address(networkNumber, toOctetString(addr, port));
    }

    public static InetSocketAddress getInetSocketAddress(OctetString mac) {
        return InetAddrCache.get(getInetAddress(mac), getPort(mac));
    }

    public static InetAddress getInetAddress(OctetString mac) {
        try {
            return InetAddress.getByAddress(getIpBytes(mac));
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getPort(OctetString mac) {
        if (mac.getLength() != 18)
            throw new IllegalArgumentException("Not an I/Pv6 mac");
        return ((mac.getBytes()[16] & 0xff) << 8) | (mac.getBytes()[17] & 0xff);
    }

    public static byte[] getIpBytes(OctetString mac) {
        if (mac.getLength() != 18)
            throw new IllegalArgumentException("Not an I/Pv6 mac");
        byte[] b = new byte[16];
        System.arraycopy(mac.getBytes(), 0, b, 0, 16);
        return b;
    }

    public static String toString(OctetString mac) {
        return toIpPortString(mac);
    }

    public static String toIpPortString(OctetString mac) {
        return "[" + toIpString(mac) + "]:" + getPort(mac);
    }

    public static String toIpString(OctetString mac) {
        byte[] ipBytes = getIpBytes(mac);
        try {
            return InetAddress.getByAddress(ipBytes).getHostAddress();
        }
        catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
