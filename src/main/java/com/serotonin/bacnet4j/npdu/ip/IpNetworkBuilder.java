package com.serotonin.bacnet4j.npdu.ip;

public class IpNetworkBuilder {
    private String broadcastIp = IpNetwork.DEFAULT_BROADCAST_IP;
    private String subnetMask = IpNetwork.DEFAULT_SUBNET_MASK;
    private int port = IpNetwork.DEFAULT_PORT;
    private String localBindAddress = IpNetwork.DEFAULT_BIND_IP;
    private int localNetworkNumber = 0;
    private boolean reuseAddress = false;

    public IpNetworkBuilder broadcastIp(String broadcastIp) {
        this.broadcastIp = broadcastIp;
        return this;
    }

    public IpNetworkBuilder subnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
        return this;
    }

    public IpNetworkBuilder port(int port) {
        this.port = port;
        return this;
    }

    public IpNetworkBuilder localBindAddress(String localBindAddress) {
        this.localBindAddress = localBindAddress;
        return this;
    }

    public IpNetworkBuilder localNetworkNumber(int localNetworkNumber) {
        this.localNetworkNumber = localNetworkNumber;
        return this;
    }

    public IpNetworkBuilder reuseAddress(boolean reuseAddress) {
        this.reuseAddress = reuseAddress;
        return this;
    }

    @SuppressWarnings("deprecation")
    public IpNetwork build() {
        return new IpNetwork(broadcastIp, port, localBindAddress, subnetMask, localNetworkNumber, reuseAddress);
    }
}
