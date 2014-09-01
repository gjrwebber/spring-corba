package org.gw.connector;

@SuppressWarnings("serial")
public class ConnectTimeoutException extends CouldNotConnectException {

	public ConnectTimeoutException() {
		super("Timed out whilst attempting to connect.");
	}

	public ConnectTimeoutException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConnectTimeoutException(String message) {
		super(message);
	}

	public ConnectTimeoutException(Throwable cause) {
		super(cause);
	}

}
