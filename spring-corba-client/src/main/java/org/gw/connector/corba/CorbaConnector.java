package org.gw.connector.corba;

import org.omg.CORBA.Object;

public class CorbaConnector extends GenericCorbaConnector<Object> {

    public CorbaConnector() {
    }

    public CorbaConnector(Class<? extends Object> objType, String corbaObjectName) {
        super(objType, corbaObjectName);
    }

    public CorbaConnector(Class<? extends Object> objType) {
        super(objType);
    }

    public CorbaConnector(String corbaObjectName) {
        super(corbaObjectName);
    }

}
