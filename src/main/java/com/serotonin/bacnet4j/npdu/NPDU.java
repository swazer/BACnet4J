package com.serotonin.bacnet4j.npdu;

import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.ServicesSupported;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class NPDU {
    private final Address from;
    private final OctetString linkService;
    private final boolean networkMessage;
    private final int networkMessageType;
    private final ByteQueue queue;

    /**
     * Constructor for APDU messages.
     */
    public NPDU(Address from, OctetString linkService, ByteQueue queue) {
        this.from = from;
        this.linkService = linkService;
        this.networkMessage = false;
        this.networkMessageType = -1;
        this.queue = queue;
    }

    /**
     * Constructor for network messages.
     */
    public NPDU(Address from, OctetString linkService, int networkMessageType, ByteQueue queue) {
        this.from = from;
        this.linkService = linkService;
        this.networkMessage = true;
        this.networkMessageType = networkMessageType;
        this.queue = queue;
    }

    public Address getFrom() {
        return from;
    }

    public OctetString getLinkService() {
        return linkService;
    }

    public boolean isNetworkMessage() {
        return networkMessage;
    }

    public int getNetworkMessageType() {
        return networkMessageType;
    }

    public ByteQueue getNetworkMessageData() {
        return queue;
    }

    public APDU getAPDU(ServicesSupported servicesSupported) throws BACnetException {
        try {
            return APDU.createAPDU(servicesSupported, queue);
        }
        catch (BACnetException e) {
            // If it's already a BACnetException, don't bother wrapping it.
            throw e;
        }
        catch (Exception e) {
            throw new BACnetException("Error while creating APDU: ", e);
        }
    }

    @Override
    public String toString() {
        if (networkMessage)
            return "NPDU [from=" + from + ", linkService=" + linkService + ", networkMessageType=" + networkMessageType
                    + "]";
        return "NPDU [from=" + from + ", linkService=" + linkService + ", queue=" + queue + "]";
    }
}
