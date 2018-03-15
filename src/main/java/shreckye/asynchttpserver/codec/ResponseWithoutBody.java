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

import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;

import java.util.Set;

/**
 * Representing the status line and the header fields of an HTTP response.
 *
 * @author Yongshun Ye
 */
public interface ResponseWithoutBody extends MessageWithoutBody, HttpObject {
    /**
     * Returns the HTTP response status of this instance.
     *
     * @return the HTTP response status
     * @see HttpResponseStatus
     */
    HttpResponseStatus status();

    /**
     * Returns the status code of the HTTP response status of this instance.
     *
     * @return the status code of the HTTP response status
     * @see HttpResponseStatus#code()
     */
    default int statusCode() {
        return status().code();
    }

    /**
     * Returns the reason phrase of the HTTP response status of this instance.
     *
     * @return the reason phrase of the HTTP response status
     * @see HttpResponseStatus#reasonPhrase()
     */
    default String statusReasonPhrase() {
        return status().reasonPhrase();
    }

    /**
     * Returns the value of the "connection" header.
     *
     * @return the value of the "connection" header
     */
    String connection();

    /**
     * Decodes the cookies in the "set-cookie" header.
     *
     * @return the cookies in the "set-cookie" header
     */
    Set<Cookie> setCookie();

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

    /**
     * Converts this instance to a Netty {@link HttpResponse}.
     *
     * @return a Netty {@link HttpResponse}
     */
    default HttpResponse toNettyHttpResponse() {
        return new DefaultHttpResponse(HttpVersion.HTTP_1_1, status(), headers());
    }
}
