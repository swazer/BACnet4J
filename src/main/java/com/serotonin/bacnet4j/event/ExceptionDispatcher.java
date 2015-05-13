package com.serotonin.bacnet4j.event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.sero.ByteQueue;

public class ExceptionDispatcher {
    private final List<ExceptionListener> listeners = new CopyOnWriteArrayList<ExceptionListener>();
    private final ExceptionListener defaultExceptionListener = new DefaultExceptionListener();

    public ExceptionDispatcher() {
        listeners.add(defaultExceptionListener);
    }

    public void addListener(ExceptionListener l) {
        listeners.add(l);
    }

    public void removeListener(ExceptionListener l) {
        listeners.remove(l);
    }

    public void removeDefaultExceptionListener() {
        listeners.remove(defaultExceptionListener);
    }

    public void fireUnimplementedVendorService(UnsignedInteger vendorId, UnsignedInteger serviceNumber, ByteQueue queue) {
        for (ExceptionListener l : listeners)
            l.unimplementedVendorService(vendorId, serviceNumber, queue);
    }

    public void fireReceivedException(Exception e) {
        for (ExceptionListener l : listeners)
            l.receivedException(e);
    }

    public void fireReceivedThrowable(Throwable t) {
        for (ExceptionListener l : listeners)
            l.receivedThrowable(t);
    }
}
