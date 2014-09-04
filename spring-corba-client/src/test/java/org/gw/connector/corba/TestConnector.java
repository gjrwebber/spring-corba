package org.gw.connector.corba;

import org.gw.connector.GenericObjectConnector;
import org.gw.connector.ITestObject;
import org.gw.connector.RetryConnectionException;
import org.gw.connector.TestObject;

public class TestConnector extends GenericObjectConnector<ITestObject> {

	private int connectAttempts = 0;
	private int connectAfter = 1;

	/**
	 * @param connectAttempts the connectAttempts to set
	 */
	public void setConnectAttempts(int connectAttempts) {
		this.connectAttempts = connectAttempts;
	}

	/**
	 * @param connectAfter the connectAfter to set
	 */
	public void setConnectAfter(int connectAfter) {
		this.connectAfter = connectAfter;
	}

	public TestConnector() {
		retryIntervalSeconds = 2;
		maxRetries = 5;
	}

	public TestConnector(Class<ITestObject> objType) {
		super(objType);
		setLazy(true);
	}

	@Override
	public void doConnect() throws RetryConnectionException {
		if (++connectAttempts < connectAfter) {
			throw new RetryConnectionException();
		}
		obj = new TestObject();
	}

	public void setAttempts(int attempts) {
		this.connectAttempts = attempts;
	}

    @Override
    public boolean isRetryException(Throwable e) {
        return false;
    }

    @Override
    public boolean isConnected() {
        return obj != null;
    }
}
