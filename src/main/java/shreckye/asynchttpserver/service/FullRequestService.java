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

package shreckye.asynchttpserver.service;

import shreckye.asynchttpserver.ConnectionContext;
import shreckye.asynchttpserver.codec.FullRequest;

/**
 * The kind of {@link Service} that serves after a full HTTP request is received.
 *
 * @author Yongshun Ye
 */
public abstract class FullRequestService implements Service {
    /**
     * Serves a full HTTP request and returns a full HTTP response to send back to the client.
     *
     * @param fullRequest       the received full HTTP request
     * @param connectionContext the context to send back HTTP objects
     * @throws Exception when an exception occurs
     */
    public abstract void onServeFullRequest(FullRequest fullRequest, ConnectionContext connectionContext) throws Exception;
}
