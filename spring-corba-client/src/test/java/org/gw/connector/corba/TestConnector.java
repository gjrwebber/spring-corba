package org.gw.connector.corba;

import org.gw.connector.RetryConnectionException;

public class TestConnector extends GenericCorbaConnector<ITestObject> {

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

}
