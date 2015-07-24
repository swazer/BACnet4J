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

public class AuthenticationStatus extends Enumerated {
    private static final long serialVersionUID = -9153277407804672743L;

    public static final AuthenticationStatus notReady = new AuthenticationStatus(0);
    public static final AuthenticationStatus ready = new AuthenticationStatus(1);
    public static final AuthenticationStatus disabled = new AuthenticationStatus(2);
    public static final AuthenticationStatus waitingForAuthenticationFactor = new AuthenticationStatus(3);
    public static final AuthenticationStatus waitingForAccompaniment = new AuthenticationStatus(4);
    public static final AuthenticationStatus waitingForVerification = new AuthenticationStatus(5);
    public static final AuthenticationStatus inProgress = new AuthenticationStatus(6);

    public static final AuthenticationStatus[] ALL = { notReady, ready, disabled, waitingForAuthenticationFactor,
            waitingForAccompaniment, waitingForVerification, inProgress, };

    public AuthenticationStatus(int value) {
        super(value);
    }

    public AuthenticationStatus(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == notReady.intValue())
            return "notReady";
        if (type == ready.intValue())
            return "ready";
        if (type == disabled.intValue())
            return "disabled";
        if (type == waitingForAuthenticationFactor.intValue())
            return "waitingForAuthenticationFactor";
        if (type == waitingForAccompaniment.intValue())
            return "waitingForAccompaniment";
        if (type == waitingForVerification.intValue())
            return "waitingForVerification";
        if (type == inProgress.intValue())
            return "inProgress";
        return "Unknown(" + type + ")";
    }
}
