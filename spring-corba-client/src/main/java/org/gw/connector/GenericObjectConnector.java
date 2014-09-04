package org.gw.connector;

import net.sf.cglib.proxy.Enhancer;
import org.omg.CORBA.Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Abstract class providing a mechanism for the connection to be tested on each method
 * call on the connected {@link Object} &lt;C&gt;. If the connection is found
 * to be down it will re-connect using the reconnection logic described in
 * {@link GenericReconnectingConnector}
 *
 * @author Gman
 *
 * @param <C>
 *            Can be any {@link Object}. This represents the {@link Object}
 *            being connected and is expected to be returned when
 *            getConnectedObject() is called.
 *
 * @see IConnector
 * @see IAsyncConnector
 * @see IObjectConnector
 */
public abstract class GenericObjectConnector<C>
		extends GenericReconnectingConnector implements IObjectConnector<C>, FactoryBean<C> {

	private static Logger logger = LoggerFactory
			.getLogger(GenericObjectConnector.class);

    /**
	 * The {@link Object} &lt;C&gt; that is connected.
	 */
	protected C obj;

	/**
	 * The of {@link Class} &lt;C&gt; that is connected.
	 */
	protected Class<? extends C> objType;

	/**
	 * A CgLib proxy {@link Object} &lt;C&gt; that is tests the connection on
	 * every method call.
	 */
	private C proxy;

    /**
     * If set to true, then the object is not connected until the first method
     * call.
     */
    private boolean lazy = true;

    /**
     * If set to false and the connected object is found to be disconnected on a
     * method call, the method returns immediately by throwing a
     * {@link ConnectedObjectDisconnectedException} and the connection is
     * re-establish in a new Thread.
     */
    private boolean blockOnConnect = false;

	/**
	 * Constructor retrieves the generic type using reflection.
	 */
	@SuppressWarnings("unchecked")
	public GenericObjectConnector() {
        super();
		if (this.objType == null) {
			Type type = getClass().getGenericSuperclass();

			while (type != null && type instanceof Class<?>) {
				type = ((Class<?>) type).getGenericSuperclass();
			}
			if (type != null && type instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) type;
				Type[] genericTypes = paramType.getActualTypeArguments();
				if (genericTypes == null || genericTypes.length != 1) {
					throw new IllegalStateException(
							getClass().getSimpleName()
									+ " does not provide a generic type as required by GenericConnectorObjectFactoryBean.");
				}
				this.objType = (Class<? extends C>) genericTypes[0];
			}
		}
		if (this.objType == null) {
			throw new IllegalStateException(
					"Could not determine generic types for "
							+ this.getClass().getName());
		}
	}

	/**
	 *
	 * @param objType
	 */
	public GenericObjectConnector(Class<? extends C> objType) {
		this.objType = objType;
	}

	public void initialise() throws Exception {
		// initialise stats
		if (statsService != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("initialising stats for " + objType);
			}
		}
	}

	/**
	 * @throws CouldNotConnectException
	 * @throws ConnectTimeoutException
	 * @see IObjectConnector#getConnectedObject()
	 */
	@Override
	public C getConnectedObject() throws CouldNotConnectException,
			ConnectTimeoutException {

		try {
			if (!isLazy()) {
				/* connect() returns immediately if already connected. */
				connect();
			}
		} catch (CouldNotConnectException e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		return getProxiedObject();
	}

	/**
	 * Returns a proxied version of the connected {@link Object}
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public C getProxiedObject() {

		if (proxy == null) {
			proxy = (C) Enhancer.create(objType,
					new ConnectorObjectInterceptor(this));
		}
		return proxy;
	}

    @Override
    public C getObject() throws Exception {
        return getConnectedObject();
    }

    @Override
    public Class<?> getObjectType() {
        return objType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

	/**
	 * Returns the underlying connected object
	 *
	 * @return
	 */
	public C getObj() {
		return obj;
	}

	@Override
	public Class<? extends C> getObjType() {
		return this.objType;
	}

	public void setObjType(Class<? extends C> objType) {
		this.objType = objType;
	}

    public boolean isBlockOnConnect() {
        return blockOnConnect;
    }

    public void setBlockOnConnect(boolean blockForReconnect) {
        this.blockOnConnect = blockForReconnect;
    }

    @Override
    public boolean isLazy() {
        return lazy;
    }

    @Override
    public void setLazy(boolean lazy) {
        this.lazy = lazy;
    }
}
