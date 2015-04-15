package com.serotonin.bacnet4j.npdu.test;

import com.serotonin.bacnet4j.npdu.NetworkIdentifier;

public class TestNetworkIdentifier extends NetworkIdentifier {
    @Override
    public String getIdString() {
        return "test";
    }
}
