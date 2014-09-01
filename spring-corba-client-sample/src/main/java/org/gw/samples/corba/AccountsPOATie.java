package org.gw.samples.corba;

import org.omg.PortableServer.POA;

/**
 * Generated from IDL interface "Accounts".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public class AccountsPOATie
        extends AccountsPOA {
    private AccountsOperations _delegate;

    private POA _poa;

    public AccountsPOATie(AccountsOperations delegate) {
        _delegate = delegate;
    }

    public AccountsPOATie(AccountsOperations delegate, POA poa) {
        _delegate = delegate;
        _poa = poa;
    }

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

    public AccountsOperations _delegate() {
        return _delegate;
    }

    public void _delegate(AccountsOperations delegate) {
        _delegate = delegate;
    }

    public POA _default_POA() {
        if (_poa != null) {
            return _poa;
        }
        return super._default_POA();
    }

    public void createAccount(org.gw.samples.corba.Account account) {
        _delegate.createAccount(account);
    }

}
