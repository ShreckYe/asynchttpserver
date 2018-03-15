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

import io.netty.handler.codec.http.cookie.Cookie;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Representing the request line and the header fields of an HTTP response.
 *
 * @author Yongshun Ye
 */
public interface RequestWithoutBody extends MessageWithoutBody, HttpObject {
    /**
     * Returns the request method of this instance.
     *
     * @return the request method of this instance
     */
    String method();


    /**
     * Returns the URI of this instance.
     *
     * @return the URI of this instance
     */
    String uri();


    /**
     * Returns the path in the URI.
     *
     * @return the path in the URI
     */
    String path();

    /**
     * Returns the query string in the URI.
     *
     * @return the query string in the URI
     */
    String query();

    /**
     * Returns the query parameters in the URI.
     *
     * @return the query parameters in the URI as a map
     */
    Map<String, List<String>> queryParams();


    /**
     * Returns the value of the "host" header.
     *
     * @return the value of the "host" header
     */
    String host();

    /**
     * Returns the value of the "connection" header.
     *
     * @return the value of the "connection" header
     */
    String connection();

    /**
     * Decodes the cookies in the "cookie" header.
     *
     * @return the cookies in the "cookie" header
     */
    Set<Cookie> cookie();

    /**
     * Returns the value of the "content-type" header.
     *
     * @return the value of the "content-type" header
     * @see MediaType
     * @see eu.medsea.mimeutil.MimeUtil
     */
    String contentType();

    /**
     * Decodes the value of the "content-length" header.
     *
     * @return the value of the "content-length" header in {@link Long}
     */
    Long contentLength();
}
