package org.gw.connector;

/**
 * Callback for notification of Asynchronous connections. This
 * {@link ConnectorAsyncConnectionCallback} will be added to a {@link java.util.Set} for a
 * single asynchronous connection as there may be more than one Thread wanting
 * to be notified.
 * 
 * @author gman
 * @since 1.0
 * @version 1.0
 * 
 */
public interface ConnectorAsyncConnectionCallback {
	void connectionSuccess();

	void connectionFailed();

	void connectionTimeout();
}
