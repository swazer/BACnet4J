package com.serotonin.bacnet4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;

abstract public class ResponseConsumerAdapter<T extends AcknowledgementService> implements ResponseConsumer {
    static final Logger LOG = LoggerFactory.getLogger(ResponseConsumerAdapter.class);

    @SuppressWarnings("unchecked")
    @Override
    public final void success(AcknowledgementService ack) {
        ack((T) ack);
    }

    abstract public void ack(T ack);

    @Override
    public void fail(AckAPDU ack) {
        LOG.error("Request failed {}", ack);
    }

    @Override
    public void ex(BACnetException e) {
        LOG.error("Request failed", e);
    }
}
