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

public class VtClass extends Enumerated {
    private static final long serialVersionUID = 8557805107090951917L;
    public static final VtClass defaultTerminal = new VtClass(0);
    public static final VtClass ansi_x3_64 = new VtClass(1);
    public static final VtClass dec_vt52 = new VtClass(2);
    public static final VtClass dec_vt100 = new VtClass(3);
    public static final VtClass dec_vt220 = new VtClass(4);
    public static final VtClass hp_700_94 = new VtClass(5);
    public static final VtClass ibm_3130 = new VtClass(6);

    public static final VtClass[] ALL = { defaultTerminal, ansi_x3_64, dec_vt52, dec_vt100, dec_vt220, hp_700_94,
            ibm_3130, };

    public VtClass(int value) {
        super(value);
    }

    public VtClass(ByteQueue queue) {
        super(queue);
    }
}
