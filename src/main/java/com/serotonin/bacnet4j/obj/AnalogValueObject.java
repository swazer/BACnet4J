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
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.IntrinsicReportingMixin;
import com.serotonin.bacnet4j.obj.mixin.intrinsicReporting.OutOfRangeAlgo;
import com.serotonin.bacnet4j.type.constructed.EventTransitionBits;
import com.serotonin.bacnet4j.type.constructed.LimitEnable;
import com.serotonin.bacnet4j.type.constructed.StatusFlags;
import com.serotonin.bacnet4j.type.enumerated.EngineeringUnits;
import com.serotonin.bacnet4j.type.enumerated.EventState;
import com.serotonin.bacnet4j.type.enumerated.NotifyType;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;

public class AnalogValueObject extends BACnetObject {
    private static final long serialVersionUID = -919281684940144433L;

    public AnalogValueObject(int instanceNumber, String name, float presentValue, EngineeringUnits units,
            boolean outOfService) {
        super(ObjectType.analogValue, instanceNumber, name);

        writePropertyImpl(PropertyIdentifier.eventState, EventState.normal);
        writePropertyImpl(PropertyIdentifier.presentValue, new Real(presentValue));
        writePropertyImpl(PropertyIdentifier.units, units);
        writePropertyImpl(PropertyIdentifier.outOfService, new com.serotonin.bacnet4j.type.primitive.Boolean(
                outOfService));
        writePropertyImpl(PropertyIdentifier.statusFlags, new StatusFlags(false, false, false, outOfService));

        // Mixins
        addMixin(new HasStatusFlagsMixin(this));
        addMixin(new CommandableMixin(this));
    }

    public void supportIntrinsicReporting(int timeDelay, int notificationClass, float highLimit, float lowLimit,
            float deadband, LimitEnable limitEnable, EventTransitionBits eventEnable, NotifyType notifyType,
            int timeDelayNormal) {

        // Prepare the object with all of the properties that intrinsic reporting will need.
        // User-defined properties
        writePropertyImpl(PropertyIdentifier.timeDelay, new UnsignedInteger(timeDelay));
        writePropertyImpl(PropertyIdentifier.notificationClass, new UnsignedInteger(notificationClass));
        writePropertyImpl(PropertyIdentifier.highLimit, new Real(highLimit));
        writePropertyImpl(PropertyIdentifier.lowLimit, new Real(lowLimit));
        writePropertyImpl(PropertyIdentifier.deadband, new Real(deadband));
        writePropertyImpl(PropertyIdentifier.limitEnable, limitEnable);
        writePropertyImpl(PropertyIdentifier.eventEnable, eventEnable);
        writePropertyImpl(PropertyIdentifier.notifyType, notifyType);
        writePropertyImpl(PropertyIdentifier.timeDelayNormal, new UnsignedInteger(timeDelayNormal));

        // Now add the mixin.
        addMixin(new IntrinsicReportingMixin(this, new OutOfRangeAlgo(this),
                null, //
                new PropertyIdentifier[] { PropertyIdentifier.presentValue, PropertyIdentifier.highLimit,
                        PropertyIdentifier.lowLimit, PropertyIdentifier.deadband, PropertyIdentifier.limitEnable, },
                new PropertyIdentifier[] { PropertyIdentifier.presentValue }));
    }

    public void supportCovReporting(float covIncrement) {
        supportCovReporting(CovReportingMixin.criteria13_1_3, new Real(covIncrement));
    }
}
