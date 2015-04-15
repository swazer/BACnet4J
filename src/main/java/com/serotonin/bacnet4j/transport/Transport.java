package com.serotonin.bacnet4j.transport;

import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.ResponseConsumer;
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.apdu.Abort;
import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.apdu.ComplexACK;
import com.serotonin.bacnet4j.apdu.ConfirmedRequest;
import com.serotonin.bacnet4j.apdu.Reject;
import com.serotonin.bacnet4j.apdu.SegmentACK;
import com.serotonin.bacnet4j.apdu.Segmentable;
import com.serotonin.bacnet4j.apdu.SimpleACK;
import com.serotonin.bacnet4j.apdu.UnconfirmedRequest;
import com.serotonin.bacnet4j.enums.MaxSegments;
import com.serotonin.bacnet4j.event.ExceptionDispatch;
import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.BACnetRejectException;
import com.serotonin.bacnet4j.exception.BACnetTimeoutException;
import com.serotonin.bacnet4j.exception.NotImplementedException;
import com.serotonin.bacnet4j.exception.ServiceTooBigException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.NetworkIdentifier;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.service.confirmed.ConfirmedRequestService;
import com.serotonin.bacnet4j.service.unconfirmed.UnconfirmedRequestService;
import com.serotonin.bacnet4j.type.constructed.Address;
import com.serotonin.bacnet4j.type.constructed.BACnetError;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.Segmentation;
import com.serotonin.bacnet4j.type.error.BaseError;
import com.serotonin.bacnet4j.type.primitive.OctetString;
import com.serotonin.bacnet4j.util.sero.ByteQueue;
import com.serotonin.bacnet4j.util.sero.ThreadUtils;

/**
 * Provides segmentation support for all data link types.
 * 
 * @author Matthew
 */
public class Transport implements Runnable {
    public static final int DEFAULT_TIMEOUT = 6000;
    public static final int DEFAULT_SEG_TIMEOUT = 5000;
    public static final int DEFAULT_SEG_WINDOW = 5;
    public static final int DEFAULT_RETRIES = 2;

    static final Logger LOG = LoggerFactory.getLogger(Transport.class);
    static final MaxSegments MAX_SEGMENTS = MaxSegments.MORE_THAN_64;

    // Configuration
    private LocalDevice localDevice;
    final Network network;
    int timeout = DEFAULT_TIMEOUT;
    int retries = DEFAULT_RETRIES;
    int segTimeout = DEFAULT_SEG_TIMEOUT;
    int segWindow = DEFAULT_SEG_WINDOW;

    // Message queues
    private final Queue<Outgoing> outgoing = new ConcurrentLinkedQueue<Outgoing>();
    private final Queue<Incoming> incoming = new ConcurrentLinkedQueue<Incoming>();

    // Processing
    final UnackedMessages unackedMessages = new UnackedMessages();
    private Thread thread;
    private volatile boolean running = true;
    private final Object pauseLock = new Object();

    public Transport(Network network) {
        this.network = network;
    }

