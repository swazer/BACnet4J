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
