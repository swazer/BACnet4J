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
