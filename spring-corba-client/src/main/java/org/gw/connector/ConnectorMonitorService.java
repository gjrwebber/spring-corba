package org.gw.connector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by gman on 6/08/2014.
 */
@Service
public class ConnectorMonitorService {

    private static Logger logger = LoggerFactory
            .getLogger(ConnectorMonitorService.class);

    /**
     * The Set of all IConnectors in the system.
     */
    @Autowired(required = false)
    private Set<IConnector> connectors = new HashSet<IConnector>();

    /**
     * Set off IConnector statuses to monitor. These are the IConnectors that return true for IConnector#monitorConnection()
     */
    private Set<ConnectorStatus> connectorsToMonitor;

    /**
     * The interval between checking the status of the connection
     */
    @Value("${connector.monitor.interval?:5}")
    private int monitorIntervalSecs = 5;

    /**
     * The Set of {@link ConnectorMonitorListener}s
     */
    @Autowired(required = false)
    private Set<ConnectorMonitorListener> monitorListeners = new HashSet<ConnectorMonitorListener>();

    /**
     * The ScheduledExecutorService to run
     */
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "Connector-Monitor");
            t.setDaemon(true);
            return t;
        }
    });

    /**
     * Starts the ScheduledExecutor to monitor the connection.
     */
    @PostConstruct
    public void startMonitor() {

        connectorsToMonitor = new HashSet<ConnectorStatus>();
        for (IConnector connector : connectors) {
            ConnectorStatus monitor = new ConnectorStatus();
            monitor.connector = connector;
            connectorsToMonitor.add(monitor);
        }

        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                for (ConnectorStatus monitor : connectorsToMonitor) {
                    monitor.checkConnector();
                }
            }
        }, 0, monitorIntervalSecs, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void stopMonitor() {
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    private void connectionUp(IConnector connector) {
        logger.info(connector.getName() + " Connection Up.");
        if (monitorListeners != null) {
            for (ConnectorMonitorListener listener : monitorListeners) {
                listener.connectionUp(connector);
            }
        }
    }

    private void connectionDown(IConnector connector) {
        logger.warn(connector.getName() + " Connection Down.");
        if (monitorListeners != null) {
            for (ConnectorMonitorListener listener : monitorListeners) {
                listener.connectionDown(connector);
            }
        }
    }

    public int getMonitorIntervalSecs() {
        return monitorIntervalSecs;
    }

    public void setMonitorIntervalSecs(int monitorIntervalSecs) {
        this.monitorIntervalSecs = monitorIntervalSecs;
    }

    public Set<ConnectorMonitorListener> getMonitorListeners() {
        return monitorListeners;
    }

    public void setMonitorListeners(Set<ConnectorMonitorListener> monitorListeners) {
        this.monitorListeners = monitorListeners;
    }

    public Set<IConnector> getConnectors() {
        return connectors;
    }

    public void setConnectors(Set<IConnector> connectors) {
        this.connectors = connectors;
    }

    private class ConnectorStatus {

        // The IConnector to monitor
        private IConnector connector;
        // State of the IConnector connection on the previous iteration of the monitor
        private boolean wasConnected = false;

        private void checkConnector() {

            // Only do some checking if this GenericReconnectingConnector is wasConnected
            if (!connector.isConnected()) {

                // Set wasConnected to false as it is now disconnected
                wasConnected = false;
                // Fire connection down event
                connectionDown(connector);

            } else if (!wasConnected && connector.isConnected()) {

                // Set wasConnected to true as it is now wasConnected
                wasConnected = true;
                // Fire connection up event
                connectionUp(connector);

            }
        }
    }
}
