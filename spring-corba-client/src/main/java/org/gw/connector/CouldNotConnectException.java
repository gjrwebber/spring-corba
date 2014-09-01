package org.gw.connector;

@SuppressWarnings("serial")
public class CouldNotConnectException extends Exception {

	public CouldNotConnectException() {
		super();
	}

	public CouldNotConnectException(String message, Throwable cause) {
		super(message, cause);
	}

	public CouldNotConnectException(String message) {
		super(message);
	}

	public CouldNotConnectException(Throwable cause) {
		super(cause);
	}

}
