package org.gw.connector.corba;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ATestUser {

    @Autowired
    private ITestObject obj;

    @Autowired
    private ITestPrototypeObject prototypeObj;

    /**
	 * @return the obj
	 */
	public ITestObject getObj() {
		return obj;
	}

	/**
	 * @param obj the obj to set
	 */
	public void setObj(ITestObject obj) {
		this.obj = obj;
	}

	/**
	 * @return the prototypeObj
	 */
	public ITestPrototypeObject getPrototypeObj() {
		return prototypeObj;
	}

	/**
	 * @param prototypeObj the prototypeObj to set
	 */
	public void setPrototypeObj(ITestPrototypeObject prototypeObj) {
		this.prototypeObj = prototypeObj;
	}

	@PostConstruct
    public void init() throws Exception {
        System.out.println("Testing from ATestUser in @PostConstruct...");
        obj.check(false);
        prototypeObj.check();
    }
}
