package org.gw.connector.corba;

import org.omg.CORBA.Object;

public class SpringLoadedJMXCorbaConnectorAdapter extends GenericSpringLoadedJMXCorbaConnectorAdapter<Object>  {

    public SpringLoadedJMXCorbaConnectorAdapter() {
    }

    public SpringLoadedJMXCorbaConnectorAdapter(Class<? extends Object> objType, String corbaObjectName) {
        super(objType, corbaObjectName);
    }

    public SpringLoadedJMXCorbaConnectorAdapter(Class<? extends Object> objType) {
        super(objType);
    }

    public SpringLoadedJMXCorbaConnectorAdapter(String corbaObjectName) {
        super(corbaObjectName);
    }

}
