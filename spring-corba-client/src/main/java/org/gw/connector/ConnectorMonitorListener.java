package org.gw.connector;

/**
 * Callback for notification of monitoring events.
 *
 * @author gman
 * @since 1.0
 * @version 1.0
 *
 */
public interface ConnectorMonitorListener {
	void connectionDown(IConnector connector);

	void connectionUp(IConnector connector);
}
