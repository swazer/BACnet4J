package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class LightingOperation extends Enumerated {
    private static final long serialVersionUID = -7585094616691021512L;

    public static final LightingOperation none = new LightingOperation(0);
    public static final LightingOperation fadeTo = new LightingOperation(1);
    public static final LightingOperation rampTo = new LightingOperation(2);
    public static final LightingOperation stepUp = new LightingOperation(3);
    public static final LightingOperation stepDown = new LightingOperation(4);
    public static final LightingOperation stepOn = new LightingOperation(5);
    public static final LightingOperation stepOff = new LightingOperation(6);
    public static final LightingOperation warn = new LightingOperation(7);
    public static final LightingOperation warnOff = new LightingOperation(8);
    public static final LightingOperation warnRelinquish = new LightingOperation(9);
    public static final LightingOperation stop = new LightingOperation(10);

    public static final LightingOperation[] ALL = { none, fadeTo, rampTo, stepUp, stepDown, stepOn, stepOff, warn,
            warnOff, warnRelinquish, stop, };

    public LightingOperation(int value) {
        super(value);
    }

    public LightingOperation(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();

        if (type == none.intValue())
            return "none";
        if (type == fadeTo.intValue())
            return "fadeTo";
        if (type == rampTo.intValue())
            return "rampTo";
        if (type == stepUp.intValue())
            return "stepUp";
        if (type == stepDown.intValue())
            return "stepDown";
        if (type == stepOn.intValue())
            return "stepOn";
        if (type == stepOff.intValue())
            return "stepOff";
        if (type == warn.intValue())
            return "warn";
        if (type == warnOff.intValue())
            return "warnOff";
        if (type == warnRelinquish.intValue())
            return "warnRelinquish";
        if (type == stop.intValue())
            return "stop";
        return "Unknown(" + type + ")";
    }
}
