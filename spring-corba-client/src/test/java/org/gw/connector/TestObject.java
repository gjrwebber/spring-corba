package org.gw.connector;

import org.omg.CORBA.*;
import org.omg.CORBA.Object;

public class TestObject extends ParentTestObject implements ITestObject {

	public TestObject() {
		System.out.println("Creating TestObject..");
	}

	@Override
	public boolean check(boolean doException) throws Exception {
		if (doException) {
			return check(new TestException(), 1);
		} else {
			return check(null, 1);
		}
	}

	private int throwings;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.gw.connector.corba.ITestObject#check
	 * (java.lang.Exception)
	 */
	@Override
	public boolean check(Exception throwE, int throwCount) throws Exception {
		if (throwE != null && throwings++ <= throwCount) {
			System.out.println("TestObject throwing "
					+ throwE.getClass().getSimpleName() + " " + (throwings)
					+ " times...");
			throw throwE;
		}
		System.out.println("TestObject invoked.");
		return tryProtected();
	}
	
	protected boolean tryProtected() {
		return isProtected();
	}


    public boolean _is_a(String repositoryIdentifier) {
        return false;
    }

    public boolean _is_equivalent(org.omg.CORBA.Object other) {
        return false;
    }

    public boolean _non_existent() {
        return false;
    }

    public int _hash(int maximum) {
        return 0;
    }

    public Object _duplicate() {
        return null;
    }

    public void _release() {

    }

    public Object _get_interface_def() {
        return null;
    }

    public Request _request(String operation) {
        return null;
    }

    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result) {
        return null;
    }

    public Request _create_request(Context ctx, String operation, NVList arg_list, NamedValue result, ExceptionList exclist, ContextList ctxlist) {
        return null;
    }

    public Policy _get_policy(int policy_type) {
        return null;
    }

    public DomainManager[] _get_domain_managers() {
        return new DomainManager[0];
    }

    public Object _set_policy_override(Policy[] policies, SetOverrideType set_add) {
        return null;
    }
}
