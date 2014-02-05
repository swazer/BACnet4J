package com.serotonin.bacnet4j.transport;

import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.primitive.OctetString;

public class ConfirmedSendItem {
    private final Address address;
    private final OctetString linkService;
    private final int maxAPDULengthAccepted;
    private final Segmentation segmentationSupported;
    private final ConfirmedRequestService service;

    public ConfirmedSendItem(Address address, OctetString linkService, int maxAPDULengthAccepted,
            Segmentation segmentationSupported, ConfirmedRequestService service) {
        this.address = address;
        this.linkService = linkService;
        this.maxAPDULengthAccepted = maxAPDULengthAccepted;
        this.segmentationSupported = segmentationSupported;
        this.service = service;
    }

    public Address getAddress() {
        return address;
    }

    public OctetString getLinkService() {
        return linkService;
    }

    public int getMaxAPDULengthAccepted() {
        return maxAPDULengthAccepted;
    }

    public Segmentation getSegmentationSupported() {
        return segmentationSupported;
    }

    public ConfirmedRequestService getService() {
        return service;
    }
}
