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
@DependsOn("accountsConnector")
public class MyService {

    @Autowired
    private Accounts accounts;

    public void createAccount(String name) {
        Account account = new Account(name);
        accounts.createAccount(account);
    }

}
