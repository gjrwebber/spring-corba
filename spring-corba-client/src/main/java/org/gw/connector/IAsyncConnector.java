package org.gw.connector;

/**
 * The asynchronous connector interface.
 * 
 * @author Gman
 *
 */
public interface IAsyncConnector extends IConnector {

	/**
	 * Attempts to connect the {@link Object} represented by <C> asynchronously.
	 * This method should return immediately and will start a Thread to do the
	 * work of the connection.
	 * 
	 * @return Returns false if an asynchronous connection is already in
	 *         progress. The {@link ConnectorAsyncConnectionCallback} is added
	 *         to the {@link java.util.Set} of callbacks.
	 */
	boolean connectAsync();

	/**
	 * Attempts to connect the {@link Object} represented by <C> asynchronously.
	 * This method should return immediately and will start a Thread to do the
	 * work of the connection.
	 * <p>
	 * The given {@link ConnectorAsyncConnectionCallback} is notified of success
	 * or failure. In the case where the {@link IAsyncConnector} may try
	 * forever, it will never be notified of failure.
	 * 
	 * @param callback
	 *            The {@link ConnectorAsyncConnectionCallback} for notifying of
	 *            success or failure
	 * @return Returns false if an asynchronous connection attempt is already in
	 *         progress. The {@link ConnectorAsyncConnectionCallback} is added
	 *         to the {@link java.util.Set} of callbacks.
	 */
	boolean connectAsync(ConnectorAsyncConnectionCallback callback);

}
