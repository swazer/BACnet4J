package com.serotonin.bacnet4j.npdu.ipv6;

import com.serotonin.bacnet4j.npdu.NetworkIdentifier;

public class Ipv6NetworkIdentifier extends NetworkIdentifier {
    private final String multicastAddress;
    private final int port;
    private final String localBindAddress;

    public Ipv6NetworkIdentifier(String multicastAddress, int port, String localBindAddress) {
        this.multicastAddress = multicastAddress;
        this.port = port;
        this.localBindAddress = localBindAddress;
    }

    public String getMulticastAddress() {
        return multicastAddress;
    }

    public int getPort() {
        return port;
    }

    public String getLocalBindAddress() {
        return localBindAddress;
    }

    @Override
    public String getIdString() {
        return "[" + multicastAddress + "]:" + port + " @ " + localBindAddress;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((localBindAddress == null) ? 0 : localBindAddress.hashCode());
        result = prime * result + ((multicastAddress == null) ? 0 : multicastAddress.hashCode());
        result = prime * result + port;
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
        if (localBindAddress == null) {
            if (other.localBindAddress != null)
                return false;
        }
        else if (!localBindAddress.equals(other.localBindAddress))
            return false;
        if (multicastAddress == null) {
            if (other.multicastAddress != null)
                return false;
        }
        else if (!multicastAddress.equals(other.multicastAddress))
            return false;
        if (port != other.port)
            return false;
        return true;
    }
}
