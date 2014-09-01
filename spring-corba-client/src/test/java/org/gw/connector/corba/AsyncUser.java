package org.gw.connector.corba;

import org.gw.connector.TestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;


@Service
public class AsyncUser {

    @Autowired
    private ITestObject obj;

    @Autowired
    private ITestPrototypeObject prototypeObj;

    @PostConstruct
    public void init() throws TestException {
        System.out.println("Testing from AsyncUser in @PostConstruct...");
        new Thread(new Runnable() {

            int runs = 10;
            int exRuns = 5;
            boolean throwEx = true;

            @Override
            public void run() {
                int i = 0;
                while (i++ < runs) {
                    try {
                        if (exRuns < i) {
                            throwEx = false;
                        }
                        prototypeObj.check();
                        System.out.println("Trying check(" + throwEx + ")");
                        obj.check(throwEx);
                        System.out.println("check(" + throwEx + ") returned ok.");
                    } catch (Exception e) {
                        System.out.println(e.getClass().getSimpleName() + " caught.");
                        // Expected
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        // Bla
                    }
                }
                System.out.println("Finished AsyncUser test.");
            }
        }).start();
    }
}
