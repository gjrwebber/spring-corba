#Spring CORBA Client Sample

I have provided a sample in this folder to get you started. Once you have downloaded the source, you should be able to run it using gradle. 

####Prerequisites

- Java 6 JDK
- Gradle 2.0+

##Running the sample

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

## Code breakdown

### AccountsService

```AccountsService``` implements the service layer for Accounts. The ```Accounts``` CORBA client Object is injected into this instance by Spring.

	@Service
	public class AccountsService {
	
	    /**
	     * The Accounts CORBA client Object is injected here directly.
	     */
	    @Autowired
	    private Accounts accounts;
	
	    public void createAccount(String name) {
	        Account account = new Account(name);
	
	        try {
	            // Every method call on the CORBA Object is tested, and any failed connections will be reported,
	            // and depending on the connector parameters, it might block or return and retry asynchronously x times.
	            accounts.createAccount(account);
	
	        } catch (ConnectedObjectDisconnectedException e) {
	            // If the CORBA connection was lost and it could not reconnect given the connectors
	            // parameters then it will throw a ConnectedObjectDisconnectedException
	            e.printStackTrace();
	        }
	
	    }
	
	}

### Configuration

#### [Annotation based](id:annotation)
	
For full annotation configuration, check out ```AnnotationBasedConfig.java```:

	@Configurable
	@ComponentScan(basePackages = "org.gw")
	@PropertySource("app.properties")
	public class AnnotationBasedConfig {
	
	    @Bean
	    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
	        return new PropertySourcesPlaceholderConfigurer();
	    }
	
	}
	
and ```AccountsConnector.java```:


	@Service
	public class AccountsConnector extends GenericCorbaConnector<Accounts> {
	
	    @Value("${connector.accounts.corba.name?:org.gw.samples/Accounts}")
	    public void setCorbaObjectName(String corbaName) {
	        super.setCorbaObjectName(corbaName);
	    }
	
	    @Value("${connector.accounts.lazy?:true}")
	    public void setLazy(boolean lazy){
	        super.setLazy(lazy);
	    }
	
	    @Value("${connector.accounts.block?:false}")
	    public void setBlockOnConnect(boolean blockOnConnect) {
	        super.setBlockOnConnect(blockOnConnect);
	    }
	
	    @Value("${connector.accounts.max.retries?:10}")
	    public void setMaxRetries(int maxRetries){
	        super.setMaxRetries(maxRetries);
	    }
	}

#### [XML](id:xml)

If you want to use XML to configure, check out ```app-config.xml```:


	...
    <bean id="accountsConnector"
          class="org.gw.connector.corba.CorbaConnector"
          p:lazy="true"
          p:blockOnConnect="false"
          p:retryIntervalSeconds="2"
          p:maxRetries="2">
        <constructor-arg index="0" type="java.lang.Class"
                         value="org.gw.samples.corba.Accounts"/>
        <constructor-arg index="1" type="java.lang.String"
                         value="org.gw.samples/Accounts"/>
    </bean>
    ...

#### [Java Config](id:java)

To configure using Java, check out ```HardCodedConfig.java```:


	...	
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
    ...


