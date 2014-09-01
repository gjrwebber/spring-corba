package org.gw.samples;

import org.gw.samples.corba.Account;
import org.gw.samples.corba.AccountsPOA;

/**
 * Created by gman on 29/08/2014.
 */
public class AccountsImpl extends AccountsPOA {

    public static final String NAME = "Accounts";
    public static final String CONTEXT = "org.gw.samples";

    @Override
    public void createAccount(Account account) {
        System.out.println("Creating account (" + account.name + ") on the server");
    }
}
