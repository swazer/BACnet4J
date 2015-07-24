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
package com.serotonin.bacnet4j.transport;

import com.serotonin.bacnet4j.ResponseConsumer;
import com.serotonin.bacnet4j.apdu.APDU;
import com.serotonin.bacnet4j.apdu.Segmentable;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class UnackedMessageContext {
    private long deadline;
    private int attemptsLeft;

    // The response consumer, for confirmed requests
    private final ResponseConsumer consumer;

    // The original APDU for resending in case of timeout.
    private APDU originalApdu;

    // Segment info for receiving segmented messages.
    private SegmentWindow segmentWindow;
    private Segmentable segmentedMessage;

    // Segment info for sending segmented messages.
    private Segmentable segmentTemplate;
    private ByteQueue serviceData;
    private byte[] segBuf;
    private int lastIdSent;

    public UnackedMessageContext(int timeout, int retries, ResponseConsumer consumer) {
        reset(timeout, retries);
        this.consumer = consumer;
    }

    public void retry(int timeout) {
        this.deadline = System.currentTimeMillis() + timeout;
        attemptsLeft--;
    }

    public void reset(int timeout, int retries) {
        this.deadline = System.currentTimeMillis() + timeout;
        this.attemptsLeft = retries;
    }

    public long getDeadline() {
        return deadline;
    }

    public boolean hasMoreAttempts() {
        return attemptsLeft > 0;
    }

    public ResponseConsumer getConsumer() {
        return consumer;
    }

    public APDU getOriginalApdu() {
        return originalApdu;
    }

    public void setOriginalApdu(APDU originalApdu) {
        this.originalApdu = originalApdu;
    }

    public SegmentWindow getSegmentWindow() {
        return segmentWindow;
    }

    public void setSegmentWindow(SegmentWindow segmentWindow) {
        this.segmentWindow = segmentWindow;
    }

    public Segmentable getSegmentedMessage() {
        return segmentedMessage;
    }

    public void setSegmentedMessage(Segmentable segmentedResponse) {
        this.segmentedMessage = segmentedResponse;
    }

    public boolean isExpired(long now) {
        return deadline < now;
    }

    public Segmentable getSegmentTemplate() {
        return segmentTemplate;
    }

    public void setSegmentTemplate(Segmentable segmentTemplate) {
        this.segmentTemplate = segmentTemplate;
    }

    public ByteQueue getServiceData() {
        return serviceData;
    }

    public void setServiceData(ByteQueue serviceData) {
        this.serviceData = serviceData;
    }

    public void setSegBuf(byte[] segBuf) {
        this.segBuf = segBuf;
    }

    public ByteQueue getNextSegment() {
        int count = serviceData.pop(segBuf);
        return new ByteQueue(segBuf, 0, count);
    }

    public int getLastIdSent() {
        return lastIdSent;
    }

    public void setLastIdSent(int lastIdSent) {
        this.lastIdSent = lastIdSent;
    }
}
