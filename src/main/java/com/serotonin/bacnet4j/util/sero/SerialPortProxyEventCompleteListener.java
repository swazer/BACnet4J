package com.serotonin.bacnet4j.util.sero;

public interface SerialPortProxyEventCompleteListener {

	/**
	 * Event fired when event has completed
	 * @param time
	 */
	public void eventComplete(long time, SerialPortProxyEventTask task);
	
}
