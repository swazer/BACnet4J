package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AccessZoneOccupancyState extends Enumerated {
    private static final long serialVersionUID = 6051525977406474069L;

    public static final AccessZoneOccupancyState normal = new AccessZoneOccupancyState(0);
    public static final AccessZoneOccupancyState belowLowerLimit = new AccessZoneOccupancyState(1);
    public static final AccessZoneOccupancyState atLowerLimit = new AccessZoneOccupancyState(2);
    public static final AccessZoneOccupancyState atUpperLimit = new AccessZoneOccupancyState(0);
    public static final AccessZoneOccupancyState aboveUpperLimit = new AccessZoneOccupancyState(1);
    public static final AccessZoneOccupancyState disabled = new AccessZoneOccupancyState(2);
    public static final AccessZoneOccupancyState notSupported = new AccessZoneOccupancyState(0);

    public static final AccessZoneOccupancyState[] ALL = { normal, belowLowerLimit, atLowerLimit, atUpperLimit,
            aboveUpperLimit, disabled, notSupported, };

    public AccessZoneOccupancyState(int value) {
        super(value);
    }

    public AccessZoneOccupancyState(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == normal.intValue())
            return "normal";
        if (type == belowLowerLimit.intValue())
            return "belowLowerLimit";
        if (type == atLowerLimit.intValue())
            return "atLowerLimit";
        if (type == atUpperLimit.intValue())
            return "atUpperLimit";
        if (type == aboveUpperLimit.intValue())
            return "aboveUpperLimit";
        if (type == disabled.intValue())
            return "disabled";
        if (type == notSupported.intValue())
            return "notSupported";
        return "Unknown(" + type + ")";
    }
}
