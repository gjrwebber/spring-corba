package org.gw.connector;

@SuppressWarnings("serial")
public class ConnectedObjectDisconnectedException extends RuntimeException {

	public ConnectedObjectDisconnectedException() {
	}

	public ConnectedObjectDisconnectedException(String arg0) {
		super(arg0);
	}

	public ConnectedObjectDisconnectedException(Throwable arg0) {
		super(arg0);
	}

	public ConnectedObjectDisconnectedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
