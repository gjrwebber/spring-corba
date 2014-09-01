package org.gw.samples.corba;


/**
 * Generated from IDL interface "Accounts".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public abstract class AccountsHelper {
    private volatile static org.omg.CORBA.TypeCode _type;

    public static org.omg.CORBA.TypeCode type() {
        if (_type == null) {
            synchronized (AccountsHelper.class) {
                if (_type == null) {
                    _type = org.omg.CORBA.ORB.init().create_interface_tc("IDL:org/gw/samples/Accounts:1.0", "Accounts");
                }
            }
        }
        return _type;
    }

    public static void insert(final org.omg.CORBA.Any any, final org.gw.samples.corba.Accounts s) {
        any.insert_Object(s);
    }

    public static org.gw.samples.corba.Accounts extract(final org.omg.CORBA.Any any) {
        return narrow(any.extract_Object());
    }

    public static String id() {
        return "IDL:org/gw/samples/Accounts:1.0";
    }

    public static Accounts read(final org.omg.CORBA.portable.InputStream in) {
        return narrow(in.read_Object(org.gw.samples.corba._AccountsStub.class));
    }

    public static void write(final org.omg.CORBA.portable.OutputStream _out, final org.gw.samples.corba.Accounts s) {
        _out.write_Object(s);
    }

    public static org.gw.samples.corba.Accounts narrow(final org.omg.CORBA.Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.gw.samples.corba.Accounts) {
            return (org.gw.samples.corba.Accounts) obj;
        } else if (obj._is_a("IDL:org/gw/samples/Accounts:1.0")) {
            org.gw.samples.corba._AccountsStub stub;
            stub = new org.gw.samples.corba._AccountsStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
            return stub;
        } else {
            throw new org.omg.CORBA.BAD_PARAM("Narrow failed");
        }
    }

    public static org.gw.samples.corba.Accounts unchecked_narrow(final org.omg.CORBA.Object obj) {
        if (obj == null) {
            return null;
        } else if (obj instanceof org.gw.samples.corba.Accounts) {
            return (org.gw.samples.corba.Accounts) obj;
        } else {
            org.gw.samples.corba._AccountsStub stub;
            stub = new org.gw.samples.corba._AccountsStub();
            stub._set_delegate(((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate());
            return stub;
        }
    }
}
