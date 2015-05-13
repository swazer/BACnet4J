package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessPassbackMode extends Enumerated {
    private static final long serialVersionUID = -9153277407804672743L;

    public static final AccessPassbackMode passbackOff = new AccessPassbackMode(0);
    public static final AccessPassbackMode hardPassback = new AccessPassbackMode(1);
    public static final AccessPassbackMode softPassback = new AccessPassbackMode(2);

    public static final AccessPassbackMode[] ALL = { passbackOff, hardPassback, softPassback, };

    public AccessPassbackMode(int value) {
        super(value);
    }

    public AccessPassbackMode(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == passbackOff.intValue())
            return "passbackOff";
        if (type == hardPassback.intValue())
            return "hardPassback";
        if (type == softPassback.intValue())
            return "softPassback";
        return "Unknown(" + type + ")";
    }
}
