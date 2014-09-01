package org.gw.connector;

import org.omg.CORBA.COMM_FAILURE;

public abstract class AbstractTestConnector<C extends org.omg.CORBA.Object> extends GenericObjectConnector<C> {

	protected int connectAttempts = 0;
	protected int connectAfter = 1;
    protected boolean connected = true;

	public AbstractTestConnector() {
		super();
		retryIntervalSeconds = 1;
		maxRetries = 2;
	}

    /**
     * @return true if this IConnector is trying to connect.
     */
    @Override
    public boolean isConnecting() {
        return false;
    }

    /**
     * @return true if this IConnector is trying to connect.
     */
    @Override
    public boolean isConnected() {
        return obj != null && this.connected;
    }

    public AbstractTestConnector(Class<C> objType) {
		super(objType);
	}

    /**
     * Disconnects the connected {@link org.omg.CORBA.Object} <C>
     */
    @Override
    public void disconnect() {
        connected = false;
    }

    public void setAttempts(int attempts) {
		this.connectAttempts = attempts;
	}

    @Override
	public boolean isRetryException(Throwable e) {
		return e instanceof COMM_FAILURE;
	}

	public void setConnectAfter(int connectAfter) {
		this.connectAfter = connectAfter;
	}

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
