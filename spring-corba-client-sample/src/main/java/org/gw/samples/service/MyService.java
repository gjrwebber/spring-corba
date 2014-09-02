package org.gw.samples.service;

import org.gw.connector.ConnectorMonitorListener;
import org.gw.connector.IConnector;
import org.gw.samples.corba.Account;
import org.gw.samples.corba.Accounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

/**
 * Created by gman on 29/08/2014.
 */

@Service
// Ensure the Connector is initialised before this Service.
@DependsOn("accountsConnector")
public class MyService {

    /**
     * The Accounts CORBA Object is injected here directly.
     */
    @Autowired
    private Accounts accounts;

    public void createAccount(String name) {
        Account account = new Account(name);

        // Every method call on the CORBA Object is tested, and any failed connections will be reported,
        // and depending on the connector parameters, it might block or return and retry asynchronously x times.
        accounts.createAccount(account);
    }

}
