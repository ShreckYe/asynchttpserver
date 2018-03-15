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
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * an inbound {@link RequestWithoutBody} class representing a received request without body that cannot be edited,
 * whose URI path, URI query params, and headers will be decoded only as needed.
 *
 * @author Yongshun Ye
 */
public class DefaultInboundRequestWithoutBody implements RequestWithoutBody {
    final String method;
    final String uri;
    final HttpHeaders headers;

    // Decoded from the uri when needed
    boolean uriToDecode = true;
    String path = null, query = null;
    boolean queryToDecode = true;
    Map<String, List<String>> queryParams = null;

    // Decoded from the headers when needed
    boolean toDecodeHost = true;
    String host = null;
    boolean toDecodeConnection = true;
    String connection = null;
    boolean toDecodeCookie = true;
    Set<Cookie> cookie = null;
    boolean toDecodeContentType = true;
    String contentType = null;
    boolean toDecodeContentLength = true;
    Long contentLength = null;

    /**
     * Creates a new instance with the HTTP method, the URI, the HTTP headers, and the body.
     *
     * @param method  the HTTP method
     * @param uri     the URI
     * @param headers the headers
     */
    public DefaultInboundRequestWithoutBody(String method, String uri, HttpHeaders headers) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
    }

    protected DefaultInboundRequestWithoutBody(HttpRequest httpRequest) {
        this(httpRequest.method().name(), httpRequest.uri(), httpRequest.headers());
    }

    /**
     * Decodes a new instance from the Netty {@link HttpRequest}.
     *
     * @param httpRequest the Netty {@link HttpRequest}
     */
    public static DefaultInboundRequestWithoutBody fromNettyHttpRequest(HttpRequest httpRequest) {
        return new DefaultInboundRequestWithoutBody(httpRequest);
    }

    @Override
    public String method() {
        return method;
    }

    @Override
    public String uri() {
        return uri;
    }

    private void decodeUriIfNeeded() {
        if (uriToDecode) {
            int dividerIndex = uri.indexOf('?');
            if (dividerIndex == -1) {
                path = uri;
                query = null;
            } else {
                path = uri.substring(0, dividerIndex);
                query = uri.substring(dividerIndex + 1);
            }

            uriToDecode = false;
        }
    }

    @Override
    public String path() {
        decodeUriIfNeeded();
        return path;
    }

    @Override
    public String query() {
        decodeUriIfNeeded();
        return query;
    }

    private void decodeQueryIfNeeded() {
        if (queryToDecode) {
            String query = query();
            if (query != null) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(query, false);
                queryParams = queryStringDecoder.parameters();
            }

            queryToDecode = false;
        }
    }

    @Override
    public Map<String, List<String>> queryParams() {
        decodeQueryIfNeeded();
        return queryParams;
    }


    @Override
    public HttpHeaders headers() {
        return headers;
    }

    private void decodeHostIfNeeded() {
        if (toDecodeHost) {
            host = headers.get(RequestHeaderNames.HOST);

            toDecodeHost = false;
        }
    }

    @Override
    public String host() {
        decodeHostIfNeeded();
        return host;
    }

    private void decodeConnectionIfNeeded() {
        if (toDecodeConnection) {
            connection = headers.get(RequestHeaderNames.CONNECTION);

            toDecodeConnection = false;
        }
    }

    @Override
    public String connection() {
        decodeConnectionIfNeeded();
        return connection;
    }

    private void decodeCookieIfNeeded() {
        if (toDecodeCookie) {
            String cookieString = headers.get(RequestHeaderNames.COOKIE);
            if (cookieString != null) ;
            cookie = ServerCookieDecoder.STRICT.decode(cookieString);

            toDecodeCookie = false;
        }
    }

    @Override
    public Set<Cookie> cookie() {
        decodeCookieIfNeeded();
        return cookie;
    }

    private void decodeContentTypeIfNeeded() {
        if (toDecodeContentType) {
            contentType = headers.get(RequestHeaderNames.CONTENT_TYPE);

            toDecodeContentType = false;
        }
    }

    @Override
    public String contentType() {
        decodeContentTypeIfNeeded();
        return contentType;
    }

    private void decodeContentLengthIfNeeded() {
        if (toDecodeContentLength) {
            String contentLengthString = headers.get(RequestHeaderNames.CONTENT_LENGTH);
            if (contentLengthString != null)
                contentLength = Long.parseLong(contentLengthString);

            toDecodeContentLength = false;
        }
    }

    @Override
    public Long contentLength() {
        decodeContentLengthIfNeeded();
        return contentLength;
    }

    @Override
    public String toString() {
        return "DefaultInboundRequestWithoutBody{" +
                "method='" + method + '\'' +
                ", uri='" + uri + '\'' +
                ", headers=" + headers +
                '}';
    }
}
