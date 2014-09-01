package org.gw.samples.corba;

/**
 * Generated from IDL interface "Accounts".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public final class AccountsHolder implements org.omg.CORBA.portable.Streamable {
    public Accounts value;

    public AccountsHolder() {
    }

    public AccountsHolder(final Accounts initial) {
        value = initial;
    }

    public org.omg.CORBA.TypeCode _type() {
        return AccountsHelper.type();
    }

    public void _read(final org.omg.CORBA.portable.InputStream in) {
        value = AccountsHelper.read(in);
    }

    public void _write(final org.omg.CORBA.portable.OutputStream _out) {
        AccountsHelper.write(_out, value);
    }
}
