/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
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
