package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AuthenticationFactorType extends Enumerated {
    private static final long serialVersionUID = -665947098579814279L;

    public static final AuthenticationFactorType undefined = new AuthenticationFactorType(0);
    public static final AuthenticationFactorType error = new AuthenticationFactorType(1);
    public static final AuthenticationFactorType custom = new AuthenticationFactorType(2);
    public static final AuthenticationFactorType simpleNumber16 = new AuthenticationFactorType(3);
    public static final AuthenticationFactorType simpleNumber32 = new AuthenticationFactorType(4);
    public static final AuthenticationFactorType simpleNumber56 = new AuthenticationFactorType(5);
    public static final AuthenticationFactorType simpleAlphaNumeric = new AuthenticationFactorType(6);
    public static final AuthenticationFactorType abaTrack2 = new AuthenticationFactorType(7);
    public static final AuthenticationFactorType wiegand26 = new AuthenticationFactorType(8);
    public static final AuthenticationFactorType wiegand37 = new AuthenticationFactorType(9);
    public static final AuthenticationFactorType wiegand37Facility = new AuthenticationFactorType(10);
    public static final AuthenticationFactorType facility16Card32 = new AuthenticationFactorType(11);
    public static final AuthenticationFactorType facility32Card32 = new AuthenticationFactorType(12);
    public static final AuthenticationFactorType fascN = new AuthenticationFactorType(13);
    public static final AuthenticationFactorType fascNBcd = new AuthenticationFactorType(14);
    public static final AuthenticationFactorType fascNLarge = new AuthenticationFactorType(15);
    public static final AuthenticationFactorType fascNLargeBcd = new AuthenticationFactorType(16);
    public static final AuthenticationFactorType gsa75 = new AuthenticationFactorType(17);
    public static final AuthenticationFactorType chuid = new AuthenticationFactorType(18);
    public static final AuthenticationFactorType chuidFull = new AuthenticationFactorType(19);
    public static final AuthenticationFactorType guid = new AuthenticationFactorType(20);
    public static final AuthenticationFactorType cbeffA = new AuthenticationFactorType(22);
    public static final AuthenticationFactorType cbeffB = new AuthenticationFactorType(23);
    public static final AuthenticationFactorType cbeffC = new AuthenticationFactorType(24);
    public static final AuthenticationFactorType userPassword = new AuthenticationFactorType(25);

    public static final AuthenticationFactorType[] ALL = { undefined, error, custom, simpleNumber16, simpleNumber32,
            simpleNumber56, simpleAlphaNumeric, abaTrack2, wiegand26, wiegand37, wiegand37Facility, facility16Card32,
            facility32Card32, fascN, fascNBcd, fascNLarge, fascNLargeBcd, gsa75, chuid, chuidFull, guid, cbeffA,
            cbeffB, cbeffC, userPassword, };

    public AuthenticationFactorType(int value) {
        super(value);
    }

    public AuthenticationFactorType(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == undefined.intValue())
            return "undefined";
        if (type == error.intValue())
            return "error";
        if (type == custom.intValue())
            return "custom";
        if (type == simpleNumber16.intValue())
            return "simpleNumber16";
        if (type == simpleNumber32.intValue())
            return "simpleNumber32";
        if (type == simpleNumber56.intValue())
            return "simpleNumber56";
        if (type == simpleAlphaNumeric.intValue())
            return "simpleAlphaNumeric";
        if (type == abaTrack2.intValue())
            return "abaTrack2";
        if (type == wiegand26.intValue())
            return "wiegand26";
        if (type == wiegand37.intValue())
            return "wiegand37";
        if (type == wiegand37Facility.intValue())
            return "wiegand37Facility";
        if (type == facility16Card32.intValue())
            return "facility16Card32";
        if (type == facility32Card32.intValue())
            return "facility32Card32";
        if (type == fascN.intValue())
            return "fascN";
        if (type == fascNBcd.intValue())
            return "fascNBcd";
        if (type == fascNLarge.intValue())
            return "fascNLarge";
        if (type == fascNLargeBcd.intValue())
            return "fascNLargeBcd";
        if (type == gsa75.intValue())
            return "gsa75";
        if (type == chuid.intValue())
            return "chuid";
        if (type == chuidFull.intValue())
            return "chuidFull";
        if (type == guid.intValue())
            return "guid";
        if (type == cbeffA.intValue())
            return "cbeffA";
        if (type == cbeffB.intValue())
            return "cbeffB";
        if (type == cbeffC.intValue())
            return "cbeffC";
        if (type == userPassword.intValue())
            return "userPassword";
        return "Unknown(" + type + ")";
    }
}
