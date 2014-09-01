package org.gw.samples.corba;


/**
 * Generated from IDL struct "Account".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public abstract class AccountHelper {
    private volatile static org.omg.CORBA.TypeCode _type;

    public static org.omg.CORBA.TypeCode type() {
        if (_type == null) {
            synchronized (AccountHelper.class) {
                if (_type == null) {
                    _type = org.omg.CORBA.ORB.init().create_struct_tc(org.gw.samples.corba.AccountHelper.id(), "Account", new org.omg.CORBA.StructMember[]{new org.omg.CORBA.StructMember("name", org.omg.CORBA.ORB.init().create_string_tc(0), null)});
                }
            }
        }
        return _type;
    }

    public static void insert(final org.omg.CORBA.Any any, final org.gw.samples.corba.Account s) {
        any.type(type());
        write(any.create_output_stream(), s);
    }

    public static org.gw.samples.corba.Account extract(final org.omg.CORBA.Any any) {
        org.omg.CORBA.portable.InputStream in = any.create_input_stream();
        try {
            return read(in);
        } finally {
            try {
                in.close();
            } catch (java.io.IOException e) {
                throw new RuntimeException("Unexpected exception " + e.toString());
            }
        }
    }

    public static String id() {
        return "IDL:org/gw/samples/Account:1.0";
    }

    public static org.gw.samples.corba.Account read(final org.omg.CORBA.portable.InputStream in) {
        org.gw.samples.corba.Account result = new org.gw.samples.corba.Account();
        result.name = in.read_string();
        return result;
    }

    public static void write(final org.omg.CORBA.portable.OutputStream out, final org.gw.samples.corba.Account s) {
        java.lang.String tmpResult0 = s.name;
        out.write_string(tmpResult0);
    }
}
