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

/**
 * A {@link Service} that serves an HTTP request with method "GET" and sends back a JSON content HTTP response.
 *
 * @author Yongshun Ye
 */
public abstract class JsonGetService extends GetLightweightService {
    @Override
    public FullResponse onServeGet(FullRequest fullRequest) throws Exception {
        return DefaultOutboundFullResponse.newJsonInstance(HttpResponseStatus.OK,
                onServeJson(fullRequest));
    }

    /**
     * Serves and returns a JSON string.
     *
     * @param fullRequest the original full request
     * @return the result JSON string
     * @throws Exception
     */
    public abstract String onServeJson(FullRequest fullRequest) throws Exception;
}
