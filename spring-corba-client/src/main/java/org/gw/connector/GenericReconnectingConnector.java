package org.gw.connector;

import org.gw.stats.AveragingStatistic;
import org.gw.stats.StatisticsService;
import org.gw.commons.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract class containing the logic for reconnecting the connection
 * {@link Object} &lt;C&gt;. This class uses a standard reconnect pattern
 * whereby a connection attempt is made <code>maxRetries</code> times, sleeping
 * <code>retryIntervalSeconds</code> between each attempt.
 * <p/>
 * It also provides a mechanism for the connection to be tested on each method
 * call on the connection {@link Object} &lt;C&gt;. If the connection is found
 * to be down it will re-connect using the reconnection logic described above.
 *
 * @author Gman
 * @see IAsyncConnector
 */
public abstract class GenericReconnectingConnector
        implements IConnector, IAsyncConnector {

    private static Logger logger = LoggerFactory
            .getLogger(GenericReconnectingConnector.class);

    // max number of times to try when Connecting
    private final int DEFAULT_MAX_RETRIES = 10;
    protected int maxRetries = this.DEFAULT_MAX_RETRIES;

    // interval between connection attempts, in seconds
    private final int DEFAULT_RETRY_INTERVAL_IN_SECS = 10;
    protected int retryIntervalSeconds = this.DEFAULT_RETRY_INTERVAL_IN_SECS;

    private AtomicBoolean asyncConnecting = new AtomicBoolean();
    private AtomicBoolean connecting = new AtomicBoolean();
    private Date lastConnectedDate;

    /**
     * If set to a positive value and blockOnConnect is set to true, and the
     * connected object is found to be disconnected on a method call, the caller
     * will be blocked for a maximum of this value in millis. If the connection
     * fails to connect in the time period a {@link ConnectTimeoutException} is
     * thrown.
     */
    private long connectionTimeoutInMillis = 0;

    /**
     * The Set of {@link ConnectorAsyncConnectionCallback}s for an asynchronous
     * connection.
     */
    private Set<ConnectorAsyncConnectionCallback> asynConnectorCallbacks = new HashSet<ConnectorAsyncConnectionCallback>();

    /**
     * An {@link StatisticsService} to log stats against.
     */
    protected StatisticsService statsService;

    /**
     * Users can provide a ConnectorMonitorListener for listening to connection up and down events.
     */
    protected ConnectorMonitorListener listener;

    /**
     * {@link java.util.concurrent.ThreadFactory} for creating {@link Thread}s to connect
     * asynchronously
     */
    private ThreadFactory tf = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, getName() + " connectAsync Thread");
            t.setDaemon(true);
            return t;
        }
    };

    /**
     *
     */
    public GenericReconnectingConnector() {
    }

    /**
     *
     */
    public GenericReconnectingConnector(ConnectorMonitorListener listener) {
        this.listener = listener;
    }

    public void initialise() throws Exception {
        // initialise stats
        if (statsService != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("initialising stats for " + getClass().getSimpleName());
            }

            statsService.initialiseStats("Successful Connection Count",
                    getClass(), AveragingStatistic.ROLLING_AVG_WINDOW.HOUR);
            statsService.initialiseStats("Retry Exception Count",
                    getClass(), AveragingStatistic.ROLLING_AVG_WINDOW.HOUR);
            statsService.initialiseStats("Timeout On Connect Count",
                    getClass(), AveragingStatistic.ROLLING_AVG_WINDOW.HOUR);
            statsService.initialiseStats("Could Not Connect Count",
                    getClass(), AveragingStatistic.ROLLING_AVG_WINDOW.HOUR);
            statsService.initialiseStats("NPE Could Not Connect Count",
                    getClass(), AveragingStatistic.ROLLING_AVG_WINDOW.HOUR);
            statsService.initialiseStats("Unhandled Exception on Connect Count",
                    getClass(), AveragingStatistic.ROLLING_AVG_WINDOW.HOUR);
        }
    }

    /**
     * Connects asynchronously without specifying a callback. If you would like
     * to be notified of connection success or failure use
     * {@link GenericReconnectingConnector#connectAsync(ConnectorAsyncConnectionCallback)}
     *
     * @return Returns false if an asynchronous connection attempt is already in
     * progress. The {@link ConnectorAsyncConnectionCallback} is added
     * to the {@link java.util.Set} of callbacks.
     */
    public boolean connectAsync() {
        return connectAsync(null);
    }

    /**
     * Connects asynchronously. The given
     * {@link ConnectorAsyncConnectionCallback} is notified of success or
     * failure. In the case where the {@link IConnector} may try forever, it
     * will never be notified of failure.
     *
     * @param callback The {@link ConnectorAsyncConnectionCallback} for notifying of
     *                 success or failure
     * @return Returns false if an asynchronous connection attempt is already in
     * progress. The {@link ConnectorAsyncConnectionCallback} is added
     * to the {@link java.util.Set} of callbacks.
     */
    public boolean connectAsync(ConnectorAsyncConnectionCallback callback) {

		/*
         * If the callback is not null, add it to the Set
		 */
        synchronized (asynConnectorCallbacks) {
            if (callback != null) {
                asynConnectorCallbacks.add(callback);
            }
        }

		/*
         * If an asyncronous connection is already in process, just return.
		 */
        if (asyncConnecting.getAndSet(true)) {
            return false;
        }

		/*
         * Create a Thread to call the connect() method
		 */
        tf.newThread(new Runnable() {

            @Override
            public void run() {

                try {
                    connect();

					/*
                     * Once connection is successful, call connectionSuccess if
					 * there are callbacks available.
					 */
                    synchronized (asynConnectorCallbacks) {
                        for (ConnectorAsyncConnectionCallback callback : asynConnectorCallbacks) {
                            callback.connectionSuccess();
                        }
                    }
                } catch (CouldNotConnectException e) {
					/*
					 * Log that we could not reconnect
					 */
                    if (e instanceof ConnectTimeoutException) {
                        logger.warn(getClass().getSimpleName()
                                + " could not connect within a timeout of "
                                + connectionTimeoutInMillis
                                + "ms. Notifying callback.", e);

						/*
						 * Once connection fails, call connectionFailed if there
						 * are callbacks available.
						 */
                        synchronized (asynConnectorCallbacks) {
                            for (ConnectorAsyncConnectionCallback callback : asynConnectorCallbacks) {
                                callback.connectionTimeout();
                            }
                        }
                    } else {
						/* Log that we could not reconnect */
                        logger.warn(getClass().getSimpleName()
                                + " could not connect. Notifying callback.", e);

						/*
						 * Once connection fails, call connectionFailed if there
						 * are callbacks available.
						 */
                        synchronized (asynConnectorCallbacks) {
                            for (ConnectorAsyncConnectionCallback callback : asynConnectorCallbacks) {
                                callback.connectionFailed();
                            }
                        }
                    }
                } finally {
					/*
					 * Finished trying to connect
					 */
                    asyncConnecting.set(false);

					/*
					 * Clear the callbacks
					 */
                    synchronized (asynConnectorCallbacks) {
                        asynConnectorCallbacks.clear();
                    }
                }
            }
        }).start();

        return true;
    }

    /**
     * @see IConnector#disconnect()
     */
    @Override
    public void disconnect() {
        notifyConnectionDown();
    }

    /**
     * @see IConnector#connect()
     */
    @Override
    public boolean connect() throws CouldNotConnectException,
            ConnectTimeoutException {
        return connect(connectionTimeoutInMillis, maxRetries,
                retryIntervalSeconds);
    }

    /**
     * Lock for connection as we don't want more than one Thread attempting to
     * connect at any one time.
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * @param timeout
     * @param maxRetries
     * @param retryIntervalSeconds
     * @return
     * @throws CouldNotConnectException
     * @throws ConnectTimeoutException
     */
    @Override
    public boolean connect(long timeout, int maxRetries, int retryIntervalSeconds)
            throws CouldNotConnectException, ConnectTimeoutException {

		/*
		 * If connected already return immediately!
		 */
        if (isConnected()) {
            return false;
        }

        if (logger.isDebugEnabled())
            logger.debug(this.getClass().getSimpleName()
                    + " is not connected with server. Attempting to reconnect.");

        if (timeout >= 0) {
            try {
                if (!lock.tryLock(timeout, TimeUnit.MILLISECONDS)) {
                    throw new ConnectTimeoutException(
                            "Connect attempt timed out while waiting for another Thread to connect.");
                }
            } catch (InterruptedException e) {
                throw new CouldNotConnectException(
                        "Connect attempt interrupted out while waiting for another Thread to connect.");
            }
        } else {
            // Else this Thread aquired the lock
            lock.lock();
        }

        // If connected after lock has been released, return immediately!
        if (isConnected()) {
            return false;
        }


        // Set connecting to true
        connecting.set(true);

        try {
            long timeoutTime = 0;
            if (timeout > 0) {
                timeoutTime = System.currentTimeMillis() + timeout;
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format(
                            "Setting timeout time to %d. Current time: %d",
                            timeoutTime, timeoutTime - timeout));
                }
            }

            int attempts = 0;
            try {
                while (maxRetries == -1 || attempts++ < maxRetries) {

					/*
					 * Check we haven't timed out. If so, throw exception
					 */
                    if (timeoutTime > 0
                            && System.currentTimeMillis() > timeoutTime) {
                        throw new ConnectTimeoutException();
                    }

                    try {

						/* Call the implementations doConnect method */
                        doConnect();
                        this.lastConnectedDate = new Date();

                        incrementStat("Successful Connection Count");
                        notifyConnectionUp();
                        return true;

                    } catch (RetryConnectionException e) {
                        incrementStat("Retry Exception Count");

                        String msg = "Failed to connect "
                                + this.getClass().getSimpleName()
                                + " with the server " + attempts + " of "
                                + maxRetries + " times. Retrying in "
                                + retryIntervalSeconds + " seconds. Reason: ";

                        String reason = "None given.";
                        if (e.getCause() != null
                                && e.getCause().getMessage() != null) {
                            reason = e.getCause().getMessage();
                        } else if (e.getMessage() != null) {
                            reason = e.getMessage();
                        }
                        msg += reason;

                        logger.warn(msg);

                        try {
                            long sleep = retryIntervalSeconds * 1000;

							/*
							 * If we have a timeout, test the sleep won't go
							 * past it
							 */
                            if (timeoutTime > 0) {
                                long now = System.currentTimeMillis();
                                if (now + sleep > timeoutTime) {

									/*
									 * If sleeping takes us past the timeout,
									 * then set the sleep to 100ms before the
									 * timeout time for one last shot.
									 */
                                    sleep = timeoutTime - now - 100;
                                    if (sleep < 1000) {

                                        incrementStat("Timeout On Connect Count");
										/*
										 * Just throw exception
										 */
                                        throw new ConnectTimeoutException();

                                    }
                                }
                            }

                            // sleep before retrying
                            if (logger.isDebugEnabled()) {
                                logger.debug("Actually sleep time after timeout has beeb taken into consideration: "
                                        + sleep + "ms");
                            }
                            Thread.sleep(sleep);

                        } catch (InterruptedException ie) {
                            // we were interrupted but continue trying to
                            // connect
                            logger.debug("Interrupted while sleeping.");
                        }
                    }
                }

                incrementStat("Could Not Connect Count");

                throw new CouldNotConnectException("Attempted to reconnect "
                        + this.getClass().getSimpleName() + " " + maxRetries
                        + " times, but failed to resolve.");

            } catch (NullPointerException e) {

                incrementStat("NPE Could Not Connect Count");

                logger.error("NPE Caught. Error connecting "
                        + this.getClass().getSimpleName());
                throw new CouldNotConnectException(
                        "NPE Caught. Error retrieving the Config Bean - check that the Name Service is initialised",
                        e);
            } catch (Exception e) {
                if (e instanceof CouldNotConnectException) {
                    throw (CouldNotConnectException) e;
                } else if (e instanceof ConnectTimeoutException) {
                    throw (ConnectTimeoutException) e;
                }

                incrementStat("Unhandled Exception on Connect Count");

                logger.error("Unhandled exception while Connecting: "
                        + this.getClass().getSimpleName(), e);
                throw new CouldNotConnectException(
                        "Unhandled exception while Connecting: "
                                + this.getClass().getSimpleName(), e);
            }
        } finally {
            lock.unlock();
            connecting.set(false);
        }

    }

    protected void notifyConnectionUp() {
        if (listener != null) {
            listener.connectionUp(this);
        }
    }

    protected void notifyConnectionDown() {
        if (listener != null) {
            listener.connectionDown(this);
        }
    }

