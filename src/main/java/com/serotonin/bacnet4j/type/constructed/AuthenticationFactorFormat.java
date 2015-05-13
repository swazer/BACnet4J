package com.serotonin.bacnet4j.type.constructed;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.enumerated.AuthenticationFactorType;
import com.serotonin.bacnet4j.type.primitive.Unsigned16;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class AuthenticationFactorFormat extends BaseType {
    private static final long serialVersionUID = -6299237061566474854L;

    private final AuthenticationFactorType formatType;
    private final Unsigned16 vendorId;
    private final Unsigned16 vendorFormat;

    public AuthenticationFactorFormat(AuthenticationFactorType formatType, Unsigned16 vendorId, Unsigned16 vendorFormat) {
        super();
        this.formatType = formatType;
        this.vendorId = vendorId;
        this.vendorFormat = vendorFormat;
    }

    @Override
    public void write(ByteQueue queue) {
        write(queue, formatType, 0);
        writeOptional(queue, vendorId, 1);
        writeOptional(queue, vendorFormat, 2);
    }

    public AuthenticationFactorFormat(ByteQueue queue) throws BACnetException {
        formatType = read(queue, AuthenticationFactorType.class, 0);
        vendorId = read(queue, Unsigned16.class, 1);
        vendorFormat = read(queue, Unsigned16.class, 2);
    }

    public AuthenticationFactorType getFormatType() {
        return formatType;
    }

    public Unsigned16 getVendorId() {
        return vendorId;
    }

    public Unsigned16 getVendorFormat() {
        return vendorFormat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formatType == null) ? 0 : formatType.hashCode());
        result = prime * result + ((vendorFormat == null) ? 0 : vendorFormat.hashCode());
        result = prime * result + ((vendorId == null) ? 0 : vendorId.hashCode());
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
        AuthenticationFactorFormat other = (AuthenticationFactorFormat) obj;
        if (formatType == null) {
            if (other.formatType != null)
                return false;
        }
        else if (!formatType.equals(other.formatType))
            return false;
        if (vendorFormat == null) {
            if (other.vendorFormat != null)
                return false;
        }
        else if (!vendorFormat.equals(other.vendorFormat))
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
