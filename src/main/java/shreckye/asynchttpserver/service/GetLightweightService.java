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

import io.netty.handler.codec.http.HttpResponseStatus;
import shreckye.asynchttpserver.codec.DefaultOutboundFullResponse;
import shreckye.asynchttpserver.codec.FullRequest;
import shreckye.asynchttpserver.codec.FullResponse;
import shreckye.asynchttpserver.codec.Method;

/**
 * The kind of {@link LightweightService} that only serves a full request with a "GET" request method.
 * If the request method isn't "GET", a response with status "405 Method Not Allowed" will be sent back.
 *
 * @author Yongshun Ye
 */
public abstract class GetLightweightService extends LightweightService {
    @Override
    public FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception {
        if (!fullRequest.method().equals(Method.GET))
            return new DefaultOutboundFullResponse(HttpResponseStatus.METHOD_NOT_ALLOWED);

        return onServeGet(fullRequest);
    }

    /**
     * serves a full request with a "GET" request method.
     *
     * @param fullRequest the full request with a "GET" request method.
     * @return the full response to send back
     * @throws Exception when an exception occurred
     */
    public abstract FullResponse onServeGet(FullRequest fullRequest) throws Exception;
}
