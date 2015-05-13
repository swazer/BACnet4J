package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class LightingInProgress extends Enumerated {
    private static final long serialVersionUID = -1825768763307283055L;

    public static final LightingInProgress idle = new LightingInProgress(0);
    public static final LightingInProgress fadeActive = new LightingInProgress(1);
    public static final LightingInProgress rampActive = new LightingInProgress(2);
    public static final LightingInProgress notControlled = new LightingInProgress(3);
    public static final LightingInProgress other = new LightingInProgress(4);

    public static final LightingInProgress[] ALL = { idle, fadeActive, rampActive, notControlled, other, };

    public LightingInProgress(int value) {
        super(value);
    }

    public LightingInProgress(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == idle.intValue())
            return "idle";
        if (type == fadeActive.intValue())
            return "fadeActive";
        if (type == rampActive.intValue())
            return "rampActive";
        if (type == notControlled.intValue())
            return "notControlled";
        if (type == other.intValue())
            return "other";
        return "Unknown(" + type + ")";
    }
}
