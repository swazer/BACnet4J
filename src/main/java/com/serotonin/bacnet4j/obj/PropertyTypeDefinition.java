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

import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.PriorityArray;
import com.serotonin.bacnet4j.type.constructed.PriorityValue;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

public class PropertyTypeDefinition {
    private final ObjectType objectType;
    private final PropertyIdentifier propertyIdentifier;
    private final Class<? extends Encodable> clazz;
    private final boolean sequence;
    private final boolean required;

    PropertyTypeDefinition(ObjectType objectType, PropertyIdentifier propertyIdentifier,
            Class<? extends Encodable> clazz, boolean sequence, boolean required) {
        this.objectType = objectType;
        this.propertyIdentifier = propertyIdentifier;
        this.clazz = clazz;
        this.sequence = sequence;
        this.required = required;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public PropertyIdentifier getPropertyIdentifier() {
        return propertyIdentifier;
    }

    public Class<? extends Encodable> getClazz() {
        return clazz;
    }

    public boolean isSequence() {
        return sequence;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isOptional() {
        return !required;
    }

    public Class<? extends Encodable> getInnerType() {
        if (clazz == PriorityArray.class)
            return PriorityValue.class;
        return null;
    }
}
