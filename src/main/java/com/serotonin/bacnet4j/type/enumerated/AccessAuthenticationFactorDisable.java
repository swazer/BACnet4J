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

public class AccessAuthenticationFactorDisable extends Enumerated {
    private static final long serialVersionUID = 3409339570414164518L;

    public static final AccessAuthenticationFactorDisable none = new AccessAuthenticationFactorDisable(0);
    public static final AccessAuthenticationFactorDisable disabled = new AccessAuthenticationFactorDisable(1);
    public static final AccessAuthenticationFactorDisable disabledLost = new AccessAuthenticationFactorDisable(2);
    public static final AccessAuthenticationFactorDisable disabledStolen = new AccessAuthenticationFactorDisable(3);
    public static final AccessAuthenticationFactorDisable disabledDamaged = new AccessAuthenticationFactorDisable(4);
    public static final AccessAuthenticationFactorDisable disabledDestroyed = new AccessAuthenticationFactorDisable(5);

    public static final AccessAuthenticationFactorDisable[] ALL = { none, disabled, disabledLost, disabledStolen,
            disabledDamaged, disabledDestroyed };

    public AccessAuthenticationFactorDisable(int value) {
        super(value);
    }

    public AccessAuthenticationFactorDisable(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == none.intValue())
            return "none";
        if (type == disabled.intValue())
            return "disabled";
        if (type == disabledLost.intValue())
            return "disabledLost";
        if (type == disabledStolen.intValue())
            return "disabledStolen";
        if (type == disabledDamaged.intValue())
            return "disabledDamaged";
        if (type == disabledDestroyed.intValue())
            return "disabledDestroyed";
        return "Unknown(" + type + ")";
    }
}
