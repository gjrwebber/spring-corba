package org.gw.connector.corba;

import org.gw.connector.TestException;
import org.omg.CORBA.*;
import org.omg.CORBA.Object;

public class TestObject implements ITestObject {

    public TestObject() {
	System.out.println("Creating TestObject..");
    }

    @Override
    public boolean check(boolean doException) throws Exception {
	if (doException) {
	    return check(new TestException());
	} else {
	    return check(null);
	}
    }

    private Exception thrown;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.gw.connector.corba.ITestObject#check
     * (java.lang.Exception)
     */
    @Override
    public boolean check(Exception throwE) throws Exception {
	if (throwE != null && !throwE.equals(thrown)) {
	    thrown = throwE;
	    System.out.println("TestObject throwing "
		    + throwE.getClass().getSimpleName() + "...");
	    throw throwE;
	}
	System.out.println("TestObject invoked.");
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context,
     * java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue)
     */
    @Override
    public Request _create_request(Context ctx, String operation,
	    NVList arg_list, NamedValue result) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_create_request(org.omg.CORBA.Context,
     * java.lang.String, org.omg.CORBA.NVList, org.omg.CORBA.NamedValue,
     * org.omg.CORBA.ExceptionList, org.omg.CORBA.ContextList)
     */
    @Override
    public Request _create_request(Context ctx, String operation,
	    NVList arg_list, NamedValue result, ExceptionList exclist,
	    ContextList ctxlist) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_duplicate()
     */
    @Override
    public Object _duplicate() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_get_domain_managers()
     */
    @Override
    public DomainManager[] _get_domain_managers() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_get_interface_def()
     */
    @Override
    public Object _get_interface_def() {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_get_policy(int)
     */
    @Override
    public Policy _get_policy(int policy_type) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_hash(int)
     */
    @Override
    public int _hash(int maximum) {
	// TODO Auto-generated method stub
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_is_a(java.lang.String)
     */
    @Override
    public boolean _is_a(String repositoryIdentifier) {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_is_equivalent(org.omg.CORBA.Object)
     */
    @Override
    public boolean _is_equivalent(Object other) {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_non_existent()
     */
    @Override
    public boolean _non_existent() {
	// TODO Auto-generated method stub
	return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_release()
     */
    @Override
    public void _release() {
	// TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_request(java.lang.String)
     */
    @Override
    public Request _request(String operation) {
	// TODO Auto-generated method stub
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.omg.CORBA.Object#_set_policy_override(org.omg.CORBA.Policy[],
     * org.omg.CORBA.SetOverrideType)
     */
    @Override
    public Object _set_policy_override(Policy[] policies,
	    SetOverrideType set_add) {
	// TODO Auto-generated method stub
	return null;
    }
}
