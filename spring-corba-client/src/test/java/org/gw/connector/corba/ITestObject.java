package org.gw.connector.corba;

import org.omg.CORBA.Object;

public interface ITestObject extends Object {
    boolean check(boolean doException) throws Exception;
    boolean check(Exception throwE) throws Exception;
}
