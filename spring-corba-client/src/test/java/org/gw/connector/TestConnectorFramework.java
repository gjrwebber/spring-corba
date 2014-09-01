package org.gw.connector;

import org.junit.Test;

public class TestConnectorFramework
		extends
			AbstractTestConnectorFramework<TestObject> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gw.connector.AbstractTestConnectorFramework
	 * #getTestConnector()
	 */
	@Override
	protected AbstractTestConnector<TestObject> getTestConnector() {
		return new TestConnector();
	}

	@Test
	public void testGetProxiedObject() throws CouldNotConnectException {
		init(false);
		testObj.tryProtected();
	}

}
