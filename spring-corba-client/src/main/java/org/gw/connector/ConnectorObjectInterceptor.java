/**
 * 
 */
package org.gw.connector;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * An AOP wrapper to catch {@link IConnector} {@link Object} method calls to
 * test their connection status. This class works heavily with the
 * {@link GenericReconnectingConnector}.
 * 
 * @author Gman
 * 
 */
public class ConnectorObjectInterceptor implements MethodInterceptor {

	private static Logger logger = LoggerFactory
			.getLogger(ConnectorObjectInterceptor.class);

	private IObjectConnector<?> connector;

	public ConnectorObjectInterceptor(IObjectConnector<?> connector) {
		if (connector == null) {
			throw new IllegalArgumentException(
					"GenericReconnectingConnector cannot be null.");
		}
		this.connector = connector;
	}

	/**
	 * The AOP wrapper. This method is called before the "target" method is
	 * called for every Object in the AOP configuration.
	 * 
	 * @param proxy
     * @param args
     * @param method
     * @param methodProxy
	 * @return
	 * @throws Throwable
	 *             The propagated {@link Throwable}
	 * @throws ConnectedObjectDisconnectedException
	 *             When
	 */
	@Override
	public Object intercept(final Object proxy, final Method method,
			final Object[] args, final MethodProxy methodProxy)
			throws Throwable {

		if (logger.isDebugEnabled()) {
			logger.debug("Attempting to check connection for: "
					+ connector.getObjType().getSimpleName() + " " + method
					+ " args" + Arrays.toString(args));
		}

		try {
			/*
			 * Attempt to connect if not connected already
			 */
			if (!connector.isConnected()) {
				logger.debug("Connector disconnected. Attempting to connect...");
				connect(method, args);
			}

			/* Run the method on the underlying Connected object */
			if (!Modifier.isAbstract(method.getModifiers())) {
				return executeProxy(proxy, methodProxy, args);
			} else {
				return executeConnectedObject(connector.getObj(), method, args);
			}

		} catch (RetryConnectionException e) {

			/*
			 * If we want to retry, we must disconnect the object then retry by
			 */
			logger.info(connector.getObjType().getSimpleName() + " " + method
					+ " args" + Arrays.toString(args) + " threw "
					+ e.getClass().getSimpleName()
					+ ". Disconnecting, and retrying on a new proxy.");

			/*
			 * Disconnect the connector just to be sure to reconnect on the next
			 * call
			 */
			connector.disconnect();

			/*
			 * Recursively call this method to attempt again.
			 */
			return intercept(proxy, method, args, methodProxy);

		} catch (Throwable e) {
			/* Log that we caught an exception */
			logger.info(connector.getObjType().getSimpleName() + " " + method
					+ " args" + Arrays.toString(args) + " threw "
					+ e.getClass().getSimpleName() + ": " + e.getMessage()
					+ ". Re-throwing.");
			throw e;

		} finally {
			if (logger.isDebugEnabled()) {
				logger.debug("Finished checking connection for: "
						+ connector.getObjType().getSimpleName() + " " + method
						+ " args" + Arrays.toString(args));
			}
		}

	}

	private void connect(Method method, Object[] args)
			throws CouldNotConnectException, ConnectTimeoutException, Throwable {

		if (connector.isBlockOnConnect()) {
			logger.debug("Connector will block until connected. Attempting to connect..");

			/*
			 * Call connect on the Connector. This uses the connectors timeout,
			 * maxRetries and maxRetryInterval.
			 */
			connector.connect();

		} else {
			logger.debug("Connector will return if cannot connect. Trying one time..");
			try {
				/*
				 * Try and connect just once using the connectors
				 * blockForReconnect timeout period. If it fails to connect,
				 * then connect asynchronously and return a
				 * ConnectedObjectDisconnectedException
				 */
				connector.connect(connector.getConnectionTimeoutInMillis(), 1,
						0);

				logger.debug("Connected.");
			} catch (CouldNotConnectException e) {

				logger.debug("Could not connect. Starting an asynchronous connection.");

				/*
				 * Connect asynchronously
				 */
				connector.connectAsync();

				logger.debug("Throwing ConnectedObjectDisconnectedException.");

				/*
				 * Then throw the ConnectedObjectDisconnectedException
				 */
				throw new ConnectedObjectDisconnectedException(connector
						.getObjType().getSimpleName()
						+ " was disconnected when "
						+ method
						+ " was called with args" + Arrays.toString(args));
			}

		}

	}

	/**
	 * Makes the method call on the newly connected object using reflection.
	 * 
	 * @param target
	 *            The target {@link Object} to make the method call on
	 * @param method
	 *            The {@link java.lang.reflect.Method} to invoke
	 * @param args
	 *            The {@link Object} array of args
	 * @return The return value of the invoked method
	 * @throws Throwable
	 *             Throws either a {@link RetryConnectionException} or the
	 *             {@link Throwable} coming from the method invocation.
	 */
	public Object executeConnectedObject(Object target, Method method,
			Object[] args) throws Throwable {

		try {
			logger.debug("Executing Connector method...");
			return method.invoke(target, args);
		} catch (InvocationTargetException e) {
			logger.debug("Connector method threw exception");
			if (connector.isRetryException(e.getTargetException())) {
				logger.debug("Retrying connector method as this matched a retry exception.");
				throw new RetryConnectionException();
			} else {
				logger.debug("Throwing connector exception");
				throw e.getTargetException();
			}
		}
	}

	/**
	 * Makes the method call on the connected proxy object using the given
	 * {@link MethodProxy}.
	 * 
	 * @param proxy
	 *            The proxy {@link Object} to make the method call on
	 * @param method
	 *            The {@link MethodProxy} to invoke
	 * @param args
	 *            The {@link Object} array of args
	 * @return The return value of the invoked method
	 * @throws Throwable
	 *             Throws either a {@link RetryConnectionException} or the
	 *             {@link Throwable} coming from the method invocation.
	 */
	public Object executeProxy(Object proxy, MethodProxy method, Object[] args)
			throws Throwable {

		try {
			logger.debug("Executing Connector method...");
			return method.invokeSuper(proxy, args);
		} catch (Throwable e) {
			logger.debug("Connector method threw exception");
			if (connector.isRetryException(e)) {
				logger.debug("Retrying connector method as this matched a retry exception.");
				throw new RetryConnectionException();
			} else {
				logger.debug("Throwing connector exception");
				throw e;
			}
		}
	}
}
