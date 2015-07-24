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
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Terry Packer
 */
public abstract class SerialPortInputStream extends InputStream {
    static final Logger LOG = LoggerFactory.getLogger(SerialPortInputStream.class);

    private final Object closeLock = new Object();
    private volatile boolean closed = false;

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#read()
     */
    @Override
    public abstract int read() throws IOException;

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#available()
     */
    @Override
    public abstract int available() throws IOException;

    /*
     * (non-Javadoc)
     * 
     * @see java.io.InputStream#close()
     */
    @Override
    public void close() throws IOException {
        LOG.debug("Attempting Close of Serial Port Input Stream.");
        synchronized (closeLock) {
            if (closed) {
                return;
            }
            closeImpl();
            closed = true;
        }
        LOG.debug("Closed Serial Port Input Stream.");
    }

    /**
     * The close implementation for the stream
     * 
     * @throws IOException
     */
    public abstract void closeImpl() throws IOException;

    /**
     * Peek at the head of the stream, do not remove the byte
     */
    public abstract int peek();
}
