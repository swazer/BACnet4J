/**
 * Copyright (C) 2014 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.bacnet4j.util.sero;

import java.io.IOException;

import jssc.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Terry Packer
 * 
 */
public class JsscSerialPortOutputStream extends SerialPortOutputStream {
    static final Logger LOG = LoggerFactory.getLogger(JsscSerialPortOutputStream.class);
    private final SerialPort port;

    public JsscSerialPortOutputStream(SerialPort port) {
        this.port = port;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.OutputStream#write(int)
     */
    @Override
    public void write(int arg0) throws IOException {
        try {
            byte b = (byte) arg0;
            if (LOG.isDebugEnabled())
                LOG.debug("Writing byte: " + String.format("%02x", b));
            if ((port != null) && (port.isOpened())) {
                port.writeByte(b);
            }
        }
        catch (jssc.SerialPortException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() {
        if (LOG.isDebugEnabled())
            LOG.debug("Called no-op flush...");
        //Nothing yet
    }

}
