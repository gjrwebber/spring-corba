package org.gw.connector.corba;

import org.gw.connector.RetryConnectionException;

public class TestPrototypeConnector extends GenericCorbaConnector<ITestPrototypeObject> {

    private int connectAttempts = 0;
    private int connectAfter = 1;

    public TestPrototypeConnector() {
        retryIntervalSeconds = 2;
        maxRetries = 5;
    }

    public TestPrototypeConnector(String bla) {
        this();
    }

    public TestPrototypeConnector(Class<ITestPrototypeObject> objType) {
        super(objType);
    }

    @Override
    public void doConnect() throws RetryConnectionException {
        if (++connectAttempts < connectAfter) {
            throw new RetryConnectionException();
        }
        obj = new TestPrototypeObject("bla");
    }

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

}
