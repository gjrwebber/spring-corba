package org.gw.connector;

import org.gw.stats.StatisticsService;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import java.util.Date;

public abstract class GenericSpringLoadedJMXConnectorAdapter<C extends Object> extends GenericObjectConnector<C> {

    public GenericSpringLoadedJMXConnectorAdapter() {
        super();
    }

    public GenericSpringLoadedJMXConnectorAdapter(Class<? extends C> objType) {
        super(objType);
    }

    public void initialise() throws Exception {
    	// call GenericReconnectingConnector initialise
    	super.initialise();
    }

    @Override
    @ManagedOperation
    public boolean connect() throws CouldNotConnectException {
        return super.connect();
    }

    @ManagedAttribute
    public boolean connected() {
        return isConnected();
    }

    @Override
    @ManagedOperation
    public void disconnect() {
        // placeholder
    }

    @Override
    @ManagedAttribute
    public int getMaxRetries() {
        return super.getMaxRetries();
    }

    @Override
    @ManagedAttribute
    public void setMaxRetries(int maxRetries) {
        super.setMaxRetries(maxRetries);
    }

    @Override
    @ManagedAttribute
    public int getRetryIntervalSeconds() {
        return super.getRetryIntervalSeconds();
    }

    @Override
    @ManagedAttribute
    public void setRetryIntervalSeconds(int retryIntervalSeconds) {
        super.setRetryIntervalSeconds(retryIntervalSeconds);
    }

    @Override
    @ManagedAttribute
    public Date getLastConnectionDate() {
        return super.getLastConnectionDate();
    }

    @Override
    @ManagedAttribute
    public String getUptime() {
        return super.getUptime();
    }

    @Override
    @Autowired(required = false)
    public void setStatsService(StatisticsService statsService) {
        super.setStatsService(statsService);
    }


}
