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

public class ServiceFutureImpl<T extends AcknowledgementService> implements ServiceFuture<T>, ResponseConsumer<T> {
    private T ack;
    private BACnetException ex;
    private volatile boolean done;

    @Override
    public synchronized T get() throws BACnetException {
        if (done) {
            if (ex != null)
                throw ex;
            return ack;
        }

        ThreadUtils.wait(this);

        if (ex != null)
            throw ex;
        return ack;
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized void success(AcknowledgementService ack) {
        this.ack = (T) ack;
        complete();
    }

    @Override
    public synchronized void fail(AckAPDU ack) {
        if (ack instanceof com.serotonin.bacnet4j.apdu.Error)
            ex = new ErrorAPDUException((com.serotonin.bacnet4j.apdu.Error) ack);
        else if (ack instanceof Reject)
            ex = new RejectAPDUException((Reject) ack);
        else if (ack instanceof Abort)
            ex = new AbortAPDUException((Abort) ack);
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
