/*
 *    Copyright 2018 Yongshun Ye
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package shreckye.asynchttpserver;

import shreckye.asynchttpserver.codec.RequestWithoutBody;
import shreckye.asynchttpserver.service.Service;

/**
 * A handler that handles state changes in an HTTP connection.
 *
 * @author Yongshun Ye
 */
public interface ConnectionHandler {
    byte STATE_INITIAL = 0, STATE_RESPONSE_WITHOUT_BODY_SENT = 1, STATE_LAST_CONTENT_BLOCK_SENT = 2;

    /**
     * Returns the state of the response of the current service.
     *
     * @return the state of the response of the current service
     */
    byte serviceResponseState();

    /**
     * Sets the state of the response of the current service.
     *
     * @param serviceResponseState the state to set
     */
    void setServiceResponseState(byte serviceResponseState);

    /**
     * Called when a client is connected.
     *
     * @param connectionContext the connection context
     */
    void onConnected(ConnectionContext connectionContext);

    /**
     * Called when a client is disconnected.
     *
     * @param connectionContext the connection context
     */
    void onDisconnected(ConnectionContext connectionContext);

    /**
     * Called when an HTTP request is received.
     *
     * @param connectionContext  the connection context
     * @param requestWithoutBody the received {@link RequestWithoutBody}
     */
    void onServiceCreated(ConnectionContext connectionContext, RequestWithoutBody requestWithoutBody, Service service);

    /**
     * Handles a network connection exception.
     *
     * @param connectionContext the connection context
     */
    void onConnectionThrowable(ConnectionContext connectionContext, Throwable t);

    /**
     * Handles an exception during a {@link Service}.
     *
     * @param connectionContext the connection context
     * @param service           the {@link Service} the was serving an HTTP request when this exception occured
     * @param t                 the exception to handle
     */
    void onServiceThrowable(ConnectionContext connectionContext, Service service, Throwable t);
}
