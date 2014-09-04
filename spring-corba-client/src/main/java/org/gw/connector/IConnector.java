package org.gw.connector;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import java.util.Date;

/**
 * The connector interface.
 *
 * @author Gman
 */
public interface IConnector {

    /**
     * Attempts to connect. This method
     * returns true if a new connection was made, otherwise false is returned if
     * already connected. The {@link CouldNotConnectException} is thrown if a
     * connection could not be made using the IConnector's connection
     * logic. The {@link ConnectTimeoutException} is thrown if a connection
     * could not be made in the timeout period if one has been applied.
     *
     * @return true if a new connection was made, false if already connected.
     * @throws CouldNotConnectException
     * @throws ConnectTimeoutException
     */
    @ManagedOperation
    boolean connect() throws CouldNotConnectException, ConnectTimeoutException;

    /**
     * Attempts to connect. This method
     * returns true if a new connection was made, otherwise false is returned if
     * already connected. The {@link CouldNotConnectException} is thrown if a
     * connection could not be made using the IConnector's connection
     * logic and the given parameters. The {@link ConnectTimeoutException} is thrown if a connection
     * could not be made in the given timeout period.
     *
     * @param timeout THe timeout in millis
     * @param maxRetries THe number of retry attempts before throwing CouldNotConnectException
     * @param retryIntervalSeconds The time in seconds between retrying to conenct
     * @return true if a new connection was made, false if already connected.
     * @throws CouldNotConnectException
     * @throws ConnectTimeoutException
     */
    boolean connect(long timeout, int maxRetries, int retryIntervalSeconds)
            throws CouldNotConnectException, ConnectTimeoutException;

    /**
     * Disconnects the connected {@link Object} <C>
     */
    @ManagedOperation
    void disconnect();

    /**
     * @return true if this IConnector is connected.
     */
    @ManagedAttribute
    boolean isConnected();

    /**
     * @return true if this IConnector is trying to connect.
     */
    @ManagedAttribute
    boolean isConnecting();

    /**
     * @return a name for this IConnector
     */
    @ManagedAttribute
    String getName();

    /**
     * @return The connection timeout in millis
     */
    @ManagedAttribute
    long getConnectionTimeoutInMillis();

    /**
     * Returns the {@link java.util.Date} of the IConnector's last successful
     * connection
     *
     * @return the {@link java.util.Date} of the IConnector's last successful
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
