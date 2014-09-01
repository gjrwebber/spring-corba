package org.gw.samples.corba;


/**
 * Generated from IDL interface "Accounts".
 *
 * @author JacORB IDL compiler V 3.2, 07-Dec-2012
 * @version generated at 29/08/2014 3:50:46 PM
 */

public class _AccountsStub
        extends org.omg.CORBA.portable.ObjectImpl
        implements org.gw.samples.corba.Accounts {
    public final static java.lang.Class _opsClass = org.gw.samples.corba.AccountsOperations.class;
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;
    private String[] ids = {"IDL:org/gw/samples/Accounts:1.0"};

    public String[] _ids() {
        return ids;
    }

    public void createAccount(org.gw.samples.corba.Account account) {
        while (true) {
            if (!this._is_local()) {
                org.omg.CORBA.portable.InputStream _is = null;
                org.omg.CORBA.portable.OutputStream _os = null;
                try {
                    _os = _request("createAccount", true);
                    org.gw.samples.corba.AccountHelper.write(_os, account);
                    _is = _invoke(_os);
                    return;
                } catch (org.omg.CORBA.portable.RemarshalException _rx) {
                    continue;
                } catch (org.omg.CORBA.portable.ApplicationException _ax) {
                    String _id = _ax.getId();
                    try {
                        _ax.getInputStream().close();
                    } catch (java.io.IOException e) {
                        throw new RuntimeException("Unexpected exception " + e.toString());
                    }
                    throw new RuntimeException("Unexpected exception " + _id);
                } finally {
                    if (_os != null) {
                        try {
                            _os.close();
                        } catch (java.io.IOException e) {
                            throw new RuntimeException("Unexpected exception " + e.toString());
                        }
                    }
                    this._releaseReply(_is);
                }
            } else {
                org.omg.CORBA.portable.ServantObject _so = _servant_preinvoke("createAccount", _opsClass);
                if (_so == null)
                    continue;
                AccountsOperations _localServant = (AccountsOperations) _so.servant;
                try {
                    _localServant.createAccount(account);
                    if (_so instanceof org.omg.CORBA.portable.ServantObjectExt)
                        ((org.omg.CORBA.portable.ServantObjectExt) _so).normalCompletion();
                    return;
                } catch (RuntimeException re) {
                    if (_so instanceof org.omg.CORBA.portable.ServantObjectExt)
                        ((org.omg.CORBA.portable.ServantObjectExt) _so).exceptionalCompletion(re);
                    throw re;
                } catch (java.lang.Error err) {
                    if (_so instanceof org.omg.CORBA.portable.ServantObjectExt)
                        ((org.omg.CORBA.portable.ServantObjectExt) _so).exceptionalCompletion(err);
                    throw err;
                } finally {
                    _servant_postinvoke(_so);
                }
            }

        }

    }

}
