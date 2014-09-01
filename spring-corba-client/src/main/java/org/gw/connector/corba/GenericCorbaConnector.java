package org.gw.connector.corba;

import org.gw.connector.GenericObjectConnector;
import org.gw.connector.RetryConnectionException;
import org.omg.CORBA.COMM_FAILURE;
import org.omg.CORBA.TIMEOUT;
import org.omg.CORBA.TRANSIENT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Abstract class containing the logic for connecting and retrieving a CORBA
 * object from the server.
 * 
 * @author Gman
 * 
 * @param <C>
 *            Can be any {@link Object}. This represents the {@link Object}
 *            being connected and is expected to be returned when
 *            getConnectedObject() is called.
 */
public abstract class GenericCorbaConnector<C extends org.omg.CORBA.Object>
		extends
        GenericObjectConnector<C> {

	private static Logger logger = LoggerFactory
			.getLogger(GenericCorbaConnector.class);

	protected RootNamingContextFactoryBean rootNamingContext;

	private String corbaObjectName;

	public GenericCorbaConnector() {
		super();
	}

	public GenericCorbaConnector(String corbaObjectName) {
		this();
		this.corbaObjectName = corbaObjectName;
	}

	public GenericCorbaConnector(Class<? extends C> objType) {
		super(objType);
	}

	public GenericCorbaConnector(Class<? extends C> objType,
			String corbaObjectName) {
		this(objType);
		this.corbaObjectName = corbaObjectName;
	}

    /**
     * A CORBA connector is connected if the GenericReconnectingConnector is connected and the CORBA obj exists as far
     * as CORBA is concerned.
     *
     * @see org.gw.connector.IConnector#isConnected()
     */
    @Override
    public boolean isConnected() {
        return obj != null && !obj._non_existent();
    }

    /**
	 * Connects the CORBA Object by resolving through the naming service. Then
	 * using the name of the generic type, the narrow method is called on it's
	 * helper using reflection.
	 */
	@Override
	public void doConnect() throws RetryConnectionException {
		try {
			logger.info("Retrieving " + objType.getSimpleName()
					+ " CORBA object from server with name: " + corbaObjectName
					+ " ...");
			org.omg.CORBA.Object resolved = rootNamingContext
					.getRootNamingContext().resolve_str(corbaObjectName);
			Method narrowMethod = Class.forName(
					objType.getCanonicalName() + "Helper").getDeclaredMethod(
					"narrow", org.omg.CORBA.Object.class);
			Object object = narrowMethod.invoke((Object) null, resolved);

			obj = objType.cast(object);

			if (!isConnected()) {
				throw new RetryConnectionException(objType.getSimpleName()
						+ " was deemed non existent by CORBA.");
			}
			
			logger.info("Retrieved " + objType.getSimpleName()
					+ " CORBA object from server.");

		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException("Could not find Class: "
					+ objType.getSimpleName() + "Helper", e);
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException(
					"Could not find Method narrow in Class "
							+ objType.getSimpleName() + "Helper", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException(
					"Could not invoke Method narrow in Class "
							+ objType.getSimpleName() + "Helper", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException(e.getMessage(), e);

		} catch (RuntimeException e) {
			if (isRetryException(e)) {
				throw new RetryConnectionException(e);
			} else {
				throw e;
			}
		} catch (Exception e) {
			throw new RetryConnectionException(e);
		}

	}

	public boolean isRetryException(Throwable e) {
		if (e instanceof TRANSIENT || e instanceof COMM_FAILURE
				|| e instanceof TIMEOUT) {
			return true;
		} else {
			return false;
		}
	}

    /**
     * Disconnects the connected {@link Object} <C>
     */
    @Override
    public void disconnect() {

    }

    public RootNamingContextFactoryBean getRootNamingContext() {
		return rootNamingContext;
	}

	public void setRootNamingContext(RootNamingContextFactoryBean rootNamingContext) {
		this.rootNamingContext = rootNamingContext;
	}

	public String getCorbaObjectName() {
		return corbaObjectName;
	}

	public void setCorbaObjectName(String corbaObjectName) {
		this.corbaObjectName = corbaObjectName;
	}

}
