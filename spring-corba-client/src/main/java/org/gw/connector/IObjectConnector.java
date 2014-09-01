package org.gw.connector;

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
    Class<? extends C> getObjType();

    C getObj();

    boolean isBlockOnConnect();

    boolean isLazy();

    void setLazy(boolean lazy);

    /**
     * Decides whether the given Throwable should cause a retry.
     *
     * @param e The Throwable to test
     * @return true if the IConnector should retry to connect when the given Throwable is thrown. False otherwise.
     */
    boolean isRetryException(Throwable e);


}
