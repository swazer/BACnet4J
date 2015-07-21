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
