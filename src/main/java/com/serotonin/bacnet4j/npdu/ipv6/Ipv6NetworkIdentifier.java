package com.serotonin.bacnet4j.npdu.ipv6;

import com.serotonin.bacnet4j.npdu.NetworkIdentifier;

public class Ipv6NetworkIdentifier extends NetworkIdentifier {
    private final String multicastAddress;

    public Ipv6NetworkIdentifier(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    @Override
    public String getIdString() {
        return multicastAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((multicastAddress == null) ? 0 : multicastAddress.hashCode());
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
        Ipv6NetworkIdentifier other = (Ipv6NetworkIdentifier) obj;
        if (multicastAddress == null) {
            if (other.multicastAddress != null)
                return false;
        }
        else if (!multicastAddress.equals(other.multicastAddress))
            return false;
        return true;
    }
}
