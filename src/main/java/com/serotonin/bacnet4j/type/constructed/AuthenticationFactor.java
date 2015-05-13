package com.serotonin.bacnet4j.type.constructed;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.enumerated.AuthenticationFactorType;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AuthenticationFactor extends BaseType {
    private static final long serialVersionUID = -1333765637692292434L;

    private final AuthenticationFactorType formatType;
    private final UnsignedInteger formatClass;
    private final OctetString value;

    public AuthenticationFactor(AuthenticationFactorType formatType, UnsignedInteger formatClass, OctetString value) {
        this.formatType = formatType;
        this.formatClass = formatClass;
        this.value = value;
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, formatType, 0);
        write(queue, formatClass, 1);
        write(queue, value, 2);
    }

    public AuthenticationFactor(ByteQueue queue) throws BACnetException {
        formatType = read(queue, AuthenticationFactorType.class, 0);
        formatClass = read(queue, UnsignedInteger.class, 1);
        value = read(queue, OctetString.class, 2);
    }

    public AuthenticationFactorType getFormatType() {
        return formatType;
    }

    public UnsignedInteger getFormatClass() {
        return formatClass;
    }

    public OctetString getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formatClass == null) ? 0 : formatClass.hashCode());
        result = prime * result + ((formatType == null) ? 0 : formatType.hashCode());
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
        AuthenticationFactor other = (AuthenticationFactor) obj;
        if (formatClass == null) {
            if (other.formatClass != null)
                return false;
        }
        else if (!formatClass.equals(other.formatClass))
            return false;
        if (formatType == null) {
            if (other.formatType != null)
                return false;
        }
        else if (!formatType.equals(other.formatType))
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
