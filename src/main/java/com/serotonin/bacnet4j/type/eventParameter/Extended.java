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
package com.serotonin.bacnet4j.type.eventParameter;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.BaseType;
import com.serotonin.bacnet4j.type.constructed.DeviceObjectPropertyReference;
import com.serotonin.bacnet4j.type.constructed.SequenceOf;
import com.serotonin.bacnet4j.type.primitive.Primitive;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class Extended extends EventParameter {
    private static final long serialVersionUID = 4998428230393436920L;

    public static final byte TYPE_ID = 9;

    private final UnsignedInteger vendorId;
    private final UnsignedInteger extendedEventType;
    private final SequenceOf<Parameter> parameters;

    public Extended(UnsignedInteger vendorId, UnsignedInteger extendedEventType, SequenceOf<Parameter> parameters) {
        this.vendorId = vendorId;
        this.extendedEventType = extendedEventType;
        this.parameters = parameters;
    }

    @Override
    protected void writeImpl(ByteQueue queue) {
        write(queue, vendorId, 0);
        write(queue, extendedEventType, 1);
        write(queue, parameters, 2);
    }

    public Extended(ByteQueue queue) throws BACnetException {
        vendorId = read(queue, UnsignedInteger.class, 0);
        extendedEventType = read(queue, UnsignedInteger.class, 1);
        parameters = readSequenceOf(queue, Parameter.class, 2);
    }

    @Override
    protected int getTypeId() {
        return TYPE_ID;
    }

    public UnsignedInteger getVendorId() {
        return vendorId;
    }

    public UnsignedInteger getExtendedEventType() {
        return extendedEventType;
    }

    public SequenceOf<Parameter> getParameters() {
        return parameters;
    }

    public static class Parameter extends BaseType {
        private static final long serialVersionUID = -7664447316518921548L;
        private Primitive primitive;
        private DeviceObjectPropertyReference reference;

        public Parameter(Primitive primitive) {
            this.primitive = primitive;
        }

        public Parameter(DeviceObjectPropertyReference reference) {
            this.reference = reference;
        }

        @Override
        public void write(ByteQueue queue) {
            if (primitive != null)
                primitive.write(queue);
            else
                reference.write(queue, 0);
        }

        public Parameter(ByteQueue queue) throws BACnetException {
            reference = readOptional(queue, DeviceObjectPropertyReference.class, 0);
            if (reference == null)
                primitive = Primitive.createPrimitive(queue);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((primitive == null) ? 0 : primitive.hashCode());
            result = prime * result + ((reference == null) ? 0 : reference.hashCode());
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
            Parameter other = (Parameter) obj;
            if (primitive == null) {
                if (other.primitive != null)
                    return false;
            }
            else if (!primitive.equals(other.primitive))
                return false;
            if (reference == null) {
                if (other.reference != null)
                    return false;
            }
            else if (!reference.equals(other.reference))
                return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((extendedEventType == null) ? 0 : extendedEventType.hashCode());
        result = PRIME * result + ((parameters == null) ? 0 : parameters.hashCode());
        result = PRIME * result + ((vendorId == null) ? 0 : vendorId.hashCode());
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
        final Extended other = (Extended) obj;
        if (extendedEventType == null) {
            if (other.extendedEventType != null)
                return false;
        }
        else if (!extendedEventType.equals(other.extendedEventType))
            return false;
        if (parameters == null) {
            if (other.parameters != null)
                return false;
        }
        else if (!parameters.equals(other.parameters))
            return false;
        if (vendorId == null) {
            if (other.vendorId != null)
                return false;
        }
        else if (!vendorId.equals(other.vendorId))
            return false;
        return true;
    }
}
