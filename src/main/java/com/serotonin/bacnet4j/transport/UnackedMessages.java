package com.serotonin.bacnet4j.transport;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.exception.BACnetRuntimeException;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.primitive.OctetString;

/**
 * This is a non-thread safe class for maintaining the list of pending requests at a local device. Access to this is
 * exclusively from Transport, which uses a single management thread.
 * 
 * @author Matthew
 */
public class UnackedMessages {
    static final Logger LOG = LoggerFactory.getLogger(UnackedMessages.class);

    private final Map<UnackedMessageKey, UnackedMessageContext> requests = new HashMap<UnackedMessageKey, UnackedMessageContext>();
    private byte nextInvokeId;

    /**
     * Add a new client-based request to the list of pending requests.
     */
    public UnackedMessageKey addClient(Address address, OctetString linkService, UnackedMessageContext ctx) {
        UnackedMessageKey key;

        // Loop until we find a key that is available.
        int attempts = 256;
        while (true) {
            // We set the server value in the key to true so that it matches with the message from the server.
            key = new UnackedMessageKey(address, linkService, nextInvokeId++, true);

            if (requests.containsKey(key)) {
                // Key collision. Try again unless we've tried too many times.
                if (--attempts > 0)
                    continue;
                throw new BACnetRuntimeException("Cannot enter a client into the un-acked messages list. key=" + key);
            }

            // Found a good id. Use it and exit.
            requests.put(key, ctx);
            break;
        }

        return key;
    }

    /**
     * Add a new server-based request to the list of pending requests. This is used for segmented responses.
     */
    public UnackedMessageKey addServer(Address address, OctetString linkService, byte id, UnackedMessageContext ctx) {
        // We set the server value in the key to false so that it matches with the message from the client.
        UnackedMessageKey key = new UnackedMessageKey(address, linkService, id, false);

        if (requests.containsKey(key))
            throw new BACnetRuntimeException("Cannot enter a server into the un-acked messages list. key=" + key);
        requests.put(key, ctx);

        return key;
    }

    public void add(UnackedMessageKey key, UnackedMessageContext value) {
        requests.put(key, value);
    }

    public UnackedMessageContext remove(UnackedMessageKey key) {
        return requests.remove(key);
    }

    public Map<UnackedMessageKey, UnackedMessageContext> getRequests() {
        return requests;
    }

    //    public AckAPDU getAck(PendingRequestKey key, long timeout, boolean throwTimeout) throws BACnetException {
    //        return (AckAPDU) getAPDU(key, timeout, throwTimeout);
    //    }
    //
    //    public ConfirmedRequest getRequest(PendingRequestKey key, long timeout, boolean throwTimeout) throws BACnetException {
    //        return (ConfirmedRequest) getAPDU(key, timeout, throwTimeout);
    //    }
    //
    //    public Segmentable getSegmentable(PendingRequestKey key, long timeout, boolean throwTimeout) throws BACnetException {
    //        APDU apdu = getAPDU(key, timeout, throwTimeout);
    //        if (apdu instanceof Abort)
    //            throw new SegmentedMessageAbortedException((Abort) apdu);
    //        try {
    //            return (Segmentable) apdu;
    //        }
    //        catch (ClassCastException e) {
    //            throw new BACnetException("Receiving an APDU of type " + apdu.getClass()
    //                    + " when expecting a Segmentable for key " + key);
    //        }
    //    }
    //
    //    public APDU getAPDU(PendingRequestKey key, long timeout, boolean throwTimeout) throws BACnetException {
    //        PendingRequestValue member = getMember(key);
    //        APDU apdu = member.getAPDU(timeout);
    //        if (apdu == null && throwTimeout)
    //            throw new BACnetTimeoutException("Timeout while waiting for APDU id " + key.getInvokeId());
    //        return apdu;
    //    }
    //
    //    public void leave(PendingRequestKey key) {
    //        synchronized (waitHere) {
    //            waitHere.remove(key);
    //            if (LOG.isLoggable(Level.FINEST))
    //                LOG.finest("WaitingRoom.leave: key=" + key);
    //        }
    //    }
    //
    //    public void notifyMember(Address address, OctetString linkService, byte id, boolean isFromServer, APDU apdu) {
    //        PendingRequestKey key = new PendingRequestKey(address, linkService, id, isFromServer);
    //        Member member = getMember(key);
    //        if (member != null) {
    //            member.setAPDU(apdu);
    //            return;
    //        }
    //
    //        // The member may not have gotten around to listening for a message yet, so enter a retry loop to
    //        // make sure that this message gets to where it's supposed to go if there is somewhere to go.
    //        int attempts = 5;
    //        long sleep = 50;
    //        while (attempts > 0) {
    //            member = getMember(key);
    //            if (member != null) {
    //                member.setAPDU(apdu);
    //                return;
    //            }
    //
    //            attempts--;
    //            try {
    //                Thread.sleep(sleep);
    //            }
    //            catch (InterruptedException e) {
    //                // no op
    //            }
    //        }
    //
    //        synchronized (waitHere) {
    //            if (LOG.isLoggable(Level.WARNING))
    //                LOG.warning("WaitingRoom.notifyMember: no waiting recipient for message: key=" + key + ", message="
    //                        + apdu + ", waiting room=" + waitHere);
    //        }
    //    }
    //
    //    private Member getMember(PendingRequestKey key) {
    //        synchronized (waitHere) {
    //            return waitHere.get(key);
    //        }
    //    }
    //
    //    /**
    //     * This class is used by network message controllers to manage the blocking of threads sending confirmed messages.
    //     * The instance itself serves as a monitor upon which the sending thread can wait (with a timeout). When a response
    //     * is received, the message controller can set it in here, automatically notifying the sending thread that the
    //     * response is available.
    //     * 
    //     * @author mlohbihler
    //     */
    //    static class Member {
    //        // A linked list is used to facilitate windows in segmented messages.
    //        private final LinkedList<APDU> apdus = new LinkedList<>();
    //
    //        synchronized void setAPDU(APDU apdu) {
    //            apdus.add(apdu);
    //            notify();
    //        }
    //
    //        synchronized APDU getAPDU(long timeout) {
    //            // Check if there is an APDU available now.
    //            APDU result = apdus.poll();
    //            if (result != null)
    //                return result;
    //
    //            // If not, wait the timeout and then check again.
    //            waitNoThrow(timeout);
    //            return apdus.poll();
    //        }
    //
    //        private void waitNoThrow(long timeout) {
    //            try {
    //                super.wait(timeout);
    //            }
    //            catch (InterruptedException e) {
    //                // Ignore
    //            }
    //        }
    //    }
}
