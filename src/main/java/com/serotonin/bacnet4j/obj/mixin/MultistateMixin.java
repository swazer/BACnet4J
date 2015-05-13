package com.serotonin.bacnet4j.obj.mixin;

import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.obj.AbstractMixin;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class MultistateMixin extends AbstractMixin {
    public MultistateMixin(BACnetObject bo) {
        super(bo);
    }

    @Override
    protected boolean validateProperty(PropertyValue value) throws BACnetServiceException {
        if (PropertyIdentifier.presentValue.equals(value.getPropertyIdentifier())) {
            UnsignedInteger pv = (UnsignedInteger) value.getValue();
            UnsignedInteger numStates = get(PropertyIdentifier.numberOfStates);
            if (pv.intValue() < 1 || pv.intValue() > numStates.intValue())
                throw new BACnetServiceException(ErrorClass.property, ErrorCode.inconsistentConfiguration);
        }
        else if (PropertyIdentifier.numberOfStates.equals(value.getPropertyIdentifier())) {
            UnsignedInteger numStates = (UnsignedInteger) value.getValue();
            if (numStates.intValue() < 1)
                throw new BACnetServiceException(ErrorClass.property, ErrorCode.inconsistentConfiguration);
        }
        else if (PropertyIdentifier.stateText.equals(value.getPropertyIdentifier())) {
            @SuppressWarnings("unchecked")
            BACnetArray<CharacterString> stateText = (BACnetArray<CharacterString>) value.getValue();
            UnsignedInteger numStates = get(PropertyIdentifier.numberOfStates);
            if (numStates.intValue() != stateText.getCount())
                throw new BACnetServiceException(ErrorClass.property, ErrorCode.inconsistentConfiguration);
        }
        return false;
    }

    @Override
    protected void afterWriteProperty(PropertyIdentifier pid, Encodable oldValue, Encodable newValue) {
        if (PropertyIdentifier.numberOfStates.equals(pid)) {
            if (oldValue != null && !oldValue.equals(newValue)) {
                BACnetArray<CharacterString> stateText = get(PropertyIdentifier.stateText);
                if (stateText != null) {
                    int numStates = ((UnsignedInteger) newValue).intValue();
                    BACnetArray<CharacterString> newText = new BACnetArray<CharacterString>(numStates);

                    // Copy the old state values in.
                    int min = newText.getCount() < stateText.getCount() ? newText.getCount() : stateText.getCount();
                    for (int i = 0; i < min; i++)
                        newText.set(i + 1, stateText.get(i + 1));

                    // Fill in any null spots in case the new array is longer.
                    for (int i = min; i < newText.getCount(); i++)
                        newText.set(i + 1, new CharacterString(""));

                    writePropertyImpl(PropertyIdentifier.stateText, newText);
                }
            }
        }
    }
}
