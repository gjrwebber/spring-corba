package org.gw.connector;

public class TestConnector extends AbstractTestConnector<TestObject> {

	@Override
	public void doConnect() throws RetryConnectionException {
		if (++connectAttempts < connectAfter) {
			throw new RetryConnectionException();
		}
		if (obj == null)
			obj = new TestObject();
        connected = true;
	}

}
