package org.gw.connector;

import java.util.Date;

import org.springframework.jmx.export.annotation.ManagedAttribute;

/**
 * The connection status interface. Provides method to check the status of a
 * connection.
 * 
 * @author Gman
 */
public interface IConnectionStatus {

	/**
	 * Rturns true if connected, false otherwise
	 * 
	 * @return
	 */
	@ManagedAttribute
	boolean isConnected();

	/**
	 * Returns the {@link java.util.Date} of the {@link IConnector}'s last successful
	 * connection
	 * 
	 * @return the {@link java.util.Date} of the {@link IConnector}'s last successful
	 *         connection
	 */
	@ManagedAttribute
	Date getLastConnectionDate();

	/**
	 * Returns the uptime as a {@link String} 23d 15m 34s
	 * 
	 * @return
	 */
	@ManagedAttribute
	String getUptime();
}
