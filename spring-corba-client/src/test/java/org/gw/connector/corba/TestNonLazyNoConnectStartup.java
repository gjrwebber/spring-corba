package org.gw.connector.corba;

import org.gw.connector.ConnectedObjectDisconnectedException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestNonLazyNoConnectStartup {

    private static Process orbdProcess;

    @BeforeClass
    public static void startORBD() throws IOException {

        System.setProperty("naming.service.port", "14006");
        // Run ORBD for CORBA
        orbdProcess = Runtime.getRuntime().exec("orbd -ORBInitialPort 14006");

    }

    @AfterClass
    public static void destroyORBD() {
        if (orbdProcess != null) {
            orbdProcess.destroy();
        }
    }

	@Test
	public void testNonLazyFailToConnect() throws Exception {
		try {
			new ClassPathXmlApplicationContext(
                    "classpath:test-non-lazy-no-connect.xml");
			Assert.fail("Should've thrown an Exception.");
		} catch (BeansException e) {
			if (!(e.getMostSpecificCause() instanceof ConnectedObjectDisconnectedException)) {
                e.printStackTrace();
				Assert.fail("Expected ConnectedObjectDisconnectedException, got "
						+ e.getMostSpecificCause().getClass().getSimpleName());
			}
		}
	}
	@Test
	public void testNonLazyNoConnect() throws Exception {
		final AtomicBoolean finished = new AtomicBoolean();
		new Thread(new Runnable() {
			@Override
			public void run() {
				ApplicationContext ctx = new ClassPathXmlApplicationContext(
                        "classpath:test-non-lazy-fail-connect.xml");
				finished.set(true);
			}
		}).start();
		Thread.sleep(10000);
		Assert.assertFalse("Should not have completed app context setup.", finished.get());
	}

}
