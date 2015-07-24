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

public class AccessCredentialDisableReason extends Enumerated {
    private static final long serialVersionUID = 5503231556407385733L;

    public static final AccessCredentialDisableReason disabled = new AccessCredentialDisableReason(0);
    public static final AccessCredentialDisableReason disabledNeedsProvisioning = new AccessCredentialDisableReason(1);
    public static final AccessCredentialDisableReason disabledUnassigned = new AccessCredentialDisableReason(2);
    public static final AccessCredentialDisableReason disabledNotYetActive = new AccessCredentialDisableReason(3);
    public static final AccessCredentialDisableReason disabledExpired = new AccessCredentialDisableReason(4);
    public static final AccessCredentialDisableReason disabledLockout = new AccessCredentialDisableReason(5);
    public static final AccessCredentialDisableReason disabledMaxDays = new AccessCredentialDisableReason(6);
    public static final AccessCredentialDisableReason disabledMaxUses = new AccessCredentialDisableReason(7);
    public static final AccessCredentialDisableReason disabledInactivity = new AccessCredentialDisableReason(8);
    public static final AccessCredentialDisableReason disabledManual = new AccessCredentialDisableReason(9);

    public static final AccessCredentialDisableReason[] ALL = { disabled, disabledNeedsProvisioning,
            disabledUnassigned, disabledNotYetActive, disabledExpired, disabledLockout, disabledMaxDays,
            disabledMaxUses, disabledInactivity, disabledManual, };

    public AccessCredentialDisableReason(int value) {
        super(value);
    }

    public AccessCredentialDisableReason(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == disabled.intValue())
            return "disabled";
        if (type == disabledNeedsProvisioning.intValue())
            return "disabledNeedsProvisioning";
        if (type == disabledUnassigned.intValue())
            return "disabledUnassigned";
        if (type == disabledNotYetActive.intValue())
            return "disabledNotYetActive";
        if (type == disabledExpired.intValue())
            return "disabledExpired";
        if (type == disabledLockout.intValue())
            return "disabledLockout";
        if (type == disabledMaxDays.intValue())
            return "disabledMaxDays";
        if (type == disabledMaxUses.intValue())
            return "disabledMaxUses";
        if (type == disabledInactivity.intValue())
            return "disabledInactivity";
        if (type == disabledManual.intValue())
            return "disabledManual";
        return "Unknown(" + type + ")";
    }
}
