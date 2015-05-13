package com.serotonin.bacnet4j.npdu.ipv6;

public class Ipv6NetworkBuilder {
    private final String multicastAddress;
    private int port = Ipv6Network.DEFAULT_PORT;
    private String localBindAddress = Ipv6Network.DEFAULT_BIND_ADDRESS;
    private int localNetworkNumber = 0;

    public Ipv6NetworkBuilder(String multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    public Ipv6NetworkBuilder port(int port) {
        this.port = port;
        return this;
    }

    public Ipv6NetworkBuilder localBindAddress(String localBindAddress) {
        this.localBindAddress = localBindAddress;
        return this;
    }

    public Ipv6NetworkBuilder localNetworkNumber(int localNetworkNumber) {
        this.localNetworkNumber = localNetworkNumber;
        return this;
    }

    public Ipv6Network build() {
        return new Ipv6Network(multicastAddress, port, localBindAddress, localNetworkNumber);
    }
}
