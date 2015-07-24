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

public class SecurityLevel extends Enumerated {
    private static final long serialVersionUID = 4872601808833629487L;

    public static final SecurityLevel incapable = new SecurityLevel(0);
    public static final SecurityLevel plain = new SecurityLevel(1);
    public static final SecurityLevel signed = new SecurityLevel(2);
    public static final SecurityLevel encrypted = new SecurityLevel(3);
    public static final SecurityLevel signedEndToEnd = new SecurityLevel(4);
    public static final SecurityLevel encryptedEndToEnd = new SecurityLevel(5);

    public static final SecurityLevel[] ALL = { incapable, plain, signed, encrypted, signedEndToEnd, encryptedEndToEnd, };

    public SecurityLevel(int value) {
        super(value);
    }

    public SecurityLevel(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == incapable.intValue())
            return "incapable";
        if (type == plain.intValue())
            return "plain";
        if (type == signed.intValue())
            return "signed";
        if (type == encrypted.intValue())
            return "encrypted";
        if (type == signedEndToEnd.intValue())
            return "signedEndToEnd";
        if (type == encryptedEndToEnd.intValue())
            return "encryptedEndToEnd";
        return "Unknown(" + type + ")";
    }
}
