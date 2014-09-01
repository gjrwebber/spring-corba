package org.gw.connector.corba;

import org.junit.Assert;
import org.gw.connector.TestException;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.PostConstruct;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:test-corba-connector.xml")
public class TestConnectorFactoryFramework {

	@Autowired
	private ApplicationContext ctx;

	@Autowired
	private ITestObject testObj;

	@Autowired
	@Qualifier("testConnector")
	private TestConnector connector;

	@Autowired
	private ITestPrototypeObject testPrototypeObj;

	@Autowired
	private TestPrototypeConnector prototypeConnector;

	@PostConstruct
	public void init() {
		System.out.println("TestConnectorFramework PostConstruct called.");
		Assert.assertNotNull(testObj);
		Assert.assertNotNull(connector);
		Assert.assertNotNull(testPrototypeObj);
		Assert.assertNotNull(prototypeConnector);

		connector.setAttempts(0);
		System.out.println("TestConnectorFramework PostConstruct finished.");
	}

	@Test(timeout = 10000)
	public void testConnect() throws Exception {
		System.out.println("testConnect()");
		Assert.assertTrue(testObj.check(false));
		Assert.assertTrue(testPrototypeObj.check());
		System.out.println("Done testConnect()");
	}

	@Test
	public void testPrototypeNotSameInstance() {
		System.out.println("testSameInstance()");
		ITestPrototypeObject testPrototypeObj2 = ctx
				.getBean(ITestPrototypeObject.class);
		Assert.assertTrue(testPrototypeObj2.check());
		Assert.assertTrue(testPrototypeObj != testPrototypeObj2);
		System.out.println("Done testSameInstance()");
	}

	@Test(expected = TestException.class)
    public void testException() throws Exception {
		System.out.println("testException()");
		testObj.check(true);
		System.out.println("Done testException()");
	}

}