    //
    //
    // Configuration
    //
    public NetworkIdentifier getNetworkIdentifier() {
        return network.getNetworkIdentifier();
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setSegTimeout(int segTimeout) {
        this.segTimeout = segTimeout;
    }

    public int getSegTimeout() {
        return segTimeout;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public int getRetries() {
        return retries;
    }

    public void setSegWindow(int segWindow) {
        this.segWindow = segWindow;
    }

    public int getSegWindow() {
        return segWindow;
    }

    public Network getNetwork() {
        return network;
    }

    public LocalDevice getLocalDevice() {
        return localDevice;
    }

    public void setLocalDevice(LocalDevice localDevice) {
        this.localDevice = localDevice;
    }

    public void initialize() throws Exception {
        network.initialize(this);
        thread = new Thread(this);
        thread.start();
    }

    public void terminate() {
        running = false;
        ThreadUtils.notifySync(pauseLock);
        if (thread == null)
            ThreadUtils.join(thread);
        network.terminate();
    }

    public long getBytesOut() {
        return network.getBytesOut();
    }

    public long getBytesIn() {
        return network.getBytesIn();
    }

    public Address getLocalBroadcastAddress() {
        return network.getLocalBroadcastAddress();
    }

    //
    //
    // Adding new requests and responses.
    //
    public void send(Address address, OctetString linkService, UnconfirmedRequestService service, boolean broadcast) {
        outgoing.add(new OutgoingUnconfirmed(address, linkService, service, broadcast));
        ThreadUtils.notifySync(pauseLock);
    }

    public <T extends AcknowledgementService> ServiceFuture<T> send(Address address, OctetString linkService,
            int maxAPDULengthAccepted, Segmentation segmentationSupported, ConfirmedRequestService service) {
        ServiceFutureImpl<T> future = new ServiceFutureImpl<T>();
        send(address, linkService, maxAPDULengthAccepted, segmentationSupported, service, future);
        return future;
    }

    public <T extends AcknowledgementService> void send(Address address, OctetString linkService,
            int maxAPDULengthAccepted, Segmentation segmentationSupported, ConfirmedRequestService service,
            ResponseConsumer<T> consumer) {
        outgoing.add(new OutgoingConfirmed<T>(address, linkService, maxAPDULengthAccepted, segmentationSupported,
                service, consumer));
        ThreadUtils.notifySync(pauseLock);
    }

    public void incoming(APDU apdu, final Address address, final OctetString linkService) {
        incoming.add(new Incoming(apdu, address, linkService));
        ThreadUtils.notifySync(pauseLock);
    }

    abstract class Outgoing {
        protected final Address address;
        protected final OctetString linkService;

        public Outgoing(Address address, OctetString linkService) {
            if (address == null)
                throw new IllegalArgumentException("address cannot be null");
            if (address.equals(linkService))
                linkService = null;

            this.address = address;
            this.linkService = linkService;
        }

        abstract void sendImpl();
    }

    class OutgoingConfirmed<T extends AcknowledgementService> extends Outgoing {
        private final int maxAPDULengthAccepted;
        private final Segmentation segmentationSupported;
        private final ConfirmedRequestService service;
        private final ResponseConsumer<T> consumer;

        public OutgoingConfirmed(Address address, OctetString linkService, int maxAPDULengthAccepted,
                Segmentation segmentationSupported, ConfirmedRequestService service, ResponseConsumer<T> consumer) {
            super(address, linkService);
            this.maxAPDULengthAccepted = maxAPDULengthAccepted;
            this.segmentationSupported = segmentationSupported;
            this.service = service;
            this.consumer = consumer;
        }

        @Override
        void sendImpl() {
            try {
                _sendImpl();
            }
            catch (BACnetException e) {
                consumer.ex(e);
            }
        }

        void _sendImpl() throws BACnetException {
            ByteQueue serviceData = new ByteQueue();
            service.write(serviceData);

            UnackedMessageContext ctx = new UnackedMessageContext(timeout, retries, consumer);
            UnackedMessageKey key = unackedMessages.addClient(address, linkService, ctx);

            APDU apdu;

            // Check if we need to segment the message.
            if (serviceData.size() > maxAPDULengthAccepted - ConfirmedRequest.getHeaderSize(false)) {
                int maxServiceData = maxAPDULengthAccepted - ConfirmedRequest.getHeaderSize(true);
                // Check if the device can accept what we want to send.
                if (segmentationSupported.intValue() == Segmentation.noSegmentation.intValue()
                        || segmentationSupported.intValue() == Segmentation.segmentedTransmit.intValue())
                    throw new ServiceTooBigException("Request too big to send to device without segmentation");
                int segmentsRequired = serviceData.size() / maxServiceData + 1;
                if (segmentsRequired > 128)
                    throw new ServiceTooBigException("Request too big to send to device; too many segments required");

                // Prepare the segmenting session.
                ctx.setSegmentTemplate(new ConfirmedRequest(true, true, true, MAX_SEGMENTS, network.getMaxApduLength(),
                        key.getInvokeId(), 0, segWindow, service.getChoiceId(), null));
                ctx.setServiceData(serviceData);
                ctx.setSegBuf(new byte[maxServiceData]);

                // Send an initial message to negotiate communication terms.
                apdu = ctx.getSegmentTemplate().clone(true, 0, segWindow, ctx.getNextSegment());
            }
            else
                // We can send the whole APDU in one shot.
                apdu = new ConfirmedRequest(false, false, true, MAX_SEGMENTS, network.getMaxApduLength(),
                        key.getInvokeId(), (byte) 0, 0, service.getChoiceId(), serviceData);

            ctx.setOriginalApdu(apdu);
            sendForResponse(key, ctx);
        }
    }

    class OutgoingUnconfirmed extends Outgoing {
        private final UnconfirmedRequestService service;
        private final boolean broadcast;

        public OutgoingUnconfirmed(Address address, OctetString linkService, UnconfirmedRequestService service,
                boolean broadcast) {
            super(address, linkService);
            this.service = service;
            this.broadcast = broadcast;
        }

        @Override
        void sendImpl() {
            try {
                network.sendAPDU(address, linkService, new UnconfirmedRequest(service), broadcast);
            }
            catch (BACnetException e) {
                LOG.error("Error during send", e);
            }
        }
    }

    class Incoming {
        final APDU apdu;
        final Address address;
        final OctetString linkService;

        public Incoming(APDU apdu, Address address, OctetString linkService) {
            this.apdu = apdu;
            this.address = address;
            this.linkService = linkService;
        }
    }

    //
    //
    // Processing
    //
    @Override
    public void run() {
        Outgoing out;
        Incoming in;
        boolean pause;

        while (running) {
            pause = true;

            // Send an outgoing message.
            out = outgoing.poll();
            if (out != null) {
                try {
                    out.sendImpl();
                }
                catch (Exception e) {
                    LOG.error("Error during send: {}", out, e);
                }
                pause = false;
            }

            // Receive an incoming message.
            in = incoming.poll();
            if (in != null) {
                try {
                    receiveImpl(in);
                }
                catch (Exception e) {
                    LOG.error("Error during receive: {}", in, e);
                }
                pause = false;
            }

            if (pause && running)
                pause = expire();

            if (pause && running)
                ThreadUtils.waitSync(pauseLock, 50);
        }
    }

    private void receiveImpl(Incoming in) {
        if (in.apdu instanceof ConfirmedRequest) {
            // Received a request that must be handled and responded to.
            final ConfirmedRequest confAPDU = (ConfirmedRequest) in.apdu;
            final byte invokeId = confAPDU.getInvokeId();

            if (confAPDU.isSegmentedMessage()) {
                UnackedMessageKey key = new UnackedMessageKey(in.address, in.linkService, invokeId, false);
                UnackedMessageContext ctx;
                if (confAPDU.getSequenceNumber() == 0)
                    // This is the first segment
                    ctx = new UnackedMessageContext(timeout, retries, null);
                else {
                    ctx = unackedMessages.remove(key);
                    if (ctx == null)
                        LOG.warn("Received an request segment for an unknown request: {}", confAPDU);
                }

                try {
                    segmentedIncoming(key, confAPDU, ctx);
                }
                catch (BACnetException e) {
                    LOG.warn("Error handling incoming request", e);
                    com.serotonin.bacnet4j.apdu.Error error = new com.serotonin.bacnet4j.apdu.Error(
                            confAPDU.getInvokeId(), new BaseError((byte) 127, new BACnetError(ErrorClass.services,
                                    ErrorCode.operationalProblem)));
                    try {
                        network.sendAPDU(in.address, in.linkService, error, false);
                    }
                    catch (BACnetException e1) {
                        LOG.warn("Error sending error response", e);
                    }
                    ExceptionDispatch.fireReceivedException(e);
                }
            }
            else
                // Just handle the message.
                incomingConfirmedRequest(confAPDU, in.address, in.linkService, invokeId);
        }
        else if (in.apdu instanceof UnconfirmedRequest) {
            // Received a request that must be handled with no response.
            UnconfirmedRequest ur = (UnconfirmedRequest) in.apdu;

            try {
                ur.parseServiceData();
                ur.getService().handle(localDevice, in.address, in.linkService);
            }
            catch (BACnetRejectException e) {
                // Ignore
            }
            catch (BACnetException e) {
                ExceptionDispatch.fireReceivedException(e);
            }
        }
        else {
            // Must be an acknowledgement
            LOG.debug("incomingApdu: recieved an acknowledgement");

            AckAPDU ack = (AckAPDU) in.apdu;
            UnackedMessageKey key = new UnackedMessageKey(in.address, in.linkService, ack.getOriginalInvokeId(),
                    ack.isServer());
            UnackedMessageContext ctx = unackedMessages.remove(key);

            if (ctx == null)
                LOG.warn("Received an acknowledgement for an unknown request: {}", ack);
            else if (ack instanceof SegmentACK)
                segmentedOutgoing(key, ctx, (SegmentACK) ack);
            else if (ctx.getConsumer() != null) {
                ResponseConsumer<? extends AcknowledgementService> consumer = ctx.getConsumer();

                if (ack instanceof SimpleACK)
                    consumer.success(null);
                else if (ack instanceof ComplexACK) {
                    ComplexACK cack = ((ComplexACK) ack);
                    if (cack.isSegmentedMessage()) {
                        try {
                            segmentedIncoming(key, cack, ctx);
                        }
                        catch (BACnetException e) {
                            consumer.ex(e);
                        }
                    }
                    else
                        completeComplexAckResponse(cack, consumer);
                }
                else if (ack instanceof com.serotonin.bacnet4j.apdu.Error)
                    consumer.fail(ack);
                else if (ack instanceof Reject)
                    consumer.fail(ack);
                else if (ack instanceof Abort)
                    consumer.fail(ack);
                else
                    LOG.error("Unexpected ack APDU: " + ack);
            }
        }
    }

    private void segmentedIncoming(UnackedMessageKey key, Segmentable msg, UnackedMessageContext ctx)
            throws BACnetException {
        int windowSize = msg.getProposedWindowSize();
        int lastSeq = msg.getSequenceNumber() & 0xff;

        if (ctx.getSegmentWindow() == null) {
            // This is the first segment.
            ctx.setSegmentWindow(new SegmentWindow(windowSize, lastSeq + 1));
            ctx.setSegmentedMessage(msg);

            // Send a segment acknowledgement going with the proposed window size.
            network.sendAPDU(key.getAddress(), key.getLinkService(),
                    new SegmentACK(false, !key.isFromServer(), msg.getInvokeId(), lastSeq, windowSize, true), false);
        }
        else {
            SegmentWindow segmentWindow = ctx.getSegmentWindow();

            if (!segmentWindow.fitsInWindow(msg))
                throw new BACnetException("Segment did not fit in segment window");
            segmentWindow.setSegment(msg);

            // Do we need to send an ack?
            if (!msg.isMoreFollows() || segmentWindow.isFull()) {
                // Send an acknowledgement
                network.sendAPDU(
                        key.getAddress(),
                        key.getLinkService(),
                        new SegmentACK(false, !key.isFromServer(), msg.getInvokeId(), lastSeq, windowSize, msg
                                .isMoreFollows()), false);

                // Append the window onto the original response.
                for (Segmentable segment : segmentWindow.getSegments()) {
                    if (segment != null)
                        ctx.getSegmentedMessage().appendServiceData(segment.getServiceData());
                }
                segmentWindow.clear(lastSeq + 1);
            }
        }

        if (msg.isMoreFollows()) {
            // Put the value back in the pending requests.
            ctx.reset(segTimeout * 4, 0);
            unackedMessages.add(key, ctx);
        }
        else if (msg instanceof ComplexACK)
            // We're done receiving the segmented response.
            completeComplexAckResponse((ComplexACK) ctx.getSegmentedMessage(), ctx.getConsumer());
        else
            // We're done receiving the segmented request.
            incomingConfirmedRequest((ConfirmedRequest) ctx.getSegmentedMessage(), key.getAddress(),
                    key.getLinkService(), msg.getInvokeId());
    }

    private void completeComplexAckResponse(ComplexACK cack, ResponseConsumer<? extends AcknowledgementService> consumer) {
        try {
            cack.parseServiceData();
            consumer.success(cack.getService());
        }
        catch (BACnetException e) {
            consumer.ex(e);
        }
    }

    /**
     * The first part of the segmented message has already been sent. This is called each time a segment ack is
     * received.
     * 
     * This method handles outgoing segmented requests and responses.
     */
    private void segmentedOutgoing(UnackedMessageKey key, UnackedMessageContext ctx, SegmentACK ack) {
        // TODO handle NAK

        if (ctx.getServiceData().size() == 0) {
            // There any no more segments to send. If this is a request, expect the response.
            if (ctx.getOriginalApdu() instanceof ConfirmedRequest)
                unackedMessages.add(key, ctx);
            // However, if this is a response, there is nothing left to do.
            return;
        }

        // This may be a segment ack for an inter-window segment. We ignore all segment acks except for the
        // one for the last segment that was sent.
        if (ack.getSequenceNumber() < ctx.getLastIdSent())
            return;

        int remaining = ack.getActualWindowSize();

        // Send the next window of messages.
        int sequenceNumber = ctx.getLastIdSent();
        while (remaining > 0 && ctx.getServiceData().size() > 0) {
            ByteQueue segData = ctx.getNextSegment();
            APDU segment = ctx.getSegmentTemplate().clone(ctx.getServiceData().size() > 0, ++sequenceNumber,
                    ack.getActualWindowSize(), segData);

            try {
                network.sendAPDU(key.getAddress(), key.getLinkService(), segment, false);
            }
            catch (BACnetException e) {
                ctx.getConsumer().ex(e);
                return;
            }

            remaining--;
        }
        ctx.setLastIdSent(sequenceNumber);

        // Expect the segment ack.
        unackedMessages.add(key, ctx);
    }

    private void incomingConfirmedRequest(ConfirmedRequest confAPDU, Address address, OctetString linkService,
            byte invokeId) {
        try {
            try {
                confAPDU.parseServiceData();
                AcknowledgementService ackService = handleConfirmedRequest(address, linkService, invokeId,
                        confAPDU.getServiceRequest());
                sendConfirmedResponse(address, linkService, confAPDU, ackService);
            }
            catch (BACnetErrorException e) {
                network.sendAPDU(address, linkService, new com.serotonin.bacnet4j.apdu.Error(invokeId, e.getError()),
                        false);
            }
            catch (BACnetRejectException e) {
                network.sendAPDU(address, linkService, new Reject(invokeId, e.getRejectReason()), false);
            }
            catch (BACnetException e) {
                LOG.warn("Error handling incoming request", e);
                com.serotonin.bacnet4j.apdu.Error error = new com.serotonin.bacnet4j.apdu.Error(confAPDU.getInvokeId(),
                        new BaseError((byte) 127, new BACnetError(ErrorClass.services, ErrorCode.operationalProblem)));
                network.sendAPDU(address, linkService, error, false);
                ExceptionDispatch.fireReceivedException(e);
            }
        }
        catch (BACnetException e) {
            ExceptionDispatch.fireReceivedException(e);
        }
    }

    private AcknowledgementService handleConfirmedRequest(Address from, OctetString linkService, byte invokeId,
            ConfirmedRequestService service) throws BACnetException {
        try {
            return service.handle(localDevice, from, linkService);
        }
        catch (NotImplementedException e) {
            LOG.warn("Unsupported confirmed request: invokeId=" + invokeId + ", from=" + from + ", request="
                    + service.getClass().getName());
            throw new BACnetErrorException(ErrorClass.services, ErrorCode.serviceRequestDenied);
        }
        catch (BACnetErrorException e) {
            throw e;
        }
        catch (Exception e) {
            throw new BACnetErrorException(ErrorClass.device, ErrorCode.operationalProblem);
        }
    }

    private void sendConfirmedResponse(final Address address, final OctetString linkService,
            final ConfirmedRequest request, final AcknowledgementService response) throws BACnetException {
        if (response == null)
            network.sendAPDU(address, linkService, new SimpleACK(request.getInvokeId(), request.getServiceRequest()
                    .getChoiceId()), false);
        else {
            // A complex ack response. Serialize the data.
            final ByteQueue serviceData = new ByteQueue();
            response.write(serviceData);

            // Check if we need to segment the message.
            if (serviceData.size() > request.getMaxApduLengthAccepted().getMaxLength()
                    - ComplexACK.getHeaderSize(false)) {
                final int maxServiceData = request.getMaxApduLengthAccepted().getMaxLength()
                        - ComplexACK.getHeaderSize(true);
                // Check if the device can accept what we want to send.
                if (!request.isSegmentedResponseAccepted())
                    throw new ServiceTooBigException("Response too big to send to device without segmentation");
                int segmentsRequired = serviceData.size() / maxServiceData + 1;
                if (segmentsRequired > request.getMaxSegmentsAccepted().getMaxSegments() || segmentsRequired > 128)
                    throw new ServiceTooBigException("Response too big to send to device; too many segments required");

                // Prepare the segmenting session.
                UnackedMessageContext ctx = new UnackedMessageContext(timeout, retries, null);
                UnackedMessageKey key = unackedMessages.addServer(address, linkService, request.getInvokeId(), ctx);

                ctx.setSegmentTemplate(new ComplexACK(true, true, request.getInvokeId(), 0, segWindow, response
                        .getChoiceId(), null));
                ctx.setServiceData(serviceData);
                ctx.setSegBuf(new byte[maxServiceData]);

                // Send an initial message to negotiate communication terms.
                APDU apdu = ctx.getSegmentTemplate().clone(true, 0, segWindow, ctx.getNextSegment());

                ctx.setOriginalApdu(apdu);
                sendForResponse(key, ctx);
            }
            else
                // We can send the whole APDU in one shot.
                network.sendAPDU(address, linkService, new ComplexACK(false, false, request.getInvokeId(), 0, 0,
                        response), false);
        }
    }

    private boolean expire() {
        boolean didSomething = false;

        long now = System.currentTimeMillis();
        Iterator<Map.Entry<UnackedMessageKey, UnackedMessageContext>> iter = unackedMessages.getRequests().entrySet()
                .iterator();
        while (iter.hasNext()) {
            Map.Entry<UnackedMessageKey, UnackedMessageContext> e = iter.next();
            UnackedMessageKey key = e.getKey();
            UnackedMessageContext ctx = e.getValue();
            if (ctx.isExpired(now)) {
                if (ctx.hasMoreAttempts()) {
                    // Resend
                    ctx.retry(timeout);
                    sendForResponse(key, ctx);
                }
                else {
                    // Timeout
                    if (ctx.getSegmentWindow() == null) {
                        // Not a segmented message, at least as far as we know.
                        ctx.getConsumer().ex(new BACnetTimeoutException());
                        iter.remove();
                    }
                    else {
                        // A segmented message.
                        if (ctx.getSegmentWindow().isEmpty() && ctx.getConsumer() != null) {
                            // No segments received. Return a timeout.
                            ctx.getConsumer().ex(
                                    new BACnetTimeoutException("Timeout while waiting for segment part: invokeId="
                                            + key.getInvokeId() + ", sequenceId="
                                            + ctx.getSegmentWindow().getFirstSequenceId()));
                            iter.remove();
                        }
                        else if (ctx.getSegmentWindow().isEmpty())
                            LOG.warn("No segments received for message " + ctx.getOriginalApdu());
                        else {
                            // Return a NAK with the last sequence id received in order and start over.
                            try {
                                network.sendAPDU(key.getAddress(), key.getLinkService(),
                                        new SegmentACK(true, key.isFromServer(), key.getInvokeId(), ctx
                                                .getSegmentWindow().getLatestSequenceId(), ctx.getSegmentWindow()
                                                .getWindowSize(), true), false);
                            }
                            catch (BACnetException ex) {
                                ctx.getConsumer().ex(ex);
                                iter.remove();
                            }
                        }
                    }
                }

                didSomething = true;
            }
        }

        return !didSomething;
    }

    void sendForResponse(UnackedMessageKey key, UnackedMessageContext ctx) {
        try {
            network.sendAPDU(key.getAddress(), key.getLinkService(), ctx.getOriginalApdu(), false);
        }
        catch (BACnetException e) {
            unackedMessages.remove(key);
            if (ctx.getConsumer() != null)
                ctx.getConsumer().ex(e);
            else
                LOG.error("", e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((network == null) ? 0 : network.hashCode());
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
        Transport other = (Transport) obj;
        if (network == null) {
            if (other.network != null)
                return false;
        }
        else if (!network.equals(other.network))
            return false;
        return true;
    }
}
