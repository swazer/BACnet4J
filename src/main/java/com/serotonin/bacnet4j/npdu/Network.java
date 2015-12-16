/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.npdu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.enums.MaxApduLength;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.transport.Transport;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

abstract public class Network {
    static final Logger LOG = LoggerFactory.getLogger(Network.class);

    private final int localNetworkNumber;
    private Transport transport;

    public Network() {
        this(0);
    }

    public Network(int localNetworkNumber) {
        this.localNetworkNumber = localNetworkNumber;
    }

    public int getLocalNetworkNumber() {
        return localNetworkNumber;
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
    }

    public Transport getTransport() {
        return transport;
    }

    abstract public long getBytesOut();

    abstract public long getBytesIn();

    abstract public NetworkIdentifier getNetworkIdentifier();

    abstract public MaxApduLength getMaxApduLength();

    public void initialize(Transport transport) throws Exception {
        this.transport = transport;
    }

    abstract public void terminate();

    public final Address getLocalBroadcastAddress() {
        return new Address(localNetworkNumber, getBroadcastMAC());
    }

    abstract protected OctetString getBroadcastMAC();

    abstract public Address[] getAllLocalAddresses();

    public final void sendAPDU(Address recipient, OctetString router, APDU apdu, boolean broadcast)
            throws BACnetException {
        ByteQueue npdu = new ByteQueue();

        NPCI npci;
        if (recipient.isGlobal())
            npci = new NPCI((Address) null);
        else if (isThisNetwork(recipient)) {
            if (router != null)
                throw new RuntimeException("Invalid arguments: router address provided for local recipient " + recipient);
            npci = new NPCI(null, null, apdu.expectsReply());
        }
        else {
            if (router == null)
                throw new RuntimeException("Invalid arguments: router address not provided for remote recipient " + recipient);
            npci = new NPCI(recipient, null, apdu.expectsReply());
        }

        if (apdu.getNetworkPriority() != null)
            npci.priority(apdu.getNetworkPriority());

        npci.write(npdu);

        apdu.write(npdu);

        sendNPDU(recipient, router, npdu, broadcast, apdu.expectsReply());
    }

    public final void sendNetworkMessage(Address recipient, OctetString router, int messageType, byte[] msg,
            boolean broadcast, boolean expectsReply) throws BACnetException {
        ByteQueue npdu = new ByteQueue();

        NPCI npci;
        if (recipient.isGlobal())
            npci = new NPCI(null, null, expectsReply, messageType, 0);
        else if (isThisNetwork(recipient)) {
            if (router != null)
                throw new RuntimeException("Invalid arguments: router address provided for a local recipient");
            npci = new NPCI(null, null, expectsReply, messageType, 0);
        }
        else {
            if (router == null)
                throw new RuntimeException("Invalid arguments: router address not provided for a remote recipient");
            npci = new NPCI(recipient, null, expectsReply, messageType, 0);
        }
        npci.write(npdu);

        // Network message
        if (msg != null)
            npdu.push(msg);

        sendNPDU(recipient, router, npdu, broadcast, expectsReply);
    }

    abstract protected void sendNPDU(Address recipient, OctetString router, ByteQueue npdu, boolean broadcast,
            boolean expectsReply) throws BACnetException;

    protected OctetString getDestination(Address recipient, OctetString link) {
        if (recipient.isGlobal())
            return getLocalBroadcastAddress().getMacAddress();
        if (link != null)
            return link;
        return recipient.getMacAddress();
    }

    protected boolean isThisNetwork(Address address) {
        int nn = address.getNetworkNumber().intValue();
        return nn == Address.LOCAL_NETWORK || nn == localNetworkNumber;
    }

    protected void handleIncomingData(ByteQueue queue, OctetString linkService) {
        try {
            NPDU npdu = handleIncomingDataImpl(queue, linkService);
            if (npdu != null) {
                LOG.debug("Received NPDU from {}: {}", linkService, npdu);
                getTransport().incoming(npdu);
            }
        }
        catch (Exception e) {
            transport.getLocalDevice().getExceptionDispatcher().fireReceivedException(e);
        }
        catch (Throwable t) {
            transport.getLocalDevice().getExceptionDispatcher().fireReceivedThrowable(t);
        }
    }

    abstract protected NPDU handleIncomingDataImpl(ByteQueue queue, OctetString linkService) throws Exception;

    public NPDU parseNpduData(ByteQueue queue, OctetString linkService) throws MessageValidationException {
        // Network layer protocol control information. See 6.2.2
        NPCI npci = new NPCI(queue);
        if (npci.getVersion() != 1)
            throw new MessageValidationException("Invalid protocol version: " + npci.getVersion());

        // Check the destination network number and ignore foreign networks requests  
        if (npci.hasDestinationInfo()) {
            int destNet = npci.getDestinationNetwork();
            if (destNet > 0 && destNet != 0xffff && getLocalNetworkNumber() > 0 && getLocalNetworkNumber() != destNet)
                return null;
        }

        Address from;
        if (npci.hasSourceInfo())
            from = new Address(npci.getSourceNetwork(), npci.getSourceAddress());
        else
            from = new Address(linkService);

        if (isThisNetwork(from))
            linkService = null;

        if (npci.isNetworkMessage())
            // Network message
            return new NPDU(from, linkService, npci.getMessageType(), queue);

        // APDU message
        return new NPDU(from, linkService, queue);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + localNetworkNumber;
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
        Network other = (Network) obj;
        if (localNetworkNumber != other.localNetworkNumber)
            return false;
        return true;
    }
}
