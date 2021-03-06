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

import io.netty.handler.codec.http.HttpHeaders;

/**
 * Representing an HTTP message, either an HTTP request or an HTTP response.
 *
 * @author Yongshun Ye
 */
public interface MessageWithoutBody extends HttpObject {
    //byte protocolVersion();

    /**
     * Returns the HTTP headers for this HTTP message.
     * Refer to {@link RequestHeaderNames}, {@link ResponseHeaderNames}, and {@link io.netty.handler.codec.http.HttpHeaderNames} for header names.
     *
     * @return the HTTP headers for this HTTP message
     */
    HttpHeaders headers();
}
