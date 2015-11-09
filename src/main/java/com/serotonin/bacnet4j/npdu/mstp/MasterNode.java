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
package com.serotonin.bacnet4j.npdu.mstp;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.util.sero.SerialParameters;

public class MasterNode extends MstpNode {
    static final Logger LOG = LoggerFactory.getLogger(MasterNode.class);

    private enum MasterNodeState {
        idle, useToken, waitForReply, doneWithToken, passToken, noToken, pollForMaster, answerDataRequest
    }

    private final List<Frame> framesToSend = new ArrayList<Frame>();

    /**
     * The MAC address of the node to which This Station passes the token. If the Next
     * Station is unknown, NS shall be equal to TS.
     */
    private byte nextStation;

    /**
     * The MAC address of the node to which This Station last sent a Poll For Master. This is
     * used during token maintenance.
     */
    private byte pollStation;

    /**
     * A counter of transmission retries used for Token and Poll For Master transmission.
     */
    private int retryCount;

    /**
     * A Boolean flag set to TRUE by the master machine if this node is the only known master node.
     */
    private boolean soleMaster;

    /**
     * The number of tokens received by this node. When this counter reaches the value Npoll, the node
     * polls the address range between TS and NS for additional master nodes. TokenCount is set to one at
     * the end of the polling process.
     */
    private int tokenCount;

    private int maxInfoFrames = Constants.MAX_INFO_FRAMES;

    private MasterNodeState state;

    private long replyDeadline;
    private Frame replyFrame;

    //    private long lastTokenPossession;

    public MasterNode(SerialParameters serialParams, byte thisStation, int retryCount) throws IllegalArgumentException {
        super(serialParams, thisStation);
        validate(retryCount);
    }

    public MasterNode(InputStream in, OutputStream out, byte thisStation, int retryCount)
            throws IllegalArgumentException {
        super(in, out, thisStation);
        validate(retryCount);
    }

    private void validate(int retryCount) {
        int is = thisStation & 0xff;
        if (is > 127)
            throw new IllegalArgumentException("thisStation cannot be greater than 127");

        this.retryCount = retryCount;

        nextStation = thisStation;
        pollStation = thisStation;
        tokenCount = Constants.POLL;
        soleMaster = false;
        state = MasterNodeState.idle;
    }

    /**
     * @param maxInfoFrames
     *            the maxInfoFrames to set
     */
    public void setMaxInfoFrames(int maxInfoFrames) {
        if (this.maxInfoFrames < 1)
            throw new IllegalArgumentException("Cannot be less than 1");
        this.maxInfoFrames = maxInfoFrames;
    }

    public void queueFrame(FrameType type, byte destination, byte[] data) {
        if (!type.oneOf(FrameType.bacnetDataExpectingReply, FrameType.bacnetDataNotExpectingReply,
                FrameType.testRequest))
            throw new RuntimeException("Cannot send frame of type: " + type);

        Frame frame = new Frame(type, destination, thisStation, data);
        synchronized (framesToSend) {
            framesToSend.add(frame);
        }
    }

    @Override
    public void setReplyFrame(FrameType type, byte destination, byte[] data) {
        synchronized (this) {
            if (state == MasterNodeState.answerDataRequest)
                // If there is still time to reply immediately...
                replyFrame = new Frame(type, destination, thisStation, data);
            else
                // The response has already exceeded the timeout, so just queue it.
                queueFrame(type, destination, data);
        }
    }

    @Override
    protected void doCycle() {
        readFrame();

        if (state == MasterNodeState.idle)
            idle();

        if (state == MasterNodeState.useToken)
            useToken();

        if (state == MasterNodeState.waitForReply)
            waitForReply();

        if (state == MasterNodeState.doneWithToken)
            doneWithToken();

        if (state == MasterNodeState.passToken)
            passToken();

        if (state == MasterNodeState.noToken)
            noToken();

        if (state == MasterNodeState.pollForMaster)
            pollForMaster();

        if (state == MasterNodeState.answerDataRequest)
            answerDataRequest();
    }

