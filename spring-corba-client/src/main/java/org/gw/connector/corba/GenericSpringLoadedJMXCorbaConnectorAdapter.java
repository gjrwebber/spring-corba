package org.gw.connector.corba;

import org.gw.connector.CouldNotConnectException;
import org.gw.stats.StatisticsService;
import org.omg.CORBA.Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;

import javax.annotation.PostConstruct;
import java.util.Date;

public abstract class GenericSpringLoadedJMXCorbaConnectorAdapter<C extends Object> extends GenericCorbaConnector<C> {

    public GenericSpringLoadedJMXCorbaConnectorAdapter() {
    }

    public GenericSpringLoadedJMXCorbaConnectorAdapter(Class<? extends C> objType, String corbaObjectName) {
        super(objType, corbaObjectName);
    }

    public GenericSpringLoadedJMXCorbaConnectorAdapter(Class<? extends C> objType) {
        super(objType);
    }

    public GenericSpringLoadedJMXCorbaConnectorAdapter(String corbaObjectName) {
        super(corbaObjectName);
    }
    
    @PostConstruct
    public void initialise() throws Exception {
    	// call GenericReconnectingConnector initialise
    	super.initialise();
    }

    @Override
    @Autowired
    public void setRootNamingContext(RootNamingContextFactoryBean rootNamingContext) {
        super.setRootNamingContext(rootNamingContext);
    }

    @Override
    @ManagedOperation
    public boolean connect() throws CouldNotConnectException {
        return super.connect();
    }

    @Override
    @ManagedAttribute
    public boolean isConnected() {
        return super.isConnected();
    }

    @Override
    @ManagedOperation
    public void disconnect() {
        super.disconnect();
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
