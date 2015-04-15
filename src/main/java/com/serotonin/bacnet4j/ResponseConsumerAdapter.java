package com.serotonin.bacnet4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;

abstract public class ResponseConsumerAdapter<T extends AcknowledgementService> implements ResponseConsumer<T> {
    static final Logger LOG = LoggerFactory.getLogger(ResponseConsumerAdapter.class);

    @Override
    public void fail(AckAPDU ack) {
        LOG.error("Request failed {}", ack);
    }

    @Override
    public void ex(BACnetException e) {
        LOG.error("Request failed", e);
    }
}
