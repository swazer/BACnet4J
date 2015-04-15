/**
 * Copyright (C) 2014 Infinite Automation Software. All rights reserved.
 * @author Terry Packer
 */
package com.serotonin.bacnet4j.util.sero;

/**
 * @author Terry Packer
 *
 */
public interface SerialPortProxyEventListener {

	/**
	 * @param upstreamEvent
	 */
	public void serialEvent(SerialPortProxyEvent upstreamEvent);

}
