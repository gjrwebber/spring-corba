# Spring CORBA Client

## What is it?A generic CORBA connection proxy which allows any component to obtain a CORBA client object whose connection is monitored and reconnected on every method call with minimal lines of code. If you are using Spring, this can be achieved in 2 lines of code with JMX support (dis/connect, state attributes, configuring, statistics)  built in.

## Features

- Access CORBA components using Spring IOC container
- Configurable connection parameters
- Reconnection when disconnect discovered
- CORBA connection monitor
- Notifications of failed connections
- Statistics on connections, exceptions, etc. using [Java JMX Statistics](http://github.com/gjrwebber/java-jmx-statistics)
- Retry operation call if desired
- Lazy initialisation
## How to use it?### Spring UsageFor the sake of simplicity I'll assume you are using annotations to inject your dependencies with Spring. In which case your Spring bean only requires the following 2 lines to have access to the CORBA client object using default settings.	@Autowired	private Accounts accounts;### Configuring with SpringFor each CORBA client object there is one supporting object required to be instantiated - A concrete ```GenericCorbaConnector```. We can do this using Springs XML configuration by instantiating a ```CorbaConnector```.    <bean id="accountsConnector"
          class="org.gw.connector.corba.CorbaConnector">
        <constructor-arg index="0" type="java.lang.Class"
                         value="org.gw.samples.corba.Accounts" />
        <constructor-arg index="1" type="java.lang.String"
                         value="org.gw.samples.corba/Accounts" />
    </bean>
    
### Annotation Spring support

If you would like to just use annotations, you must create your own concrete ```GenericCorbaConnector``` specifying the generic type of the CORBA Object. The following example shows how to use a ```PropertySourcesPlaceholderConfigurer``` to configure the connector.


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
	}### Standard Java
Of course you don't have to use Spring. The standard way is just as easy.	public static void main(String[] args) {	  CorbaConnector accountsConnector = new CorbaConnector(Accounts.class, "org.gw.samples.corba/Accounts");	  Accounts accounts = null;	  try {	    accounts = accountsConnector.getConnectedObject();	  } catch(CouldNotConnectException e) {	    // Do Something	  }	 	  try {	    accounts.createAccount(new Account("Jane"));	   } catch(ConnectedObjectDisconnectedException e) {	    // Do Something	  }	  	}## Connector ParametersFor each ```CorbaConnector``` or ```GenericCorbaConnector```, which are both extensions of ```GenericReconnectingConnector```, you have the option of specifying how the connection behave.The table below outlines the various parameters with regard to connecting and reconnecting.Parameter Name | Description | Default
:------------- | :---------- | :-----:
listener | Set a ```ConnectorMonitorListener``` for listening to connection events. | @Autowired (required = false) 
blockOnConnect | If set to false (default) and the connected object is found to be disconnected on a method call, the method returns immediately by throwing a ```ConnectedObjectDisconnectedException``` and the connection is re-establish in a new Thread in the background. | false
connectionTimeoutInMillis | If set to a positive value (defaults to 0 - no timeout) and blockOnConnect is set to true, and the connected object is found to be disconnected on a method call, the caller will be blocked for a maximum of this value in millis. If the connection fails to connect in the time period a ```ConnectTimeoutException``` is thrown. | 0 
lazy | If set to true, the connection is not attempted until the first method call. This allows the application to startup and run with or without a successful CORBA connection. | true 
statsService | If you have a ```StatisticsService``` setup in your application, you can set it here. See [java-jmx-statistics](http://github.com/gjrwebber/java-jmx-statistics) for more info. | @Autowired (required = false) ### Configuring using Spring 	<!-- Initialise a IConnector -->	<bean id="accountsConnector" class="org.gw.connector.corba.CorbaConnector">	   p:lazy="true" 	   p:blockOnConnect="true" 	   p:connectionTimeoutInMillis="2000" />	 <constructor-arg index="0" type="java.lang.Class" 
								value="org.gw.samples.corba.Accounts" />	 <constructor-arg index="1" type="java.lang.String" 
								value="org.gw.samples/Accounts" />	</bean>	<!-- This will be @Autowired into the accountsConnector -->	<bean id="statsService" class="org.gw.stats.AnnotationDrivenJMXStatisticsService" />
	### Configuring outside of Spring
	Accounts accounts = null;	CorbaConnector accountsConnector = new CorbaConnector(Accounts.class, "org.gw.samples/Accounts");	accountsConnector.setLazy(true);	accountsConnector.setBlockOnConnect(true);	accountsConnector.setConnectionTimeoutInMillis(2000);	accountsConnector.setStatsService(new JMXStatisticsService());	 	try {	  accounts = accountsConnector.getConnectedObject();	} catch(CouldNotConnectException e) {	  // Do Something	}  ## DesignBefore delving into the details, this design allows any Spring enabled component to obtain a CORBA client object whose connection is monitored and reconnected on every method call with two lines.	@Autowired	private Accounts accounts;
The base Interface ```IConnector``` defines the methods ```connect()```, ```connectAsync()```, ```isConnected()```, ```disconnect()```.An Object connector is specified by the interface ```IObjectConnector<C>``` which takes a generic type that represents the Object being connected. ```IObjectConnector``` is an IConnector that provides the additional method ```getConnectedObject()```.The ```GenericReconnectingConnector``` implements ```IConnector``` and attempts to connect something x amount of times at y intervals. This is an abstract class requiring concrete implementations to implement the abstract ```doConnect()``` method.
The ```GenericObjectConnector``` is a ```GenericReconnectingConnector``` which adds the concept of connecting an ```Object``` that has functions that require a connection to be up. This class implements Spring's ```FactoryBean``` allowing Spring beans to be injected be the *connected* Object.A ```GenericCorbaConnector``` was created to provide a generic way for CORBA Objects to be connected with the server. This class is a ```GenericObjectConnector``` and provides an implementation of the ```doConnect()``` method of the ```GenericReconnectingConnector```. It is abstract requiring its implementors to provide only the generic CORBA object type and the name of the CORBA object in the naming context. The ```doConnect()``` method first resolves the CORBA Object from the naming context. It then uses reflection to *narrow* the CORBA object based on the assumption that the generic type has a helper class ```<generic_type_name>Helper.class```. If there are any reflection based exceptions, the ```GenericCorbaConnector``` will return immediately, otherwise it will be retried by the parent ```GenericReconnectingConnector``` strategy.
The ```CorbaConnector``` is a concrete ```GenericCorbaConnector```. It takes the Corba Object type and naming service context as constructor arguments allowing configuration using XML. I have separated this implementation into it's own class for no other reason than for flexible usage patterns. You could decide, for example, to not use XML and have your own concrete ```GenericCorbaConnector``` that specifies the CORBA Object type as the generic parameter and use 100% annotation based configuration.

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
	}There are some instances where you require the application to startup up even if one of the CORBA connections are failing. This can be achieved by setting ```lazy=”true”``` on the ```GenericCorbaConnector```. 