//    /**
//     * @see org.gw.connector.IConnector#isConnected()
//     */
//    @Override
//    public boolean isConnected() {
//        return this.connected.get();
//    }

    /**
     * @see IConnector#isConnecting()
     */
    @Override
    public boolean isConnecting() {
        return this.connecting.get();
    }

//
//	/**
//	 * @throws CouldNotConnectException
//	 * @throws ConnectTimeoutException
//	 * @see org.gw.connector.IConnector#getConnectedObject()
//	 */
//	@Override
//	public C getConnectedObject() throws CouldNotConnectException,
//			ConnectTimeoutException {
//
//		try {
//			if (!isLazy()) {
//				/* connect() returns immediately if already connected. */
//				connect();
//			}
//		} catch (CouldNotConnectException e) {
//			logger.error(e.getMessage(), e);
//			throw e;
//		}
//		return getProxiedObject();
//	}
//
//	/**
//	 * Returns a proxied version of the connected {@link Object}
//	 *
//	 * @return
//	 */
//	@SuppressWarnings("unchecked")
//	public C getProxiedObject() {
//
//		if (proxy == null) {
//			proxy = (C) Enhancer.create(objType,
//					new ConnectorObjectInterceptor(this));
//		}
//		return proxy;
//	}
//
//	/**
//	 * Returns the underlying connected object
//	 *
//	 * @return
//	 */
//	public C getObj() {
//		return obj;
//	}

    /**
     * Tests the connection. This should be done by the concrete impl as it only knows how.
     *
     * @return true if the connection is healthy, false otherwise. Returns true by default.
     */
    protected boolean testConnection() {
        return true;
    }


    /**
     * Implementor's of this class must implement this method to do the actual
     * connection.
     *
     * @throws RetryConnectionException
     */
    public abstract void doConnect() throws RetryConnectionException, Exception;

    /**
     * The name of this IConnector. Defaults to Class name.
     *
     * @return
     */
    public String getName() {
        return getClass().getSimpleName();
    }

    public int getMaxRetries() {
        return this.maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getRetryIntervalSeconds() {
        return this.retryIntervalSeconds;
    }

    public void setRetryIntervalSeconds(int retryIntervalSeconds) {
        this.retryIntervalSeconds = retryIntervalSeconds;
    }

    @Override
    public Date getLastConnectionDate() {
        return this.lastConnectedDate;
    }

    @Override
    public String getUptime() {
        if (!isConnected()) {
            return "Not Connected";
        }
        long uptimeInMillis = System.currentTimeMillis()
                - this.lastConnectedDate.getTime();
        return StringUtils.convertMillisToString(uptimeInMillis, true);
    }

    @Override
    public long getConnectionTimeoutInMillis() {
        return connectionTimeoutInMillis;
    }

    public void setConnectionTimeoutInMillis(long connectionTimeoutInMillis) {
        this.connectionTimeoutInMillis = connectionTimeoutInMillis;
    }

    public StatisticsService getStatsService() {
        return statsService;
    }

    public void setStatsService(StatisticsService statsService) {
        this.statsService = statsService;
    }

    private void incrementStat(String name) {
        if (statsService != null) {
            statsService.incrementStats(name, getClass());
        }
    }

    public ConnectorMonitorListener getListener() {
        return listener;
    }

    public void setListener(ConnectorMonitorListener listener) {
        this.listener = listener;
    }
}