    private void idle() {
        if (silence() >= Constants.NO_TOKEN) {
            // LostToken
            //            trace("idle:LostToken");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:LostToken");
            state = MasterNodeState.noToken;
            activity = true;
        }
        else if (receivedInvalidFrame != null) {
            // ReceivedInvalidFrame
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:Received invalid frame: " + receivedInvalidFrame);
            receivedInvalidFrame = null;
            activity = true;
        }
        else if (receivedValidFrame) {
            frame();
            receivedValidFrame = false;
            activity = true;
        }
        else {
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:other, silence=" + silence());
        }
    }

    private void frame() {
        FrameType type = frame.getFrameType();

        if (type == null) {
            // ReceivedUnwantedFrame
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:Unknown frame type");
        }
        else if (frame.broadcast()
                && type.oneOf(FrameType.token, FrameType.bacnetDataExpectingReply, FrameType.testRequest)) {
            // ReceivedUnwantedFrame
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " Frame type should not be broadcast: " + type);
        }
        else if (frame.forStation(thisStation) && type == FrameType.token) {
            // ReceivedToken
            // debug("idle:ReceivedToken from " + frame.getSourceAddress());
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:ReceivedToken");
            frameCount = 0;
            soleMaster = false;
            state = MasterNodeState.useToken;
            //
            //            long now = timeSource.currentTimeMillis();
            //            if (lastTokenPossession > 0)
            //                debug("Since last token possession: " + (now - lastTokenPossession) + " ms");
            //            else
            //                debug("Received first token possession");
            //            lastTokenPossession = now;
        }
        else if (frame.forStation(thisStation) && type == FrameType.pollForMaster) {
            // ReceivedPFM
            //            debug("idle:ReceivedPFM from " + frame.getSourceAddress());
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:ReceivedPFM");
            sendFrame(FrameType.replyToPollForMaster, frame.getSourceAddress());
        }
        else if (frame.forStationOrBroadcast(thisStation)
                && type.oneOf(FrameType.bacnetDataNotExpectingReply, FrameType.testResponse)) {
            // ReceivedDataNoReply
            //            debug("idle:ReceivedDataNoReply");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:ReceivedDataNoReply");
            receivedDataNoReply(frame);
        }
        else if (frame.forStation(thisStation)
                && type.oneOf(FrameType.bacnetDataExpectingReply, FrameType.testRequest)) {
            // ReceivedDataNeedingReply
            //            debug("idle:ReceivedDataNeedingReply");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:ReceivedDataNeedingReply");
            receivedDataNeedingReply(frame);
            state = MasterNodeState.answerDataRequest;
            replyDeadline = lastNonSilence + Constants.REPLY_DELAY;
        }
        else {
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " idle:frame-other");
        }
    }

    private void useToken() {
        Frame frameToSend = null;
        synchronized (framesToSend) {
            if (!framesToSend.isEmpty())
                frameToSend = framesToSend.remove(0);
        }

        if (frameToSend == null) {
            // NothingToSend
            //            debug("useToken:NothingToSend");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " useToken:NothingToSend");
            frameCount = maxInfoFrames;
            state = MasterNodeState.doneWithToken;
        }
        else {
            activity = true;
            if (frameToSend.getFrameType().oneOf(FrameType.testResponse, FrameType.bacnetDataNotExpectingReply)) {
                // SendNoWait
                //                debug("useToken:SendNoWait to " + frameToSend.frame.getDestinationAddress());
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " useToken:SendNoWait");
                state = MasterNodeState.doneWithToken;
            }
            else if (frameToSend.getFrameType().oneOf(FrameType.testRequest, FrameType.bacnetDataExpectingReply)) {
                // SendAndWait
                //                debug("useToken:SendAndWait to " + frameToSend.frame.getDestinationAddress());
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " useToken:SendAndWait");
                state = MasterNodeState.waitForReply;
            }
            else
                throw new RuntimeException("Unhandled frame type: " + frameToSend.getFrameType());

            sendFrame(frameToSend);
            frameCount++;
        }
    }

    private void waitForReply() {
        if (silence() > Constants.REPLY_TIMEOUT) {
            // ReplyTimeout - assume that the request has failed
            //            debug("waitForReply:ReplyTimeout");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " waitForReply:ReplyTimeout");
            frameCount = maxInfoFrames;
            state = MasterNodeState.doneWithToken;
        }
        else if (receivedInvalidFrame != null) {
            // InvalidFrame
            //            debug("waitForReply:InvalidFrame: '" + receivedInvalidFrame + "', frame=" + frame);
            if (LOG.isDebugEnabled())
                LOG.debug("Received invalid frame: " + receivedInvalidFrame);
            receivedInvalidFrame = null;
            state = MasterNodeState.doneWithToken;
            activity = true;
        }
        else if (receivedValidFrame) {
            activity = true;
            FrameType type = frame.getFrameType();

            if (frame.forStation(thisStation)) {
                if (type.oneOf(FrameType.testResponse, FrameType.bacnetDataNotExpectingReply)) {
                    // ReceivedReply
                    //debug("waitForReply:ReceivedReply from " + frame.getSourceAddress());
                    if (LOG.isDebugEnabled())
                        LOG.debug(thisStation + " waitForReply:ReceivedReply");
                    receivedDataNoReply(frame);
                }
                else if (type.oneOf(FrameType.replyPostponed)) {
                    // ReceivedPostpone
                    //debug("waitForReply:ReceivedPostpone from " + frame.getSourceAddress());
                    if (LOG.isDebugEnabled())
                        LOG.debug(thisStation + " waitForReply:ReceivedPostpone");
                    ; // the reply to the message has been postponed until a later time.
                }

                state = MasterNodeState.doneWithToken;
            }
            else if (!type.oneOf(FrameType.testResponse, FrameType.bacnetDataNotExpectingReply)) {
                // ReceivedUnexpectedFrame
                //debug("waitForReply:ReceivedUnexpectedFrame: " + frame);
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " waitForReply:ReceivedUnexpectedFrame");

                // This may indicate the presence of multiple tokens.
                state = MasterNodeState.idle;
            }

            receivedValidFrame = false;
        }
    }

    /**
     * The DONE_WITH_TOKEN state either sends another data frame, passes the token, or initiates a Poll For Master
     * cycle.
     */
    private void doneWithToken() {
        activity = true;
        if (frameCount < maxInfoFrames) {
            // SendAnotherFrame
            //debug("doneWithToken:SendAnotherFrame");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " doneWithToken:SendAnotherFrame");
            state = MasterNodeState.useToken;
        }
        else if (tokenCount < Constants.POLL - 1 && soleMaster) {
            // SoleMaster
            //debug("doneWithToken:SoleMaster");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " doneWithToken:SoleMaster");
            frameCount = 0;
            tokenCount++;
            state = MasterNodeState.useToken;
        }
        else if ((tokenCount < Constants.POLL - 1 && !soleMaster) || nextStation == adjacentStation(thisStation)) {
            // SendToken
            //debug("doneWithToken:SendToken to " + nextStation);
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " doneWithToken:SendToken");
            tokenCount++;
            sendFrame(FrameType.token, nextStation);
            retryCount = 0;
            eventCount = 0;
            state = MasterNodeState.passToken;
        }
        else if (tokenCount >= Constants.POLL - 1 && adjacentStation(pollStation) != nextStation) {
            // SendMaintenancePFM
            //debug("doneWithToken:SendMaintenancePFM to " + adjacentStation(pollStation));
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " doneWithToken:SendMaintenancePFM");
            pollStation = adjacentStation(pollStation);
            sendFrame(FrameType.pollForMaster, pollStation);
            retryCount = 0;
            state = MasterNodeState.pollForMaster;
        }
        else if (tokenCount >= Constants.POLL - 1 && adjacentStation(pollStation) == nextStation && !soleMaster) {
            // ResetMaintenancePFM
            //debug("doneWithToken:ResetMaintenancePFM: next=" + nextStation);
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " doneWithToken:ResetMaintenancePFM");
            pollStation = thisStation;
            sendFrame(FrameType.token, nextStation);
            retryCount = 0;
            eventCount = 0;
            tokenCount = 1;
            state = MasterNodeState.passToken;
        }
        // NOTE: variation from spec here ----vvv (i.e. added the "- 1")
        else if (tokenCount >= Constants.POLL - 1 && adjacentStation(pollStation) == nextStation && soleMaster) {
            // SoleMasterRestartMaintenancePFM
            //debug("doneWithToken:SoleMasterRestartMaintenancePFM: poll=" + pollStation);
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " doneWithToken:SoleMasterRestartMaintenancePFM");
            pollStation = adjacentStation(nextStation);
            sendFrame(FrameType.pollForMaster, pollStation);
            nextStation = thisStation;
            retryCount = 0;
            eventCount = 0;
            tokenCount = 1;
            state = MasterNodeState.pollForMaster;
        }
    }

    /**
     * The PASS_TOKEN state listens for a successor to begin using the token that this node has just attempted to pass.
     */
    private void passToken() {
        activity = true;
        if (silence() < Constants.USAGE_TIMEOUT && eventCount > Constants.MIN_OCTETS) {
            // SawTokenUser
            //            debug("passToken:SawTokenUser");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " passToken:SawTokenUser");
            state = MasterNodeState.idle;
        }
        else if (silence() >= Constants.USAGE_TIMEOUT && retryCount < Constants.RETRY_TOKEN) {
            // RetrySendToken
            //            debug("passToken:RetrySendToken to " + nextStation);
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " passToken:RetrySendToken");
            retryCount++;
            sendFrame(FrameType.token, nextStation);
            eventCount = 0;
        }
        else if (silence() >= Constants.USAGE_TIMEOUT && retryCount >= Constants.RETRY_TOKEN) {
            // FindNewSuccessor
            //            debug("passToken:FindNewSuccessor: trying " + adjacentStation(nextStation));
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " passToken:FindNewSuccessor");
            pollStation = adjacentStation(nextStation);
            sendFrame(FrameType.pollForMaster, pollStation);
            nextStation = thisStation;
            retryCount = 0;
            tokenCount = 0;
            eventCount = 0;
            state = MasterNodeState.pollForMaster;
        }
    }

    /**
     * The NO_TOKEN state is entered if SilenceTimer becomes greater than Tno_token, indicating that there has been no
     * network activity for that period of time. The timeout is continued to determine whether or not this node may
     * create a token.
     */
    private void noToken() {
        long silence = silence();
        long delay = Constants.NO_TOKEN + Constants.SLOT * (thisStation & 0xff);
        if (silence < delay && eventCount > Constants.MIN_OCTETS) {
            // SawFrame
            //            debug("noToken:SawFrame");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " noToken:SawFrame");
            state = MasterNodeState.idle;
            activity = true;
        }
        else if ((silence >= delay && silence < delay + Constants.SLOT) // Silence is in this master's slot.
                // Silence is beyond all slots.
                || (silence > Constants.NO_TOKEN + Constants.SLOT * (Constants.MAX_MASTER + 1))) {
            // GenerateToken
            //            debug("noToken:GenerateToken: poll=" + adjacentStation(thisStation));
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " noToken:GenerateToken");
            pollStation = adjacentStation(thisStation);
            sendFrame(FrameType.pollForMaster, pollStation);
            nextStation = thisStation;
            tokenCount = 0;
            retryCount = 0;
            eventCount = 0;
            state = MasterNodeState.pollForMaster;
            activity = true;
        }
    }

    /**
     * In the POLL_FOR_MASTER state, the node listens for a reply to a previously sent Poll For Master frame in order to
     * find a successor node.
     */
    private void pollForMaster() {
        if (receivedValidFrame) {
            if (frame.forStation(thisStation) && frame.getFrameType() == FrameType.replyToPollForMaster) {
                // ReceivedReplyToPFM
                //                debug("pollForMaster:ReceivedReplyToPFM from " + frame.getSourceAddress());
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " pollForMaster:ReceivedReplyToPFM");
                soleMaster = false;
                nextStation = frame.getSourceAddress();
                eventCount = 0;
                sendFrame(FrameType.token, nextStation);
                pollStation = thisStation;
                tokenCount = 0;
                retryCount = 0;
                receivedValidFrame = false;
                state = MasterNodeState.passToken;
            }
            else {
                // ReceivedUnexpectedFrame
                //                debug("pollForMaster:ReceivedUnexpectedFrame: " + frame);
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " pollForMaster:ReceivedUnexpectedFrame");
                receivedValidFrame = false;
                state = MasterNodeState.idle;
            }
            activity = true;
        }
        else if (soleMaster && (silence() >= Constants.USAGE_TIMEOUT || receivedInvalidFrame != null)) {
            // SoleMaster
            //            debug("pollForMaster:SoleMaster");
            if (LOG.isDebugEnabled())
                LOG.debug(thisStation + " pollForMaster:SoleMaster");
            frameCount = 0;
            receivedInvalidFrame = null;
            state = MasterNodeState.useToken;
            activity = true;
        }
        else if (!soleMaster) {
            boolean longCondition = silence() >= Constants.USAGE_TIMEOUT || receivedInvalidFrame != null;
            if (nextStation != thisStation && longCondition) {
                // DoneWithPFM
                //                debug("pollForMaster:DoneWithPFM passing token to " + nextStation);
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " pollForMaster:DoneWithPFM");
                eventCount = 0;
                sendFrame(FrameType.token, nextStation);
                retryCount = 0;
                receivedInvalidFrame = null;
                state = MasterNodeState.passToken;
                activity = true;
            }
            else if (nextStation == thisStation) {
                if (adjacentStation(pollStation) != thisStation && longCondition) {
                    // SendNextPFM
                    //                    debug("pollForMaster:SendNextPFM to " + adjacentStation(pollStation));
                    if (LOG.isDebugEnabled())
                        LOG.debug(thisStation + " pollForMaster:SendNextPFM");
                    pollStation = adjacentStation(pollStation);
                    sendFrame(FrameType.pollForMaster, pollStation);
                    retryCount = 0;
                    receivedInvalidFrame = null;
                    activity = true;
                }
                else if (adjacentStation(pollStation) == thisStation && longCondition) {
                    // DeclareSoleMaster
                    //                    debug("pollForMaster:DeclareSoleMaster");
                    if (LOG.isDebugEnabled())
                        LOG.debug(thisStation + " pollForMaster:DeclareSoleMaster");
                    soleMaster = true;
                    frameCount = 0;
                    receivedInvalidFrame = null;
                    state = MasterNodeState.useToken;
                    activity = true;
                }
            }
        }
    }

    /**
     * The ANSWER_DATA_REQUEST state is entered when a BACnet Data Expecting Reply, a Test_Request, or a proprietary
     * frame that expects a reply is received.
     */
    private void answerDataRequest() {
        synchronized (this) {
            if (replyFrame != null) {
                // Reply
                //                debug("answerDataRequest:Reply with " + replyFrame);
                if (LOG.isDebugEnabled())
                    LOG.debug(thisStation + " answerDataRequest:Reply");
                sendFrame(replyFrame);
                replyFrame = null;
                state = MasterNodeState.idle;
                activity = true;
            }
            else {
                long now = timeSource.currentTimeMillis();
                if (replyDeadline < now) {
                    // DeferredReply
                    //                debug("answerDataRequest:DeferredReply to " + frameToReply.getSourceAddress());
                    if (LOG.isDebugEnabled())
                        LOG.debug(thisStation + " answerDataRequest:DeferredReply");
                    sendFrame(FrameType.replyPostponed, frame.getSourceAddress());
                    state = MasterNodeState.idle;
                    activity = true;
                }
                else {
                    // If the current time of the host was moved back, the above condition could cause an indefinite
                    // wait. So, we check if the reply deadline is too long, and correct if so.
                    long timeDiff = replyDeadline - now;
                    if (timeDiff > Constants.REPLY_DELAY) {
                        LOG.warn("Correcting replyDeadline time because of timeDiff of " + timeDiff);
                        replyDeadline = now + Constants.REPLY_DELAY;
                    }
                }
            }
        }
    }

    private byte adjacentStation(byte station) {
        int i = station & 0xff;
        i = (i + 1) % Constants.MAX_MASTER;
        return (byte) i;
    }
}
