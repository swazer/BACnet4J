package com.serotonin.bacnet4j.npdu;

import com.serotonin.bacnet4j.base.BACnetUtils;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkUtils;
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
            // Assume an I/P address
            return IpNetworkUtils.toString(mac);

        return toDottedString(mac);
    }

    public static String toDottedString(OctetString mac) {
        return BACnetUtils.bytesToDottedString(mac.getBytes());
    }

    public static OctetString toOctetString(String s) {
        // MS/TP
        // I/P v4
        // I/P v6
        throw new RuntimeException("not implemented");
    }

    public static Address toAddress(int networkNumber, String dottedString) {
        return new Address(new Unsigned16(networkNumber), toOctetString(dottedString));
    }
}
