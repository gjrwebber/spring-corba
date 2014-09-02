package org.gw.samples;

import org.gw.connector.corba.GenericSpringLoadedJMXCorbaConnectorAdapter;
import org.gw.samples.corba.Accounts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by gman on 2/09/2014.
 */
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
