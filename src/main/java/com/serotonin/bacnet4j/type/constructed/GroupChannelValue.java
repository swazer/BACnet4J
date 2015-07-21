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
package com.serotonin.bacnet4j.type.constructed;

import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.BinaryPV;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.primitive.Enumerated;
import com.serotonin.bacnet4j.type.primitive.Null;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.type.primitive.Unsigned16;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class GroupChannelValue extends BaseType {
    private static final long serialVersionUID = 7883669863981626367L;

    private final Unsigned16 channel; // 0
    private final UnsignedInteger overridingPriority; // 1 optional
    private final ChannelValue value; // 

    public GroupChannelValue(Unsigned16 channel, UnsignedInteger overridingPriority, ChannelValue value) {
        this.channel = channel;
        this.overridingPriority = overridingPriority;
        this.value = value;
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, channel, 0);
        writeOptional(queue, overridingPriority, 1);
        write(queue, value);
    }

    @Override
    public String toString() {
        return "GroupChannelValue [channel=" + channel + ", overridingPriority=" + overridingPriority + ", value="
                + value + "]";
    }

    public Unsigned16 getChannel() {
        return channel;
    }

    public UnsignedInteger getOverridingPriority() {
        return overridingPriority;
    }

    public ChannelValue getValue() {
        return value;
    }

    public GroupChannelValue(ByteQueue queue) throws BACnetException {
        channel = read(queue, Unsigned16.class, 0);
        overridingPriority = readOptional(queue, UnsignedInteger.class, 1);
        value = read(queue, ChannelValue.class);
    }

    public static class ChannelValue extends BaseType {
        private static final long serialVersionUID = -2620538935921657482L;

        private Null nullValue;
        private Real realValue;
        private BinaryPV binaryValue;
        private UnsignedInteger integerValue;
        private LightingCommand lightingCommand;

        public ChannelValue(Null nullValue) {
            this.nullValue = nullValue;
        }

        public ChannelValue(Real realValue) {
            this.realValue = realValue;
        }

        public ChannelValue(BinaryPV binaryValue) {
            this.binaryValue = binaryValue;
        }

        public ChannelValue(UnsignedInteger integerValue) {
            this.integerValue = integerValue;
        }

        public ChannelValue(LightingCommand lightingCommand) {
            this.lightingCommand = lightingCommand;
        }

        public Null getNullValue() {
            return nullValue;
        }

        public Real getRealValue() {
            return realValue;
        }

        public BinaryPV getBinaryValue() {
            return binaryValue;
        }

        public UnsignedInteger getIntegerValue() {
            return integerValue;
        }

        public LightingCommand getLightingCommand() {
            return lightingCommand;
        }

        public boolean isNull() {
            return nullValue != null;
        }

        public Encodable getValue() {
            if (nullValue != null)
                return nullValue;
            if (realValue != null)
                return realValue;
            if (binaryValue != null)
                return binaryValue;
            if (integerValue != null)
                return integerValue;
            return lightingCommand;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("PriorityValue(");
            if (nullValue != null)
                sb.append("nullValue=").append(nullValue);
            else if (realValue != null)
                sb.append("realValue=").append(realValue);
            else if (binaryValue != null)
                sb.append("binaryValue=").append(binaryValue);
            else if (integerValue != null)
                sb.append("integerValue=").append(integerValue);
            else if (lightingCommand != null)
                sb.append("constructedValue=").append(lightingCommand);
            sb.append(")");
            return sb.toString();
        }

        @Override
        public void write(ByteQueue queue) {
            if (nullValue != null)
                nullValue.write(queue);
            else if (realValue != null)
                realValue.write(queue);
            else if (binaryValue != null)
                binaryValue.write(queue);
            else if (integerValue != null)
                integerValue.write(queue);
            else
                lightingCommand.write(queue, 0);
        }

        public ChannelValue(ByteQueue queue) throws BACnetException {
            // Sweet Jesus...
            int tag = (queue.peek(0) & 0xff);
            if ((tag & 8) == 8) {
                // A class tag, so this is a constructed value.
                lightingCommand = read(queue, LightingCommand.class, 0);
            }
            else {
                // A primitive value
                tag = tag >> 4;
                if (tag == Null.TYPE_ID)
                    nullValue = new Null(queue);
                else if (tag == Real.TYPE_ID)
                    realValue = new Real(queue);
                else if (tag == Enumerated.TYPE_ID)
                    binaryValue = new BinaryPV(queue);
                else if (tag == UnsignedInteger.TYPE_ID)
                    integerValue = new UnsignedInteger(queue);
                else
                    throw new BACnetErrorException(ErrorClass.property, ErrorCode.invalidDataType,
                            "Unsupported primitive id: " + tag);
            }
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((binaryValue == null) ? 0 : binaryValue.hashCode());
            result = prime * result + ((integerValue == null) ? 0 : integerValue.hashCode());
            result = prime * result + ((lightingCommand == null) ? 0 : lightingCommand.hashCode());
            result = prime * result + ((nullValue == null) ? 0 : nullValue.hashCode());
            result = prime * result + ((realValue == null) ? 0 : realValue.hashCode());
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
            ChannelValue other = (ChannelValue) obj;
            if (binaryValue == null) {
                if (other.binaryValue != null)
                    return false;
            }
            else if (!binaryValue.equals(other.binaryValue))
                return false;
            if (integerValue == null) {
                if (other.integerValue != null)
                    return false;
            }
            else if (!integerValue.equals(other.integerValue))
                return false;
            if (lightingCommand == null) {
                if (other.lightingCommand != null)
                    return false;
            }
            else if (!lightingCommand.equals(other.lightingCommand))
                return false;
            if (nullValue == null) {
                if (other.nullValue != null)
                    return false;
            }
            else if (!nullValue.equals(other.nullValue))
                return false;
            if (realValue == null) {
                if (other.realValue != null)
                    return false;
            }
            else if (!realValue.equals(other.realValue))
                return false;
            return true;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((channel == null) ? 0 : channel.hashCode());
        result = prime * result + ((overridingPriority == null) ? 0 : overridingPriority.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        GroupChannelValue other = (GroupChannelValue) obj;
        if (channel == null) {
            if (other.channel != null)
                return false;
        }
        else if (!channel.equals(other.channel))
            return false;
        if (overridingPriority == null) {
            if (other.overridingPriority != null)
                return false;
        }
        else if (!overridingPriority.equals(other.overridingPriority))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        }
        else if (!value.equals(other.value))
            return false;
        return true;
    }
}
