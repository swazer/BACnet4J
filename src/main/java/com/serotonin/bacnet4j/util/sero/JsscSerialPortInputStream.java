/*
 * ============================================================================
 * GNU General Public License
 * ============================================================================
 *
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * When signing a commercial license with Infinite Automation Software,
 * the following extension to GPL is made. A special exception to the GPL is
 * included to allow you to distribute a combined work that includes BAcnet4J
 * without being obliged to provide the source code for any proprietary components.
 *
 * See www.infiniteautomation.com for commercial license options.
 * 
 * @author Matthew Lohbihler
 */
package com.serotonin.bacnet4j.util.sero;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Serial port input stream that processes incoming messages is separate threads but in Order.
 * 
 * The max pool size restricts runaway resource usage. When the pool is full the Recieve events block
 * until the pool is small enough to accept more tasks.
 * 
 * @author Terry Packer
 */
public class JsscSerialPortInputStream extends SerialPortInputStream implements SerialPortEventListener,
        SerialPortProxyEventCompleteListener {
    static final Logger LOG = LoggerFactory.getLogger(JsscSerialPortInputStream.class);
    protected LinkedBlockingQueue<Byte> dataStream;
    protected SerialPort port;
    protected List<SerialPortProxyEventListener> listeners;

    //Thread Pool for executing listeners in separate threads,
    // when a 
    private final BlockingQueue<SerialPortProxyEventTask> listenerTasks;
    private final int maxPoolSize = 20; //Default to 20;

    /**
     * @param serialPort
     * @throws SerialPortException
     */
    public JsscSerialPortInputStream(SerialPort serialPort, List<SerialPortProxyEventListener> listeners)
            throws jssc.SerialPortException {
        this.listeners = listeners;
        this.dataStream = new LinkedBlockingQueue<Byte>();

        this.port = serialPort;
        this.port.addEventListener(this, SerialPort.MASK_RXCHAR);

        //Setup a bounded Pool that will execute the listener tasks in Order
        this.listenerTasks = new ArrayBlockingQueue<SerialPortProxyEventTask>(this.maxPoolSize);

        if (LOG.isDebugEnabled())
            LOG.debug("Creating Jssc Serial Port Input Stream for: " + serialPort.getPortName());

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read()
     */
    @Override
    public int read() throws IOException {
        synchronized (dataStream) {
            try {
                if (dataStream.size() > 0)
                    return dataStream.take() & 0xFF; //Return unsigned byte value by masking off the high order bytes in the returned int
                return -1;
            }
            catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public int available() throws IOException {
        synchronized (dataStream) {
            return this.dataStream.size();
        }
    }

    @Override
    public void closeImpl() throws IOException {
        try {
            this.port.removeEventListener(); //Remove the listener
        }
        catch (jssc.SerialPortException e) {
            throw new IOException(e);
        }

    }

    /**
     * Peek at the head of the stream, do not remove the byte
     * 
     * @return
     */
    @Override
    public int peek() {
        return this.dataStream.peek();
    }

    /**
     * Serial Event Executed
     */
    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {//If data is available
            if (LOG.isDebugEnabled())
                LOG.debug("Serial Receive Event fired.");
            //Read the bytes, store into queue
            try {
                synchronized (dataStream) {
                    byte[] buffer = this.port.readBytes();
                    for (int i = 0; i < buffer.length; i++) {
                        this.dataStream.put(buffer[i]);
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Recieved: " + StreamUtils.dumpHex(buffer, 0, buffer.length));
                    }
                }

            }
            catch (Exception e) {
                LOG.error("", e);
            }

            //TODO Add max limit to number of threads we can create until blocking
            // basically use a thread pool here
            if (listeners.size() > 0) {
                //Create a new RX Event
                final SerialPortProxyEvent upstreamEvent = new SerialPortProxyEvent(System.currentTimeMillis());
                for (final SerialPortProxyEventListener listener : listeners) {
                    SerialPortProxyEventTask task = new SerialPortProxyEventTask(listener, upstreamEvent, this);
                    try {
                        this.listenerTasks.add(task); //Add to queue (wait if queue is full)
                        task.start();
                    }
                    catch (IllegalStateException e) {
                        LOG.warn("Serial Port Problem, Listener task queue full, data will be lost!", e);
                    }
                }
            }

        }//end was RX event
        else {
            if (LOG.isDebugEnabled())
                LOG.debug("Non RX Event Type Recieved: " + event.getEventType());
        }
    }

    @Override
    public void eventComplete(long time, SerialPortProxyEventTask task) {
        this.listenerTasks.remove(task);
    }

}
