package com.serotonin.bacnet4j.npdu.mstp;

import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;

public class MstpNetworkUtils {
    public static OctetString toOctetString(byte station) {
        return new OctetString(new byte[] { station });
    }

    public static byte getMstpAddress(OctetString mac) {
        return mac.getBytes()[0];
    }

    public static String toString(OctetString mac) {
        return Integer.toString(getMstpAddress(mac) & 0xff);
    }

    public static Address toAddress(byte station) {
        return new Address(toOctetString(station));
    }

    public static Address toAddress(int networkNumber, byte station) {
        return new Address(networkNumber, toOctetString(station));
    }

    public static Address toAddress(int station) {
        return new Address(toOctetString((byte) station));
    }

    public static Address toAddress(int networkNumber, int station) {
        return new Address(networkNumber, toOctetString((byte) station));
    }
}
