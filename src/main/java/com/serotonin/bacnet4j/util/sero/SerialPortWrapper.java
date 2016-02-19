/**
 * Copyright (C) 2015 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.bacnet4j.util.sero;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Wrapper to further aid in abstracting Modbus4J from a serial port implementation
 * 
 * @author Terry Packer
 *
 */
public abstract class SerialPortWrapper {

    public static final int PARITY_NONE = 0;
    public static final int STOPBITS_1 = 1;
    public static final int DATABITS_8 = 8;
    public static final int FLOWCONTROL_NONE = 0;
	
	/**
	 * Close the Serial Port
	 */
	public abstract void close() throws Exception;

	/**
	 * 
	 */
	public abstract void open() throws Exception;

	/**
	 * 
	 * Return the input stream for an open port
	 * @return
	 */
	public abstract InputStream getInputStream();

	/**
	 * Return the output stream for an open port
	 * @return
	 */
	public abstract OutputStream getOutputStream();

	/**
	 * 
	 * @return
	 */
	public int getFlowControlIn(){
	    return FLOWCONTROL_NONE;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFlowControlOut(){
	    return FLOWCONTROL_NONE;
	}
	
	/**
	 * @return
	 */
	public int getDataBits(){
	    return DATABITS_8;
	}

	/**
	 * @return
	 */
	public int getStopBits(){
	    return STOPBITS_1;
	}

	/**
	 * @return
	 */
	public int getParity(){
	    return PARITY_NONE;
	}

	/**
	 * @return
	 */
	public abstract String getCommPortId();
	
	

}
