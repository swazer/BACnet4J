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
