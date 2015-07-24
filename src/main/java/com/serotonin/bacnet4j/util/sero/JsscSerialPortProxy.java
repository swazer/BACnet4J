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

import jssc.SerialPort;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Terry Packer
 */
public class JsscSerialPortProxy extends SerialPortProxy {
    static final Logger LOG = LoggerFactory.getLogger(JsscSerialPortProxy.class);

    private SerialPort port;
    private SerialPortOutputStream os;
    private SerialPortInputStream is;

    /**
     * @param serialParameters
     */
    public JsscSerialPortProxy(SerialParameters serialParameters) {
        super(serialParameters.getCommPortId());
        this.parameters = serialParameters;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.serotonin.io.serial.SerialPortProxy#readBytes(int)
     */
    @Override
    public byte[] readBytes(int i) throws SerialPortException {
        try {
            return this.port.readBytes(i);
        }
        catch (jssc.SerialPortException e) {
            throw new SerialPortException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.serotonin.io.serial.SerialPortProxy#writeInt(int)
     */
    @Override
    public void writeInt(int arg0) throws SerialPortException {
        try {
            this.port.writeInt(arg0);
        }
        catch (jssc.SerialPortException e) {
            throw new SerialPortException(e);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.serotonin.io.serial.SerialPortProxy#close()
     */
    @Override
    public void closeImpl() throws SerialPortException {
        Throwable ex = null;

        try {
            this.is.close();
        }
        catch (IOException e) {
            LOG.error("", e);
            ex = e;
        }
        try {
            this.os.close();
        }
        catch (IOException e) {
            LOG.error("", e);
            ex = e;
        }
        try {
            this.port.closePort();
        }
        catch (jssc.SerialPortException e) {
            LOG.error("", e);
            ex = e;
        }

        if (ex != null)
            throw new SerialPortException(ex); //May miss some errors if > 1, but hey we get something back. 

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.serotonin.io.serial.SerialPortProxy#open()
     */
    @Override
    public void openImpl() throws SerialPortException {

        try {
            if (LOG.isDebugEnabled())
                LOG.debug("Opening Serial Port: " + this.parameters.getCommPortId());

            this.port = new SerialPort(this.parameters.getCommPortId());

            this.port.openPort();
            this.port.setFlowControlMode(this.parameters.getFlowControlIn() | this.parameters.getFlowControlOut());
            this.port.setParams(parameters.getBaudRate(), parameters.getDataBits(), parameters.getStopBits(),
                    parameters.getParity());
            this.is = new JsscSerialPortInputStream(this.port, this.listeners);
            this.os = new JsscSerialPortOutputStream(this.port);

        }
        catch (jssc.SerialPortException e) {
            throw new SerialPortException(e);
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.serotonin.io.serial.SerialPortProxy#getInputStream()
     */
    @Override
    public SerialPortInputStream getInputStream() {
        return this.is;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.serotonin.io.serial.SerialPortProxy#getOutputStream()
     */
    @Override
    public SerialPortOutputStream getOutputStream() {
        return this.os;
    }
}
