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

package shreckye.asynchttpserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

/**
 * An inbound full HTTP request that cannot be edited,
 * whose URI path, URI query params, and headers will be decoded only as needed.
 *
 * @author Yongshun Ye
 */
public class DefaultInboundFullRequest extends DefaultInboundRequestWithoutBody implements FullRequest {
    ByteBuf bodyContent;

    /**
     * Creates a new instance with the HTTP method, the URI, the HTTP headers, and the body.
     *
     * @param method      the HTTP method
     * @param uri         the URI
     * @param headers     the headers
     * @param bodyContent the body content as a {@link ByteBuf}
     */
    public DefaultInboundFullRequest(String method, String uri, HttpHeaders headers, ByteBuf bodyContent) {
        super(method, uri, headers);
        this.bodyContent = bodyContent;
    }

    protected DefaultInboundFullRequest(FullHttpRequest fullHttpRequest) {
        super(fullHttpRequest);
        bodyContent = fullHttpRequest.content();
    }

    /**
     * Decodes a new instance from a Netty {@link FullHttpRequest}.
     *
     * @param fullHttpRequest the Netty {@link FullHttpRequest} instance
     */
    public static DefaultInboundFullRequest fromNettyFullHttpResponse(FullHttpRequest fullHttpRequest) {
        return new DefaultInboundFullRequest(fullHttpRequest);
    }


    @Override
    public ByteBuf bodyContent() {
        return bodyContent;
    }
}
