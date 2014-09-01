package org.gw.samples.corba;

/**
 * Generated from IDL struct "Account".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public final class AccountHolder
        implements org.omg.CORBA.portable.Streamable {
    public org.gw.samples.corba.Account value;

    public AccountHolder() {
    }

    public AccountHolder(final org.gw.samples.corba.Account initial) {
        value = initial;
    }

    public org.omg.CORBA.TypeCode _type() {
        return org.gw.samples.corba.AccountHelper.type();
    }

    public void _read(final org.omg.CORBA.portable.InputStream _in) {
        value = org.gw.samples.corba.AccountHelper.read(_in);
    }

    public void _write(final org.omg.CORBA.portable.OutputStream _out) {
        org.gw.samples.corba.AccountHelper.write(_out, value);
    }
}
