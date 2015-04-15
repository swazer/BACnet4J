package com.serotonin.bacnet4j.util.sero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerialPortProxyEventTask extends Thread {
    static final Logger LOG = LoggerFactory.getLogger(SerialPortProxyEventTask.class);

    private final SerialPortProxyEventListener listener;
    private final SerialPortProxyEvent event;
    private final long creationTime;
    private final SerialPortProxyEventCompleteListener completeListener;

    public SerialPortProxyEventTask(SerialPortProxyEventListener listener, SerialPortProxyEvent event,
            SerialPortProxyEventCompleteListener completeListener) {
        this.creationTime = System.currentTimeMillis();

        this.listener = listener;
        this.event = event;
        this.completeListener = completeListener;
    }

    @Override
    public void run() {

        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Running event created at: " + this.event.getCreationTime());

            this.event.setTimeExecuted(System.currentTimeMillis());
            listener.serialEvent(this.event);
        }
        catch (Exception e) {
            LOG.error("", e);
        }
        finally {
            //I'm done here
            this.completeListener.eventComplete(System.currentTimeMillis(), this);
        }
    }

    /**
     * Get the time the task was created
     * 
     * @return
     */
    public long getEventCreationTime() {
        return this.creationTime;
    }

}
