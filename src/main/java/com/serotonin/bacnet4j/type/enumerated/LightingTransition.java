package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class LightingTransition extends Enumerated {
    private static final long serialVersionUID = 1214338255509648797L;

    public static final LightingTransition none = new LightingTransition(0);
    public static final LightingTransition fade = new LightingTransition(1);
    public static final LightingTransition ramp = new LightingTransition(2);

    public static final LightingTransition[] ALL = { none, fade, ramp, };

    public LightingTransition(int value) {
        super(value);
    }

    public LightingTransition(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == none.intValue())
            return "none";
        if (type == fade.intValue())
            return "fade";
        if (type == ramp.intValue())
            return "ramp";
        return "Unknown(" + type + ")";
    }
}
