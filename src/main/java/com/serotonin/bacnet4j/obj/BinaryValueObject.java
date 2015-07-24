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

import com.serotonin.bacnet4j.obj.mixin.CommandableMixin;
import com.serotonin.bacnet4j.obj.mixin.CovReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.HasStatusFlagsMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.ChangeOfStateAlgo;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class BinaryValueObject extends BACnetObject {
    private static final long serialVersionUID = 6513179724432379974L;

    public BinaryValueObject(int instanceNumber, String name, BinaryPV presentValue, boolean outOfService) {
        super(ObjectType.binaryValue, instanceNumber, name);

        writePropertyImpl(PropertyIdentifier.eventState, EventState.normal);
        writePropertyImpl(PropertyIdentifier.outOfService, new Boolean(outOfService));
        writePropertyImpl(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, outOfService));

        // Mixins
        addMixin(new HasStatusFlagsMixin(this));
        addMixin(new CommandableMixin(this));

        writePropertyImpl(PropertyIdentifier.presentValue, presentValue);
    }

    public void supportIntrinsicReporting(int timeDelay, int notificationClass, BinaryPV alarmValue,
            EventTransitionBits eventEnable, NotifyType notifyType, int timeDelayNormal) {
        // Prepare the object with all of the properties that intrinsic reporting will need.
        // User-defined properties
        writePropertyImpl(PropertyIdentifier.timeDelay, new UnsignedInteger(timeDelay));
        writePropertyImpl(PropertyIdentifier.notificationClass, new UnsignedInteger(notificationClass));
        writePropertyImpl(PropertyIdentifier.alarmValue, alarmValue);
        writePropertyImpl(PropertyIdentifier.eventEnable, eventEnable);
        writePropertyImpl(PropertyIdentifier.notifyType, notifyType);
        writePropertyImpl(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(timeDelayNormal));

        ChangeOfStateAlgo eventAlgo = new ChangeOfStateAlgo(this, PropertyIdentifier.presentValue,
                PropertyIdentifier.alarmValue);
        addMixin(new IntrinsicReportingMixin(this, eventAlgo, null,
                new PropertyIdentifier[] { PropertyIdentifier.presentValue },
                new PropertyIdentifier[] { PropertyIdentifier.presentValue }));
        //
        //        // Now add the mixin.
        //        addMixin(new ChangeOfStateAlgo(this, null, null, PropertyIdentifier.presentValue,
        //                PropertyIdentifier.alarmValue, new PropertyIdentifier[] { PropertyIdentifier.presentValue }));
    }

    public void supportCovReporting() {
        supportCovReporting(CovReportingMixin.criteria13_1_4, null);
    }
}
