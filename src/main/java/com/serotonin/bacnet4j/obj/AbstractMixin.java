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

import java.util.Map;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.exception.BACnetServiceException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.constructed.PropertyValue;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;

/**
 * Mixins allow different objects to share functionality that is otherwise common between them. Functionality that is
 * specific to a given object type should still be coded into the class for the type itself.
 * 
 * @author Matthew
 */
public class AbstractMixin {
    private final BACnetObject bo;

    public AbstractMixin(BACnetObject bo) {
        this.bo = bo;
    }

    //
    //
    // Utility methods for subclasses to be able to access object properties.
    //
    protected final Map<PropertyIdentifier, Encodable> properties() {
        return bo.properties;
    }

    protected final void writePropertyImpl(PropertyIdentifier pid, Encodable value) {
        bo.writePropertyImpl(pid, value);
    }

    protected final <T extends Encodable> T get(PropertyIdentifier pid) {
        return bo.get(pid);
    }

    protected final LocalDevice getLocalDevice() {
        return bo.getLocalDevice();
    }

    //
    //
    // Methods for subclasses to override as needed.
    //
    /**
     * Allow the mixin a chance to perform actions before the property is read.
     * 
     * @param pid
     */
    protected void beforeReadProperty(PropertyIdentifier pid) {
        // no op
    }

    /**
     * Allow the mixin to override the property validation.
     * 
     * @param value
     * @return true of the validation was handled, false otherwise.
     * @throws BACnetServiceException
     */
    protected boolean validateProperty(PropertyValue value) throws BACnetServiceException {
        return false;
    }

    /**
     * Allow the mixin to override the property write.
     * 
     * @param value
     * @return true of the write was handled, false otherwise.
     * @throws BACnetServiceException
     */
    protected boolean writeProperty(PropertyValue value) throws BACnetServiceException {
        return false;
    }

    /**
     * @param pid
     * @param oldValue
     * @param newValue
     */
    protected void afterWriteProperty(PropertyIdentifier pid, Encodable oldValue, Encodable newValue) {
        // no op
    }
}
