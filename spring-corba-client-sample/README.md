#Spring CORBA Client Sample

I have provided a sample in this folder to get you started. Once you have downloaded the source, you should be able to run them using gradle. 

####Prerequisites

- Java 6 JDK
- Gradle 2.0+

####Running the sample

If using an IDE and it supports Gradle. Once you have imported the projects, open the Gradle view select the target 'run' on the desired sample. 

... or on the command line ...

- cd {spring-corba}/spring-corba-client-sample
- gradle run

You should see a bunch of log messages printed to the console, then:

	...
	CORBA Connection Up.
	Creating account (Lara) on the server
	
	BUILD SUCCESSFUL

This is the result of a Spring service being injected with a CORBA (client) Object and the method on the Object being called on the server.

Here is the Spring Service code:

	@Service
	@DependsOn("accountsConnector")
	public class MyService {
	
	    @Autowired
	    private Accounts accounts;
	
	    public void createAccount(String name) {
	        Account account = new Account(name);
	        accounts.createAccount(account);
	    }
	
	}

If using XML to configure, check out ```app-config.xml```:


	...
    <bean id="accountsConnector"
          class="org.gw.connector.corba.SpringLoadedJMXCorbaConnectorAdapter"
          p:lazy="true"
          p:blockOnConnect="false"
          p:retryIntervalSeconds="2"
          p:maxRetries="2"
          p:listener-ref="corbaConnectionListener">
        <constructor-arg index="0" type="java.lang.Class"
                         value="org.gw.samples.corba.Accounts" />
        <constructor-arg index="1" type="java.lang.String"
                         value="org.gw.samples/Accounts" />
    </bean>
    ...

To configure using Java, check out ```AppConfig.java```:


    @Bean
    public SpringLoadedJMXCorbaConnectorAdapter accountsConnector() {
        SpringLoadedJMXCorbaConnectorAdapter connectorAdapter = new SpringLoadedJMXCorbaConnectorAdapter(Accounts.class, AccountsImpl.CONTEXT+"/"+AccountsImpl.NAME);
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
        connectorAdapter.setBlockOnConnect(false);

        // Add a ConnectorMonitorListener if desired
        connectorAdapter.setListener(connectorMonitorListener);

        return connectorAdapter;
    }

Or to configure using Spring annotations:

	@Service
	public class AccountsConnector extends GenericSpringLoadedJMXCorbaConnectorAdapter<Accounts> {
	
	    public AccountsConnector(@Value("${connector.accounts.lazy?:true}") boolean lazy,
	                             @Value("${connector.accounts.block?:false}") boolean blockOnConnect,
	                             @Value("${connector.accounts.max.retries?:10}") int maxRetries) {
	        setLazy(lazy);
	        setBlockOnConnect(blockOnConnect);
	        setMaxRetries(maxRetries);
	    }
	}

