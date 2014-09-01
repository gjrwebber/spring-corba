package org.gw.samples;

import org.gw.connector.ConnectorMonitorListener;
import org.gw.connector.IConnector;
import org.springframework.stereotype.Component;

/**
 * Created by gman on 1/09/2014.
 */
@Component
public class CorbaConnectionListener implements ConnectorMonitorListener {

    @Override
    public void connectionDown(IConnector connector) {
        System.out.println("CORBA Connection Down.");
    }

    @Override
    public void connectionUp(IConnector connector) {
        System.out.println("CORBA Connection Up.");
    }

}
