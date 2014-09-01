package org.gw.connector;

public interface ITestObject extends org.omg.CORBA.Object {
	boolean check(boolean doException) throws Exception;

	boolean check(Exception throwE, int throwCount) throws Exception;
}
