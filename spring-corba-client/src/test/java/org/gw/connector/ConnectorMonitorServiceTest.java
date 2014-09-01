package org.gw.connector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;

/**
 * Created by gman on 6/08/2014.
 */
public class ConnectorMonitorServiceTest {

    private TestConnector connector;
    private ConnectorMonitorService monitor;
    @Before
    public void init() {
        connector = new TestConnector();
        connector.setLazy(false);
        connector.setAttempts(1);

        monitor = new ConnectorMonitorService();
        monitor.getConnectors().add(connector);
        monitor.setMonitorIntervalSecs(1);
    }

    @After
    public void destroy() {
        monitor.stopMonitor();
    }

    @Test
    public void testMonitorConnectionListener()
            throws Exception {
        // Given
        ConnectorMonitorListener listener = Mockito.mock(ConnectorMonitorListener.class);
        monitor.getMonitorListeners().add(listener);

        // Connect straight away
        TestObject testObj = connector.getConnectedObject();

        monitor.startMonitor();
        Thread.sleep(100);
        Mockito.verify(listener).connectionUp(connector);

        connector.setConnected(false);

        // Sleep for 2s so that the monitor has time to notice and publish down events twice
        Thread.sleep(2000);

        Mockito.verify(listener, new Times(2)).connectionDown(connector);
    }

    @Test
    public void testMonitorConnectionListenerMultipleConnectors()
            throws Exception {
        // Given
        TestConnector connector2 = new TestConnector();
        connector2.setLazy(false);
        connector2.setAttempts(1);

        monitor.getConnectors().add(connector2);

        ConnectorMonitorListener listener = Mockito.mock(ConnectorMonitorListener.class);
        monitor.getMonitorListeners().add(listener);

        // Connect straight away
        connector.getConnectedObject();
        connector2.getConnectedObject();

        monitor.startMonitor();
        Thread.sleep(100);
        Mockito.verify(listener).connectionUp(connector);
        Mockito.verify(listener).connectionUp(connector2);

        connector.setConnected(false);

        // Sleep for 2s so that the monitor has time to notice and publish down events twice
        Thread.sleep(2000);

        Mockito.verify(listener, new Times(2)).connectionDown(connector);

        connector2.setConnected(false);

        // Sleep for 1s so that the monitor has time to notice and publish down events twice
        Thread.sleep(1000);

        // 1 new event for connector2
        Mockito.verify(listener).connectionDown(connector2);
    }

}
