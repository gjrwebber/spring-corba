package org.gw.connector.corba;

import org.omg.CORBA.ORB;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.Properties;

/**
 * Created by gman on 29/08/2014.
 */
@Service
public class ORBFactoryBean implements FactoryBean<ORB> {

    @Value("${naming.service.host:127.0.0.1}")
    private String namingServiceHost;
    @Value("${naming.service.port:14002}")
    private String namingServicePort;
    @Value("${orb.class:}")
    private String orbClass;
    @Value("${orb.singleton.class:}")
    private String orbSingletonClass;

    private ORB orb;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        if (!StringUtils.isEmpty(orbClass)) {
            props.setProperty("org.omg.CORBA.ORBClass", orbClass);
        }
        if (!StringUtils.isEmpty(orbSingletonClass)) {
            props.setProperty("org.omg.CORBA.ORBSingletonClass", orbSingletonClass);
        }

        orb = ORB.init(new String[]{"-ORBInitRef", "NameService=corbaloc:iiop:"
                + namingServiceHost + ":" + namingServicePort + "/NameService"}, props);
    }

    @Override
    public ORB getObject() throws Exception {
        return orb;
    }

    @Override
    public Class<?> getObjectType() {
        return ORB.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
