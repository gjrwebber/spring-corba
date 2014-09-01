package org.gw.connector;

public class TestInterfaceConnectorFramework
		extends
			AbstractTestConnectorFramework<ITestObject> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gw.connector.AbstractTestConnectorFramework
	 * #getTestConnector()
	 */
	@Override
	protected AbstractTestConnector<ITestObject> getTestConnector() {
		return new TestInterfaceConnector();
	}

}
