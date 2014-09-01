package org.gw.connector;

import junit.framework.Assert;
import org.junit.Test;
import org.omg.CORBA.COMM_FAILURE;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractTestConnectorFramework<C extends ITestObject> {

	protected C testObj;
	protected AbstractTestConnector<C> connector;

	protected abstract AbstractTestConnector<C> getTestConnector();

	public void init(boolean lazy) throws CouldNotConnectException {
		System.out.println("TestConnectorFramework init called.");
		connector = getTestConnector();
		connector.setLazy(lazy);
		testObj = connector.getConnectedObject();
		connector.setAttempts(0);
		System.out.println("TestConnectorFramework init finished.");
	}

	@Test
	public void testConnectLazy() throws Exception {
		init(true);
		System.out.println("testConnect()");
		Assert.assertTrue(testObj.check(false));
		System.out.println("Done testConnect()");
	}

	@Test
	public void testConnect() throws Exception {
		init(false);
		System.out.println("testConnect()");
		Assert.assertTrue(testObj.check(false));
		System.out.println("Done testConnect()");
	}

	@Test
	public void testConnectNoBlockLazy() throws Exception {
		init(true);
		System.out.println("testConnectNoBlock()");

		Assert.assertTrue(testObj.check(false));
		System.out.println("Done testConnectNoBlock()");
	}

	@Test
	public void testConnectNoBlock() throws Exception {
		init(false);
		System.out.println("testConnectNoBlock()");
		Assert.assertTrue(testObj.check(false));
		System.out.println("Done testConnectNoBlock()");
	}

	@Test(expected = ConnectedObjectDisconnectedException.class)
	public void testConnectNoBlockOnErrorLazy() throws Exception {
		init(true);
		System.out.println("testConnectNoBlockOnErrorLazy()");

		connector.setConnectAfter(3);
		testObj.check(new COMM_FAILURE(), 1);
		System.out.println("Done testConnectNoBlockOnErrorLazy()");
	}

	@Test(expected = ConnectedObjectDisconnectedException.class)
	public void testConnectNoBlockOnError() throws Exception {
		init(false);
		System.out.println("testConnectNoBlockOnError()");
		connector.setConnectAfter(3);
		testObj.check(new COMM_FAILURE(), 3);
		System.out.println("Done testConnectNoBlockOnError()");
	}

	@Test
	public void testConnectBlockLazy() throws Exception {
		init(true);
		System.out.println("testConnectBlock()");
		connector.setBlockOnConnect(true);
		Assert.assertTrue(connector.connect());
		connector.disconnect();

		connector.setConnectAfter(1);
		testObj.check(new COMM_FAILURE(), 1);

		Assert.assertTrue(connector.isConnected());
		System.out.println("Done testConnectBlock()");
	}

	@Test
	public void testConnectBlock() throws Exception {
		init(false);
		System.out.println("testConnectBlock()");
		connector.setBlockOnConnect(true);
		// Already connected
		Assert.assertFalse(connector.connect());
		connector.disconnect();

		connector.setConnectAfter(1);
		testObj.check(new COMM_FAILURE(), 1);

		Assert.assertTrue(connector.isConnected());
		System.out.println("Done testConnectBlock()");
	}

	@Test
	public void testConnectNegativeTimeout() throws CouldNotConnectException,
			ConnectTimeoutException, InterruptedException {
		init(true);
		connector.setConnectionTimeoutInMillis(-1);
		System.out.println("testConnectNegativeTimeout()");
		Assert.assertTrue(connector.connect());
		System.out.println("Done testConnectNegativeTimeout()");
	}

	@Test(expected = ConnectTimeoutException.class)
	public void testConnectWithTimeout() throws CouldNotConnectException,
			ConnectTimeoutException, InterruptedException {
		init(true);

		connector.setConnectionTimeoutInMillis(1000);
		System.out.println("testConnectWithTimeout()");
		connector.setRetryIntervalSeconds(10);
		connector.setAttempts(-10);
		Assert.assertTrue(connector.connect());
		System.out.println("Done testConnectWithTimeout()");
	}

	@Test
	public void testMultipleThreadConnect() throws CouldNotConnectException,
			InterruptedException {
		init(true);
		System.out.println("testMultipleThreadConnect()");
		final AtomicBoolean result = new AtomicBoolean(false);
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Assert.assertTrue("Should have connected",
							connector.connect());
					result.set(true);
					System.out
							.println("Done testMultipleThreadConnect() in Thread");
				} catch (CouldNotConnectException e) {
					Assert.fail(e.getMessage());
				}
			}
		}).start();
		Thread.sleep(1000);
		Assert.assertFalse("Should already have connected", connector.connect());
		Assert.assertTrue("Thread did not fully connect", result.get());
		System.out.println("Done testMultipleThreadConnect()");
	}

	@Test
	public void testMultipleThreadsConnectingAtOnce()
			throws CouldNotConnectException, InterruptedException {
		init(true);
		System.out.println("testMultipleThreadsConnectingAtOnce()");
		connector.setMaxRetries(10);
		connector.setRetryIntervalSeconds(1);
		connector.setConnectAfter(2);
		final AtomicInteger noTimeoutException = new AtomicInteger();
		final AtomicBoolean result = new AtomicBoolean(false);
		Thread[] ts = new Thread[10];
		for (int i = 0; i < 10; i++) {
			ts[i] = new Thread(new Runnable() {

				@Override
				public void run() {

					try {
						Assert.assertFalse(
								"Should have connected on the main Thread",
								connector.connect());
					} catch (CouldNotConnectException e) {
						if (!(e instanceof ConnectTimeoutException)) {
							Assert.fail(e.getMessage());
						} else {
							noTimeoutException.incrementAndGet();
						}
					}
				}
			});
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Assert.assertTrue("Should have connected on this Thread",
							connector.connect());
					result.set(true);
				} catch (CouldNotConnectException e) {
					if (!(e instanceof ConnectTimeoutException)) {
						Assert.fail(e.getMessage());
					}
				}
			}
		}).start();
		Thread.sleep(100);

		for (int j = 0; j < 10; j++) {
			ts[j].start();
		}

		Thread.sleep(3000);

		Assert.assertEquals(
				"Should've thrown 10 ConnectTimeoutException, but threw "
						+ noTimeoutException.get(), 10,
				noTimeoutException.get());
		Assert.assertFalse("Should already have connected", connector.connect());
		Assert.assertTrue("Thread did not fully connect", result.get());
		System.out.println("Done testMultipleThreadsConnectingAtOnce()");
	}

	@Test(expected = ConnectTimeoutException.class)
	public void testMultipleThreadConnectWithTimeout()
			throws CouldNotConnectException, InterruptedException,
			ConnectTimeoutException {
		init(true);
		connector.setConnectionTimeoutInMillis(10000);
		System.out.println("testMultipleThreadConnectWithTimeout()");
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					connector.setMaxRetries(10);
					connector.setRetryIntervalSeconds(1);
					connector.setConnectAfter(10);
					Assert.assertTrue(connector.connect());
				} catch (CouldNotConnectException e) {
					if (!(e instanceof ConnectTimeoutException)) {
						Assert.fail(e.getMessage());
					}
				}
			}
		});
		t.start();
		Thread.sleep(500);

		connector.setConnectAfter(1);
		connector.connect(200, 10, 1);
		System.out.println("Done testMultipleThreadConnectWithTimeout()");
	}
	@Test
	public void testConnectAsync() throws CouldNotConnectException,
			InterruptedException {
		init(true);
		System.out.println("testConnectAsync()");

		Assert.assertFalse(connector.isConnected());
		connector.connectAsync();
		Thread.sleep(2000);
		Assert.assertTrue(connector.isConnected());

		System.out.println("Done testConnectAsync()");
	}

	@Test
	public void testMultipleThreadConnectAsync()
			throws CouldNotConnectException, InterruptedException,
			ConnectTimeoutException {
		init(true);
		System.out.println("testMultipleThreadConnectAsync()");
		new Thread(new Runnable() {

			@Override
			public void run() {
				connector.setMaxRetries(10);
				connector.setRetryIntervalSeconds(1);
				connector.setConnectAfter(2);
				connector.connectAsync();
			}
		}).start();

		connector.connectAsync();
		Thread.sleep(2000);
		Assert.assertTrue("Should be connected, but its not.",
				connector.isConnected());
		System.out.println("Done testMultipleThreadConnectAsync()");
	}

	@Test
	public void testConnectAsyncCallback() throws CouldNotConnectException,
			InterruptedException {
		init(true);
		System.out.println("testConnectAsyncCallback()");
		connector.setRetryIntervalSeconds(1);

		final AtomicBoolean result = new AtomicBoolean();

		connector.connectAsync(new ConnectorAsyncConnectionCallback() {

			@Override
			public void connectionTimeout() {
			}

			@Override
			public void connectionSuccess() {
				result.set(true);
			}

			@Override
			public void connectionFailed() {
			}
		});
		Thread.sleep(2000);
		Assert.assertTrue(result.get());
		System.out.println("Done testConnectAsyncCallback()");
	}

	@Test
	public void testMultipleThreadConnectAsyncCallback()
			throws CouldNotConnectException, InterruptedException,
			ConnectTimeoutException {
		init(true);
		System.out.println("testMultipleThreadConnectAsyncCallback()");

		final AtomicBoolean result = new AtomicBoolean();
		final AtomicBoolean firstStarted = new AtomicBoolean();
		new Thread(new Runnable() {

			@Override
			public void run() {
				connector.setMaxRetries(10);
				connector.setRetryIntervalSeconds(1);
				connector.setConnectAfter(2);
				firstStarted.set(connector
						.connectAsync(new ConnectorAsyncConnectionCallback() {

							@Override
							public void connectionTimeout() {
							}

							@Override
							public void connectionSuccess() {
								result.set(true);
							}

							@Override
							public void connectionFailed() {
							}
						}));
			}
		}).start();
		final AtomicBoolean result2 = new AtomicBoolean();
		Thread.sleep(10);
		boolean started = connector
				.connectAsync(new ConnectorAsyncConnectionCallback() {

					@Override
					public void connectionTimeout() {
					}

					@Override
					public void connectionSuccess() {
						result2.set(true);
					}

					@Override
					public void connectionFailed() {
					}
				});

		Assert.assertFalse("Should have not started the async connect Thread.",
				started);
		Thread.sleep(2000);

		Assert.assertTrue("Should have started the async connect Thread.",
				firstStarted.get());

		Assert.assertTrue(result.get());
		Assert.assertTrue(result2.get());
		System.out.println("Done testMultipleThreadConnectAsyncCallback()");
	}

	@Test
	public void testConnectAsyncCallbackFail() throws CouldNotConnectException,
			InterruptedException {
		init(true);
		System.out.println("testConnectAsyncCallbackFail()");
		connector.setRetryIntervalSeconds(1);
		connector.setAttempts(-1);
		connector.setMaxRetries(1);
		final AtomicBoolean result = new AtomicBoolean();

		connector.connectAsync(new ConnectorAsyncConnectionCallback() {

			@Override
			public void connectionTimeout() {
			}

			@Override
			public void connectionSuccess() {
			}

			@Override
			public void connectionFailed() {
				result.set(true);
			}
		});
		Thread.sleep(1500);
		Assert.assertTrue("Connection should've failed, but didn't.",
				result.get());
		System.out.println("Done testConnectAsyncCallbackFail()");
	}

	@Test
	public void testConnectAsyncCallbackTimeout()
			throws CouldNotConnectException, InterruptedException {
		init(true);
		System.out.println("testConnectAsyncCallbackTimeout()");

		connector.setConnectionTimeoutInMillis(500);
		connector.setRetryIntervalSeconds(1);
		connector.setAttempts(-1);
		connector.setMaxRetries(1);

		final AtomicBoolean result = new AtomicBoolean();

		connector.connectAsync(new ConnectorAsyncConnectionCallback() {

			@Override
			public void connectionTimeout() {
				result.set(true);
			}

			@Override
			public void connectionSuccess() {
			}

			@Override
			public void connectionFailed() {
			}
		});
		Thread.sleep(1000);
		Assert.assertTrue("Connection should've timed out, but didn't.",
				result.get());
		System.out.println("Done testConnectAsyncCallbackTimeout()");
	}

	@Test
	public void testLazy() throws Exception {
		init(true);
		connector.setBlockOnConnect(true);
		System.out.println("testLazy()");
		Assert.assertTrue(testObj.check(false));
		System.out.println("Done testLazy()");
	}

	@Test(expected = TestException.class)
	public void testExceptionLazy() throws Exception {
		init(true);
		System.out.println("testExceptionLazy()");
		testObj.check(true);
		System.out.println("Done testExceptionLazy()");
	}

	@Test(expected = TestException.class)
	public void testException() throws Exception {
		init(false);
		System.out.println("testException()");
		connector.disconnect();
		testObj.check(true);
		System.out.println("Done testException()");
	}

	@Test(expected = TestException.class)
	public void testExceptionBlockLazy() throws Exception {
		init(true);
		connector.setBlockOnConnect(true);
		System.out.println("testExceptionBlock()");
		testObj.check(true);
		System.out.println("Done testExceptionBlock()");
	}

	@Test(expected = TestException.class)
	public void testExceptionBlock() throws Exception {
		init(false);
		connector.setBlockOnConnect(true);
		System.out.println("testExceptionBlock()");
		connector.disconnect();
		testObj.check(true);
		System.out.println("Done testExceptionBlock()");
	}

	@Test
	public void testCommFailureNoBlock() throws Exception {
		init(true);
		System.out.println("testCommFailureNoBlock()");
		connector.setRetryIntervalSeconds(1);
		connector.setMaxRetries(10);
		connector.setConnectAfter(1);
		testObj.check(new COMM_FAILURE(), 1);

		try {
			connector.disconnect();
			connector.setConnectAfter(3);
			connector.setAttempts(0);
			testObj.check(new COMM_FAILURE(), 3);
			Assert.fail("Should've thrown a ConnectedObjectDisconnectedException");
		} catch (ConnectedObjectDisconnectedException e) {
			// This is what we want
		}
		Assert.assertFalse(
				"Connector is connected, when it should be disconnected.",
				connector.isConnected());
		Thread.sleep(3000);
		Assert.assertTrue(connector.isConnected());

		System.out.println("Done testCommFailureNoBlock()");
	}

	@Test
	public void testCommFailureAfterConnectNoBlock() throws Exception {
		init(false);
		System.out.println("testCommFailureAfterConnectNoBlock()");
		connector.setMaxRetries(10);

		connector.connect();
		Assert.assertTrue(connector.isConnected());

		try {
			connector.setConnectAfter(3);
			testObj.check(new COMM_FAILURE(), 1);
			Assert.fail("Should've thrown a ConnectedObjectDisconnectedException");
		} catch (ConnectedObjectDisconnectedException e) {
			// This is what we want
		}

		Assert.assertFalse(
				"Connector is connected, when it should be disconnected.",
				connector.isConnected());
		Thread.sleep(2000);
		Assert.assertTrue(
				"Connector is disconnected, when it should be connected.",
				connector.isConnected());
		System.out.println("Done testCommFailureAfterConnectNoBlock()");
	}

	@Test
	public void testCommFailureBlocking() throws Exception {
		init(true);
		connector.setBlockOnConnect(true);
		System.out.println("testCommFailureBlocking()");
		Assert.assertTrue(testObj.check(new COMM_FAILURE(), 1));
		System.out.println("Done testCommFailureBlocking()");
	}

	@Test
	public void testCommFailureAfterConnectBlocking() throws Exception {
		init(true);
		connector.setBlockOnConnect(true);
		System.out.println("testCommFailureBlocking()");
		Assert.assertFalse(connector.isConnected());
		Assert.assertTrue(testObj.check(false));
		Assert.assertTrue(connector.isConnected());
		connector.disconnect();
		testObj.check(new COMM_FAILURE(), 1);
		Assert.assertTrue(connector.isConnected());
		System.out.println("Done testCommFailureBlocking()");
	}

	@Test(expected = ConnectTimeoutException.class)
	public void testCommFailureBlockingTimeout() throws Exception {
		init(true);
		connector.setBlockOnConnect(true);
		connector.setConnectionTimeoutInMillis(500);
		connector.setConnectAfter(0);
		connector.connect();
		connector.setConnectAfter(4);
		System.out.println("testCommFailureBlockingTimeout()");
		Assert.assertTrue(testObj.check(new COMM_FAILURE(), 10));
		System.out.println("Done testCommFailureBlockingTimeout()");
	}

	@Test
	public void testCommFailureAffterConnectBlockingTimeout() throws Exception {
		init(true);
		connector.setBlockOnConnect(true);
		connector.setConnectionTimeoutInMillis(500);
		connector.setConnectAfter(0);
		connector.connect();
		connector.setConnectAfter(4);
		System.out.println("testCommFailureBlockingTimeout()");

		Assert.assertTrue(testObj.check(false));
		Assert.assertTrue(connector.isConnected());

		connector.disconnect();
		try {
			Assert.assertTrue(testObj.check(new COMM_FAILURE(), 10));
		} catch (ConnectTimeoutException e) {
			// This is what we want
		}
		System.out.println("Done testCommFailureBlockingTimeout()");
	}


}
