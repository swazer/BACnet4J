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
package com.serotonin.bacnet4j.obj;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.obj.mixin.CommandableMixin;
import com.serotonin.bacnet4j.obj.mixin.CovReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.HasStatusFlagsMixin;
import com.serotonin.bacnet4j.obj.mixin.MultistateMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.ChangeOfStateAlgo;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.FaultStateAlgo;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin;
import com.serotonin.bacnet4j.type.constructed.BACnetArray;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.CharacterString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class MultistateValueObject extends BACnetObject {
    private static final long serialVersionUID = -5335002026964225708L;

    public MultistateValueObject(int instanceNumber, String name, int numberOfStates,
            BACnetArray<CharacterString> stateText, int presentValue, boolean outOfService) {
        super(ObjectType.multiStateValue, instanceNumber, name);

        if (numberOfStates < 1)
            throw new BACnetRuntimeException("numberOfStates cannot be less than 1");

        writePropertyImpl(PropertyIdentifier.eventState, EventState.normal);
        writeProperty(PropertyIdentifier.presentValue, new UnsignedInteger(presentValue));
        writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(true));
        writePropertyImpl(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, true));

        // Mixins
        addMixin(new HasStatusFlagsMixin(this));
        addMixin(new CommandableMixin(this));
        addMixin(new MultistateMixin(this));

        writePropertyImpl(PropertyIdentifier.numberOfStates, new UnsignedInteger(numberOfStates));
        if (stateText != null)
            writeProperty(PropertyIdentifier.stateText, stateText);
        writeProperty(PropertyIdentifier.presentValue, new UnsignedInteger(presentValue));
        if (!outOfService)
            writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(outOfService));
    }

    public void supportIntrinsicReporting(int timeDelay, int notificationClass,
            SequenceOf<UnsignedInteger> alarmValues, SequenceOf<UnsignedInteger> faultValues,
            EventTransitionBits eventEnable, NotifyType notifyType, int timeDelayNormal) {
        // Prepare the object with all of the properties that intrinsic reporting will need.
        // User-defined properties
        writePropertyImpl(PropertyIdentifier.timeDelay, new UnsignedInteger(timeDelay));
        writePropertyImpl(PropertyIdentifier.notificationClass, new UnsignedInteger(notificationClass));
        writePropertyImpl(PropertyIdentifier.alarmValues, alarmValues);
        if (faultValues != null)
            writePropertyImpl(PropertyIdentifier.faultValues, faultValues);
        writePropertyImpl(PropertyIdentifier.eventEnable, eventEnable);
        writePropertyImpl(PropertyIdentifier.notifyType, notifyType);
        writePropertyImpl(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(timeDelayNormal));

        // Now add the mixin.
        ChangeOfStateAlgo eventAlgo = new ChangeOfStateAlgo(this, PropertyIdentifier.presentValue,
                PropertyIdentifier.alarmValues);
        FaultStateAlgo faultAlgo = new FaultStateAlgo(this, PropertyIdentifier.reliability,
                PropertyIdentifier.faultValues);
        addMixin(new IntrinsicReportingMixin(this, eventAlgo, faultAlgo,
                new PropertyIdentifier[] { PropertyIdentifier.presentValue },
                new PropertyIdentifier[] { PropertyIdentifier.presentValue }));
    }

    public void supportCovReporting() {
        supportCovReporting(CovReportingMixin.criteria13_1_4, null);
    }
}
