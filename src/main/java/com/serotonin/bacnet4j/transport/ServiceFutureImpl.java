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
import com.serotonin.bacnet4j.ServiceFuture;
import com.serotonin.bacnet4j.apdu.Abort;
import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.apdu.Reject;
import com.serotonin.bacnet4j.exception.AbortAPDUException;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.exception.ErrorAPDUException;
import com.serotonin.bacnet4j.exception.RejectAPDUException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;
import com.serotonin.bacnet4j.util.sero.ThreadUtils;

public class ServiceFutureImpl implements ServiceFuture, ResponseConsumer {
    private AcknowledgementService ack;
    private AckAPDU fail;
    private BACnetException ex;
    private volatile boolean done;
    
    private long timeout; //Timeout to wait before giving up

    public ServiceFutureImpl(long timeout){
    	
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T extends AcknowledgementService> T get() throws BACnetException {
        if (done) {
            if (ex != null)
                throw ex;
            return (T) ack;
        }

        ThreadUtils.wait(this, timeout);

        if(ex == null && ack == null && fail == null)
        	ex = new BACnetException("Timeout waiting for response.");
        
        if (ex != null)
            throw ex;

        if (fail != null) {
            if (fail instanceof com.serotonin.bacnet4j.apdu.Error)
                throw new ErrorAPDUException((com.serotonin.bacnet4j.apdu.Error) fail);
            else if (fail instanceof Reject)
                throw new RejectAPDUException((Reject) fail);
            else if (fail instanceof Abort)
                throw new AbortAPDUException((Abort) fail);
        }

        return (T) ack;
    }

    @Override
    public synchronized void success(AcknowledgementService ack) {
        this.ack = ack;
        complete();
    }

    @Override
    public synchronized void fail(AckAPDU ack) {
        fail = ack;
        complete();
    }

    @Override
    public synchronized void ex(BACnetException e) {
        ex = e;
        complete();
    }

    private void complete() {
        done = true;
        notify();
    }
}
