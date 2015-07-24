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

/**
 * @author Matthew Lohbihler
 */
public class DoorSecuredStatus extends Enumerated {
    private static final long serialVersionUID = 7337105893343734773L;
    public static final DoorSecuredStatus secured = new DoorSecuredStatus(0);
    public static final DoorSecuredStatus unsecured = new DoorSecuredStatus(1);
    public static final DoorSecuredStatus unknown = new DoorSecuredStatus(2);

    public static final DoorSecuredStatus[] ALL = { secured, unsecured, unknown, };

    public DoorSecuredStatus(int value) {
        super(value);
    }

    public DoorSecuredStatus(ByteQueue queue) {
        super(queue);
    }
}
