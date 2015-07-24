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
package com.serotonin.bacnet4j.obj.mixin.intrinsicReporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.enumerated.Reliability;

// 13.4.5
public class FaultStateAlgo extends FaultAlgorithm {
    static final Logger LOG = LoggerFactory.getLogger(FaultStateAlgo.class);

    private final PropertyIdentifier currentReliabilityProperty;
    private final PropertyIdentifier faultValuesProperty;

    public FaultStateAlgo(BACnetObject bo, PropertyIdentifier currentReliabilityProperty,
            PropertyIdentifier faultValuesProperty) {
        super(bo);
        this.currentReliabilityProperty = currentReliabilityProperty;
        this.faultValuesProperty = faultValuesProperty;
    }

    @Override
    public Reliability evaluate(Encodable oldMonitoredValue, Encodable newMonitoredValue) {
        Reliability currentReliability = get(currentReliabilityProperty);
        if (currentReliability == null)
            currentReliability = Reliability.noFaultDetected;

        SequenceOf<Encodable> faultValues = get(faultValuesProperty);

        Reliability newReliability = null;

        if (currentReliability.equals(Reliability.noFaultDetected) && faultValues.contains(newMonitoredValue))
            newReliability = Reliability.multiStateFault;
        else if (currentReliability.equals(Reliability.multiStateFault) && !faultValues.contains(newMonitoredValue))
            newReliability = Reliability.noFaultDetected;
        else if (currentReliability.equals(Reliability.multiStateFault) && faultValues.contains(newMonitoredValue)
                && !faultValues.equals(oldMonitoredValue))
            newReliability = Reliability.multiStateFault;

        if (newReliability != null)
            LOG.debug("FaultState evaluated new reliability: {}", newReliability);

        return newReliability;
    }
}
