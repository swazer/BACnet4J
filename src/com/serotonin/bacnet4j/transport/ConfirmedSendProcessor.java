package com.serotonin.bacnet4j.transport;

import java.util.List;

import com.serotonin.NotImplementedException;
import com.serotonin.bacnet4j.event.DeviceEventHandler;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bkgd.WorkItemProcessor;

public class ConfirmedSendProcessor implements WorkItemProcessor<ConfirmedSendItem> {
    private final Transport transport;
    private final DeviceEventHandler eventHandler;

    public ConfirmedSendProcessor(Transport transport, DeviceEventHandler eventHandler) {
        this.transport = transport;
        this.eventHandler = eventHandler;
    }

    @Override
    public int maxBatchSize() {
        return 1;
    }

    @Override
    public boolean process(List<ConfirmedSendItem> items) {
        throw new NotImplementedException();
    }

    @Override
    public boolean process(ConfirmedSendItem item) {
        try {
            transport.send(item.getAddress(), item.getLinkService(), item.getMaxAPDULengthAccepted(),
                    item.getSegmentationSupported(), item.getService());
        }
        catch (BACnetException e) {
            eventHandler.handleException(e);
        }
        return true;
    }
}
