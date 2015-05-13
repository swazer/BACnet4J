package com.serotonin.bacnet4j.type.constructed;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.enumerated.LightingOperation;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class LightingCommand extends BaseType {
    private static final long serialVersionUID = -6093157514538223971L;

    private final LightingOperation operation;
    private final Real targetLevel;
    private final Real rampRate;
    private final Real stepIncrement;
    private final UnsignedInteger fadeTime;
    private final UnsignedInteger priority;

    public LightingCommand(LightingOperation operation, Real targetLevel, Real rampRate, Real stepIncrement,
            UnsignedInteger fadeTime, UnsignedInteger priority) {
        this.operation = operation;
        this.targetLevel = targetLevel;
        this.rampRate = rampRate;
        this.stepIncrement = stepIncrement;
        this.fadeTime = fadeTime;
        this.priority = priority;
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, operation, 0);
        write(queue, targetLevel, 1);
        write(queue, rampRate, 2);
        write(queue, stepIncrement, 3);
        write(queue, fadeTime, 4);
        writeOptional(queue, priority, 5);
    }

    public LightingCommand(ByteQueue queue) throws BACnetException {
        operation = read(queue, LightingOperation.class, 0);
        targetLevel = read(queue, Real.class, 1);
        rampRate = read(queue, Real.class, 2);
        stepIncrement = read(queue, Real.class, 3);
        fadeTime = read(queue, UnsignedInteger.class, 4);
        priority = readOptional(queue, UnsignedInteger.class, 5);
    }

    public LightingOperation getOperation() {
        return operation;
    }

    public Real getTargetLevel() {
        return targetLevel;
    }

    public Real getRampRate() {
        return rampRate;
    }

    public Real getStepIncrement() {
        return stepIncrement;
    }

    public UnsignedInteger getFadeTime() {
        return fadeTime;
    }

    public UnsignedInteger getPriority() {
        return priority;
    }

    @Override
    public String toString() {
        return "LightingCommand [operation=" + operation + ", targetLevel=" + targetLevel + ", rampRate=" + rampRate
                + ", stepIncrement=" + stepIncrement + ", fadeTime=" + fadeTime + ", priority=" + priority + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fadeTime == null) ? 0 : fadeTime.hashCode());
        result = prime * result + ((operation == null) ? 0 : operation.hashCode());
        result = prime * result + ((priority == null) ? 0 : priority.hashCode());
        result = prime * result + ((rampRate == null) ? 0 : rampRate.hashCode());
        result = prime * result + ((stepIncrement == null) ? 0 : stepIncrement.hashCode());
        result = prime * result + ((targetLevel == null) ? 0 : targetLevel.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LightingCommand other = (LightingCommand) obj;
        if (fadeTime == null) {
            if (other.fadeTime != null)
                return false;
        }
        else if (!fadeTime.equals(other.fadeTime))
            return false;
        if (operation == null) {
            if (other.operation != null)
                return false;
        }
        else if (!operation.equals(other.operation))
            return false;
        if (priority == null) {
            if (other.priority != null)
                return false;
        }
        else if (!priority.equals(other.priority))
            return false;
        if (rampRate == null) {
            if (other.rampRate != null)
                return false;
        }
        else if (!rampRate.equals(other.rampRate))
            return false;
        if (stepIncrement == null) {
            if (other.stepIncrement != null)
                return false;
        }
        else if (!stepIncrement.equals(other.stepIncrement))
            return false;
        if (targetLevel == null) {
            if (other.targetLevel != null)
                return false;
        }
        else if (!targetLevel.equals(other.targetLevel))
            return false;
        return true;
    }
}
