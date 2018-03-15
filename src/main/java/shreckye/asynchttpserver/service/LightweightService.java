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

import io.netty.channel.ChannelHandlerContext;
import shreckye.asynchttpserver.ConnectionContext;
import shreckye.asynchttpserver.codec.FullRequest;
import shreckye.asynchttpserver.codec.FullResponse;

/**
 * The simplest kind of {@link Service} that serves after a full HTTP request is received
 * and sends back a full HTTP response.
 *
 * @author Yongshun Ye
 */
public abstract class LightweightService implements Service {
    /**
     * Serves a full HTTP request and returns a full HTTP response to send back to the client.
     *
     * @param fullRequest the full HTTP request received
     * @return the full HTTP response to send back the the client
     * @exception Exception when an exception occurs
     */
    public abstract FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception;
}
