package com.serotonin.bacnet4j;

import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.service.acknowledgement.AcknowledgementService;

public interface ServiceFuture<T extends AcknowledgementService> {
    T get() throws BACnetException;
}
