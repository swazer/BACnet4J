/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2006-2011 Serotonin Software Technologies Inc. http://serotoninsoftware.com
 * @author Matthew Lohbihler
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
 * When signing a commercial license with Serotonin Software Technologies Inc.,
 * the following extension to GPL is made. A special exception to the GPL is 
 * included to allow you to distribute a combined work that includes BAcnet4J 
 * without being obliged to provide the source code for any proprietary components.
 */
package com.serotonin.bacnet4j.type.enumerated;

import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class EventState extends Enumerated {
    private static final long serialVersionUID = -8567972022145562375L;
    public static final EventState normal = new EventState(0);
    public static final EventState fault = new EventState(1);
    public static final EventState offnormal = new EventState(2);
    public static final EventState highLimit = new EventState(3);
    public static final EventState lowLimit = new EventState(4);
    public static final EventState lifeSafetyAlarm = new EventState(5);

    public static final EventState[] ALL = { normal, fault, offnormal, highLimit, lowLimit, lifeSafetyAlarm, };

    public EventState(int value) {
        super(value);
    }

    public EventState(ByteQueue queue) {
        super(queue);
    }

    public boolean isOffNormal() {
        return equals(offnormal) || equals(highLimit) || equals(lowLimit) || equals(lifeSafetyAlarm);
    }

    public int getTransitionIndex() {
        if (isOffNormal())
            return 1;
        if (equals(EventState.fault))
            return 2;
        return 3;
    }

    @Override
    public String toString() {
        int value = intValue();
        if (value == 0)
            return "normal";
        if (value == 1)
            return "fault";
        if (value == 2)
            return "offnormal";
        if (value == 3)
            return "highLimit";
        if (value == 4)
            return "lowLimit";
        if (value == 5)
            return "lifeSafetyAlarm";
        return "Unknown(" + value + ")";
    }
}
