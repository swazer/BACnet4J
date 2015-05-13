package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class WriteStatus extends Enumerated {
    private static final long serialVersionUID = -1825768763307283055L;

    public static final WriteStatus idle = new WriteStatus(0);
    public static final WriteStatus inProgress = new WriteStatus(1);
    public static final WriteStatus successful = new WriteStatus(2);
    public static final WriteStatus failed = new WriteStatus(3);

    public static final WriteStatus[] ALL = { idle, inProgress, successful, failed, };

    public WriteStatus(int value) {
        super(value);
    }

    public WriteStatus(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == idle.intValue())
            return "idle";
        if (type == inProgress.intValue())
            return "inProgress";
        if (type == successful.intValue())
            return "successful";
        if (type == failed.intValue())
            return "failed";
        return "Unknown(" + type + ")";
    }
}
