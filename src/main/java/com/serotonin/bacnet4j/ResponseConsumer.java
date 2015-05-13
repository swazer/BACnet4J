package com.serotonin.bacnet4j;

import com.serotonin.bacnet4j.apdu.AckAPDU;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;

public interface ResponseConsumer {
    void success(AcknowledgementService ack);

    void fail(AckAPDU ack);

    void ex(BACnetException e);
}
