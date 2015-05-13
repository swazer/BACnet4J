package com.serotonin.bacnet4j.transport;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.ResponseConsumer;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.npdu.NPDU;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.NetworkIdentifier;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedRequestService;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.OctetString;

/**
 * Provides segmentation support for all data link types.
 * 
 * @author Matthew
 */
public interface Transport {
    public static final int DEFAULT_TIMEOUT = 6000;
    public static final int DEFAULT_SEG_TIMEOUT = 5000;
    public static final int DEFAULT_SEG_WINDOW = 5;
    public static final int DEFAULT_RETRIES = 2;

    NetworkIdentifier getNetworkIdentifier();

    Network getNetwork();

    LocalDevice getLocalDevice();

    void setLocalDevice(LocalDevice localDevice);

    public void setTimeout(int timeout);

    public int getTimeout();

    public void setSegTimeout(int segTimeout);

    public int getSegTimeout();

    public void setRetries(int retries);

    public int getRetries();

    void initialize() throws Exception;

    void terminate();

    long getBytesOut();

    long getBytesIn();

    Address getLocalBroadcastAddress();

    void addNetworkRouter(int networkNumber, OctetString mac);

    void send(Address address, UnconfirmedRequestService service, boolean broadcast);

    ServiceFuture send(Address address, int maxAPDULengthAccepted, Segmentation segmentationSupported,
            ConfirmedRequestService service);

    void send(Address address, int maxAPDULengthAccepted, Segmentation segmentationSupported,
            ConfirmedRequestService service, ResponseConsumer consumer);

    void incoming(NPDU npdu);
}
