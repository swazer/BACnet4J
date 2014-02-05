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
import com.serotonin.util.queue.ByteQueue;

public class Reliability extends Enumerated {
    private static final long serialVersionUID = 1105281466137206125L;
    public static final Reliability noFaultDetected = new Reliability(0);
    public static final Reliability noSensor = new Reliability(1);
    public static final Reliability overRange = new Reliability(2);
    public static final Reliability underRange = new Reliability(3);
    public static final Reliability openLoop = new Reliability(4);
    public static final Reliability shortedLoop = new Reliability(5);
    public static final Reliability noOutput = new Reliability(6);
    public static final Reliability unreliableOther = new Reliability(7);
    public static final Reliability processError = new Reliability(8);
    public static final Reliability multiStateFault = new Reliability(9);
    public static final Reliability configurationError = new Reliability(10);
    public static final Reliability communicationFailure = new Reliability(12);

    public static final Reliability[] ALL = { noFaultDetected, noSensor, overRange, underRange, openLoop, shortedLoop,
            noOutput, unreliableOther, processError, multiStateFault, configurationError, communicationFailure, };

    public Reliability(int value) {
        super(value);
    }

    public Reliability(ByteQueue queue) {
        super(queue);
    }

    @Override
    public String toString() {
        int type = intValue();
        if (type == noFaultDetected.intValue())
            return "No fault detected";
        if (type == noSensor.intValue())
            return "No sensor";
        if (type == overRange.intValue())
            return "Over range";
        if (type == underRange.intValue())
            return "Under range";
        if (type == openLoop.intValue())
            return "Open loop";
        if (type == shortedLoop.intValue())
            return "Shorted loop";
        if (type == noOutput.intValue())
            return "No output";
        if (type == unreliableOther.intValue())
            return "Unreliable other";
        if (type == processError.intValue())
            return "Process error";
        if (type == multiStateFault.intValue())
            return "Multi state fault";
        if (type == configurationError.intValue())
            return "Configuration error";
        if (type == communicationFailure.intValue())
            return "Communication failure";
        return "Unknown (" + type + ")";
    }
}
