package org.gw.samples;

import org.gw.connector.ConnectorMonitorListener;
import org.gw.connector.corba.CorbaConnector;
import org.gw.connector.corba.RootNamingContextFactoryBean;
import org.gw.samples.corba.Accounts;
import org.gw.stats.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Created by gman on 29/08/2014.
 */
@Configurable
@ComponentScan(basePackages = "org.gw", excludeFilters = {
        @ComponentScan.Filter(pattern = ".*AccountsConnector", type = FilterType.REGEX)})
@PropertySource("app.properties")
public class HardCodedConfig {

    @Autowired
    private ConnectorMonitorListener connectorMonitorListener;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private RootNamingContextFactoryBean rootNamingContext;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public CorbaConnector accountsConnector() {
        CorbaConnector connectorAdapter = new CorbaConnector(Accounts.class, AccountsImpl.CONTEXT + "/" + AccountsImpl.NAME);
        connectorAdapter.setRootNamingContext(rootNamingContext);
        connectorAdapter.setStatsService(statisticsService);

        // Set lazy to false to enforce the connection on startup
        connectorAdapter.setLazy(true);

        // After connection is tried 2 times or it times out in 2s a ConnectedObjectDisconnectedException is thrown
        connectorAdapter.setMaxRetries(2);
        connectorAdapter.setConnectionTimeoutInMillis(2000);

        // Retries every 2 seconds
        connectorAdapter.setRetryIntervalSeconds(2);

        // If you want to block on connection, set to true. ie. If a method call on a CORBA object fails
        // due to disconnection it will block until it reconnects or a ConnectedObjectDisconnectedException is thrown
        // Given the above parameters
        connectorAdapter.setBlockOnConnect(true);

        // Add a ConnectorMonitorListener if desired
        connectorAdapter.setListener(connectorMonitorListener);

        return connectorAdapter;
    }
}
