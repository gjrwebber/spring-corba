package org.gw.samples;

import org.gw.samples.service.AccountsService;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;

/**
 * Created by gman on 29/08/2014.
 */
@Service
public class SpringCorbaClientSampleMain {

    @Autowired
    private NamingContextExt namingContext;

    @Autowired
    private AccountsService accountsService;

    @Autowired
    private ORB orb;

    public static void main(String[] args) throws Exception {

        // Run ORBD for CORBA
        Process process = Runtime.getRuntime().exec("orbd -ORBInitialPort 14003");


        ApplicationContext ctx = new AnnotationConfigApplicationContext(AnnotationBasedConfig.class);
//        Uncomment for Hard coded CorbaConnector configuration example
//        ApplicationContext ctx = new AnnotationConfigApplicationContext(HardCodedConfig.class);
//        Uncomment for XML configuration example
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:app-config.xml");

        SpringCorbaClientSampleMain main = ctx.getBean(SpringCorbaClientSampleMain.class);
        main.setupCorbaServer(new AccountsImpl());

        // Try and create an account
        main.accountsService.createAccount("Lara");

        // Shutdown ORBD
        process.destroy();
    }

    private void setupCorbaServer(AccountsImpl accounts) throws Exception {

        POA poaRoot = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
        poaRoot.the_POAManager().activate();

        org.omg.CORBA.Policy[] policies = {};

        POA poa = poaRoot.create_POA(AccountsImpl.NAME + "_poa", poaRoot.the_POAManager(), policies);
        poa.activate_object(accounts);
        org.omg.CORBA.Object obj = poa.servant_to_reference(accounts);
        try {
            namingContext.bind_new_context(
                    namingContext.to_name(AccountsImpl.CONTEXT));
        } catch (AlreadyBound alreadyBound) {
            System.err.println("Already Bound!");
        }

        namingContext.rebind(
                namingContext.to_name(AccountsImpl.CONTEXT + "/" + AccountsImpl.NAME), obj);

        poaRoot.the_POAManager().activate();

    }
}
