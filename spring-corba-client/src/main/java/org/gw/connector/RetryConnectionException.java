package org.gw.connector;

@SuppressWarnings("serial")
public class RetryConnectionException extends Exception {

	public RetryConnectionException() {
		super();
	}

	public RetryConnectionException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RetryConnectionException(String arg0) {
		super(arg0);
	}

	public RetryConnectionException(Throwable arg0) {
		super(arg0);
	}

}
