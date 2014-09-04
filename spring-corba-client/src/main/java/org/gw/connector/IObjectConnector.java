package org.gw.connector;

import org.springframework.jmx.export.annotation.ManagedAttribute;

/**
 * Created by gman on 6/08/2014.
 *
 * @param <C> Can be any {@link Object}. This represents the {@link Object}
 *            being connected and is expected to be returned when
 *            getConnectedObject() is called.
 * @author Gman
 */
public interface IObjectConnector<C> extends IConnector, IAsyncConnector {

    /**
     * Returns the connected {@link Object} <C>. ie. If the {@link Object} <C>
     * is not connected and {@link CouldNotConnectException} should be thrown.
     * The {@link ConnectTimeoutException} is thrown if a connection could not
     * be made in the given timeout period if one has been applied.
     *
     * @return The connected {@link Object}
     * @throws CouldNotConnectException
     * @throws ConnectTimeoutException
     */
    C getConnectedObject() throws CouldNotConnectException,
            ConnectTimeoutException;

    /**
     * Returns the connected {@link Object} <C>'s type.
     *
     * @return The connected {@link Object} <C>'s type.
     */
    @ManagedAttribute
    Class<? extends C> getObjType();

    /**
     * @return the connector Object. It may or may not be connected at this point.
     */
    C getObj();

    /**
     * @return true if this connect should block method calls when disconnected.
     */
    @ManagedAttribute
    boolean isBlockOnConnect();

    /**
     * @return true if this IObjectConnector is lazy.
     */
    @ManagedAttribute
    boolean isLazy();

    /**
     * Set to true to make this IObjectConnector lazy
     * @param lazy
     */
    @ManagedAttribute
    void setLazy(boolean lazy);

    /**
     * Decides whether the given Throwable should cause a retry.
     *
     * @param e The Throwable to test
     * @return true if the IConnector should retry to connect when the given Throwable is thrown. False otherwise.
     */
    boolean isRetryException(Throwable e);


}
