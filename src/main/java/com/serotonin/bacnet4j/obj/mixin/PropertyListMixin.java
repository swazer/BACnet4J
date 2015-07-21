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

import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectIdentifier;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectName;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.objectType;
import static com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier.propertyList;

import java.util.ArrayList;
import java.util.List;

import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.obj.AbstractMixin;
import com.serotonin.bacnet4j.obj.BACnetObject;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public class PropertyListMixin extends AbstractMixin {
    public PropertyListMixin(BACnetObject bo) {
        super(bo);
    }

    @Override
    public boolean writeProperty(PropertyValue value) throws BACnetServiceException {
        if (propertyList.equals(value.getPropertyIdentifier()))
            throw new BACnetServiceException(ErrorClass.property, ErrorCode.writeAccessDenied);
        return false;
    }

    public void update() {
        List<PropertyIdentifier> pids = new ArrayList<PropertyIdentifier>();
        for (PropertyIdentifier p : properties().keySet()) {
            if (!p.isOneOf(objectName, objectType, objectIdentifier, propertyList))
                pids.add(p);
        }

        writePropertyImpl(propertyList, new SequenceOf<PropertyIdentifier>(pids));
    }
}
