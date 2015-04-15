package com.serotonin.bacnet4j.transport;

import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;

public class UnackedMessageKey {
    private final Address address;
    private final OctetString linkService;
    private final byte invokeId;
    private final boolean fromServer;

    public UnackedMessageKey(Address address, OctetString linkService, byte invokeId, boolean fromServer) {
        this.address = address;
        this.linkService = linkService;
        this.invokeId = invokeId;
        this.fromServer = fromServer;
    }

    public Address getAddress() {
        return address;
    }

    public OctetString getLinkService() {
        return linkService;
    }

    public byte getInvokeId() {
        return invokeId;
    }

    public boolean isFromServer() {
        return fromServer;
    }

    @Override
    public String toString() {
        return "Key(address=" + address + ", linkService=" + linkService + ", invokeId=" + invokeId + ", fromServer="
                + fromServer + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + (fromServer ? 1231 : 1237);
        result = prime * result + invokeId;
        result = prime * result + ((linkService == null) ? 0 : linkService.hashCode());
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
        UnackedMessageKey other = (UnackedMessageKey) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        }
        else if (!address.equals(other.address))
            return false;
        if (fromServer != other.fromServer)
            return false;
        if (invokeId != other.invokeId)
            return false;
        if (linkService == null) {
            if (other.linkService != null)
                return false;
        }
        else if (!linkService.equals(other.linkService))
            return false;
        return true;
    }
}
