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
