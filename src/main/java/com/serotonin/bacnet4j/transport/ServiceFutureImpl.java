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

    @SuppressWarnings("unchecked")
    @Override
    public synchronized <T extends AcknowledgementService> T get() throws BACnetException {
        if (done) {
            if (ex != null)
                throw ex;
            return (T) ack;
        }

        ThreadUtils.wait(this);

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
