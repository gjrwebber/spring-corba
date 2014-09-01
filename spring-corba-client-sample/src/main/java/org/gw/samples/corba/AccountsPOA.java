package org.gw.samples.corba;


/**
 * Generated from IDL interface "Accounts".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public abstract class AccountsPOA
        extends org.omg.PortableServer.Servant
        implements org.omg.CORBA.portable.InvokeHandler, org.gw.samples.corba.AccountsOperations {
    static private final java.util.HashMap<String, Integer> m_opsHash = new java.util.HashMap<String, Integer>();

    static {
        m_opsHash.put("createAccount", Integer.valueOf(0));
    }

    private String[] ids = {"IDL:org/gw/samples/Accounts:1.0"};

    public org.gw.samples.corba.Accounts _this() {
        org.omg.CORBA.Object __o = _this_object();
        org.gw.samples.corba.Accounts __r = org.gw.samples.corba.AccountsHelper.narrow(__o);
        return __r;
    }

    public org.gw.samples.corba.Accounts _this(org.omg.CORBA.ORB orb) {
        org.omg.CORBA.Object __o = _this_object(orb);
        org.gw.samples.corba.Accounts __r = org.gw.samples.corba.AccountsHelper.narrow(__o);
        return __r;
    }

    public org.omg.CORBA.portable.OutputStream _invoke(String method, org.omg.CORBA.portable.InputStream _input, org.omg.CORBA.portable.ResponseHandler handler)
            throws org.omg.CORBA.SystemException {
        org.omg.CORBA.portable.OutputStream _out = null;
        // do something
        // quick lookup of operation
        java.lang.Integer opsIndex = (java.lang.Integer) m_opsHash.get(method);
        if (null == opsIndex)
            throw new org.omg.CORBA.BAD_OPERATION(method + " not found");
        switch (opsIndex.intValue()) {
            case 0: // createAccount
            {
                org.gw.samples.corba.Account _arg0 = org.gw.samples.corba.AccountHelper.read(_input);
                _out = handler.createReply();
                createAccount(_arg0);
                break;
            }
        }
        return _out;
    }

    public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] obj_id) {
        return ids;
    }
}
