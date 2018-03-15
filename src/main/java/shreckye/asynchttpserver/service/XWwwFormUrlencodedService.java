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
import io.netty.handler.codec.http.QueryStringDecoder;
import shreckye.asynchttpserver.codec.DefaultOutboundFullResponse;
import shreckye.asynchttpserver.codec.FullRequest;
import shreckye.asynchttpserver.codec.FullResponse;
import shreckye.asynchttpserver.codec.MediaType;

import java.util.List;
import java.util.Map;

/**
 * Serves a request with the "content-type" "x-www-form-urlencoded" and decodes the params.
 *
 * @author Yongshun Ye
 */
public abstract class XWwwFormUrlencodedService extends PostLightweightService {
    @Override
    public FullResponse onServePost(FullRequest fullRequest) throws Exception {
        if (!MediaType.APPLICATION_X_WWW_FORM_URLENCODED.equals(fullRequest.contentType()))
            return new DefaultOutboundFullResponse(HttpResponseStatus.BAD_REQUEST);

        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(new String(fullRequest.bodyContent().array()), false);
        return onServeFormParams(queryStringDecoder.parameters());
    }

    /**
     * Serves the form parameters and return a full response.
     *
     * @param formParams form parameters
     * @return the full response
     */
    public abstract FullResponse onServeFormParams(Map<String, List<String>> formParams);
}
