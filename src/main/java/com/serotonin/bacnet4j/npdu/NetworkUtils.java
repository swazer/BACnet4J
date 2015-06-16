package com.serotonin.bacnet4j.npdu;

import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

import com.serotonin.bacnet4j.base.BACnetUtils;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
import com.serotonin.bacnet4j.npdu.ipv6.Ipv6NetworkUtils;
import com.serotonin.bacnet4j.npdu.mstp.MstpNetworkUtils;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.Unsigned16;

public class NetworkUtils {
    public static String toString(OctetString mac) {
        if (mac.getLength() == 1)
            // Assume an MS/TP address
            return MstpNetworkUtils.toString(mac);

        if (mac.getLength() == 6)
            // Assume an I/Pv4 address
            return IpNetworkUtils.toString(mac);

        if (mac.getLength() == 18)
            // Assume an I/Pv6 address
            return Ipv6NetworkUtils.toString(mac);

        return toDottedString(mac);
    }

    public static String toDottedString(OctetString mac) {
        return BACnetUtils.bytesToDottedString(mac.getBytes());
    }

    /**
     * MS/TP should be a single digit: "5"
     * IPv4 should a dotted string with port: 192.168.0.5:47808
     * IPv6 should be something like this: [::1]:47808
     * 
     * @param s
     * @return
     * @throws BACnetException
     */
    public static OctetString toOctetString(String s) {
        try {
            int colon = s.indexOf(':');
            if (colon == -1)
                // Assume MS/TP
                return new OctetString(new byte[] { Byte.parseByte(s) });

            // WORKAROUND: add any scheme to make the resulting URI valid.
            URI uri = new URI("xx://" + s); // may throw URISyntaxException

            if (uri.getHost() == null || uri.getPort() == -1)
                throw new URISyntaxException(uri.toString(), "URI must have host and port parts");

            byte[] b = InetAddress.getByName(uri.getHost()).getAddress();
            byte[] bb = new byte[b.length + 2];
            System.arraycopy(b, 0, bb, 0, b.length);
            bb[b.length] = (byte) ((uri.getPort() >> 8) & 0xff);
            bb[b.length + 1] = (byte) (uri.getPort() & 0xff);

            return new OctetString(bb);
        }
        catch (Exception e) {
            throw new RuntimeException("Error parsing '" + s + "'", e);
        }
    }

    public static Address toAddress(int networkNumber, String dottedString) {
        return new Address(new Unsigned16(networkNumber), toOctetString(dottedString));
    }
}
