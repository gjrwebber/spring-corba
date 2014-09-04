package org.gw.connector.corba;

import org.gw.connector.ITestObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class TestLazyConnectorStartup {

	@Autowired
	private ITestObject testObj;

	@Autowired
	private ITestPrototypeObject testPrototypeObj;

    private static Process orbdProcess;

    @BeforeClass
    public static void startORBD() throws IOException {

        System.setProperty("naming.service.port", "14005");
        // Run ORBD for CORBA
        orbdProcess = Runtime.getRuntime().exec("orbd -ORBInitialPort 14005");

    }

    @AfterClass
    public static void destroyORBD() {
        if (orbdProcess != null) {
            orbdProcess.destroy();
        }
    }

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Starting Spring...");
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:test-lazy-corba-connector-all.xml");
		System.out
				.println("\nSpring initialised... Everyone's @PostConstruct called.\n");
		System.out.println("Getting TestLazyConnectorStartup...");
		TestLazyConnectorStartup test = ctx
				.getBean(TestLazyConnectorStartup.class);

		System.out.println("TestLazyConnectorStartup retrieved.");

		System.out.println("\nSleeping for clarity...\n");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// Do nothing
		}

		try {
			test.test();
			System.out
					.println("Performing test after Spring has been initialised...");
			test.test();
			System.out
					.println("Performing test after Spring has been initialised again just to be sure...");
		} catch (Exception e) {
			System.out.println("Caught exception: ");
			e.printStackTrace();
		}

		System.out.println("Test completed successfully.");
		Thread.sleep(20000);
	}

	@PostConstruct
	public void init() throws Exception {
		System.out.println("\nPerforming test from @PostConstruct init()...");
		test();
		System.out
				.println("\nPerforming test from @PostConstruct init() again just to be sure...");
		test();
	}

	public void test() throws Exception {
		test(false);
	}

	public void test(boolean throwEx) throws Exception {
		if (testObj.check(throwEx)) {
			System.out.println("Test connected and checked.");
		}
		if (testPrototypeObj.check()) {
			System.out.println("Prototype test connected and checked.");
		}
	}
}
