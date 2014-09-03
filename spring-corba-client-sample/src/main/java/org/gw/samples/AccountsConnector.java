package org.gw.samples;

import org.gw.connector.corba.GenericCorbaConnector;
import org.gw.samples.corba.Accounts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by gman on 2/09/2014.
 */
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
