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

import eu.medsea.mimeutil.MimeUtil;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * an outbound {@link ResponseWithoutBody} class representing a response without body to be sent,
 * whose headers will be encoded as needed (when sent).
 *
 * @author Yongshun Ye
 */
public class DefaultOutboundResponseWithoutBody implements ResponseWithoutBody {
    HttpResponseStatus status;
    long contentLength;
    @Nullable
    String contentType;
    @Nullable
    String connection;
    @Nullable
    Set<Cookie> setCookie;
    HttpHeaders customHeaders;

    protected DefaultOutboundResponseWithoutBody(HttpResponseStatus status) {
        this.status = status;
        customHeaders = new DefaultHttpHeaders();
    }

    /**
     * Creates a new instance with the specified status, content-length, and content-type.
     *
     * @param status
     * @param contentLength
     * @param contentType
     */
    public DefaultOutboundResponseWithoutBody(HttpResponseStatus status, long contentLength, @Nullable String contentType) {
        this.status = status;
        this.contentLength = contentLength;
        this.contentType = contentType;
        customHeaders = new DefaultHttpHeaders();
    }

    /*
     * Creates a new instance with the specified status, content-length, content-type, and set-cookie.
     *
     * @param status
     * @param contentLength
     * @param contentType
     * @param setCookie
     *
    public DefaultOutboundResponseWithoutBody(HttpResponseStatus status, long contentLength, @Nullable String contentType, @Nullable Set<Cookie> setCookie) {
        this.status = status;
        this.contentLength = contentLength;
        this.contentType = contentType;
        this.setCookie = setCookie;
    }*/

    /**
     * Creates a new instance of with a file's size as "content-length" and its "content-type" determined automatically from the file.
     *
     * @param status the status
     * @param file   the file
     * @return the response
     */
    public static DefaultOutboundResponseWithoutBody newAutoFileInstance(HttpResponseStatus status, File file) throws FileNotFoundException, IOException {
        String contentType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file)).getMediaType();
        return new DefaultOutboundResponseWithoutBody(status, file.length(), contentType);
    }

    @Override
    public HttpResponseStatus status() {
        return status;
    }

    /**
     * Sets the status of this instance.
     *
     * @param status the status
     */
    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    @Override
    public HttpHeaders headers() {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(ResponseHeaderNames.CONTENT_LENGTH, contentLength());
        addIfValueNotNull(headers, ResponseHeaderNames.CONTENT_TYPE, contentType());
        addIfValueNotNull(headers, ResponseHeaderNames.CONNECTION, connection());
        List<String> setCookieHeaders = setCookieHeaders();
        if (setCookieHeaders != null)
            for (String setCookieHeader : setCookieHeaders)
                headers.add(ResponseHeaderNames.SET_COOKIE, setCookieHeader);

        headers.add(customHeaders);

        return headers;
    }

    private void addIfValueNotNull(HttpHeaders headers, String name, Object value) {
        if (value != null)
            headers.add(name, value);
    }


    public long contentLengthPrimative() {
        return contentLength;
    }

    @Override
    public Long contentLength() {
        return contentLength;
    }

    /**
     * Sets the value of the"content-length" header.
     *
     * @param contentLength the value of the"content-length" header
     */
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    /**
     * Sets the value of the"content-type" header.
     *
     * @param contentType the value of the"content-type" header
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String connection() {
        return connection;
    }

    /**
     * Sets the value of the"connection" header.
     *
     * @param connection the value of the"connection" header
     */
    public void setConnection(String connection) {
        this.connection = connection;
    }

    @Override
    public Set<Cookie> setCookie() {
        return setCookie;
    }

    private List<String> setCookieHeaders() {
        Set<Cookie> setCookie = setCookie();
        return setCookie != null ?
                ServerCookieEncoder.STRICT.encode(setCookie) :
                null;
    }

    /**
     * Sets the value of the"set-cookie" header.
     *
     * @param setCookie the value of the"set-cookie" header as a {@link Set<Cookie>}
     */
    public void setSetCookie(Set<Cookie> setCookie) {
        this.setCookie = setCookie;
    }

    /**
     * Returns the custom headers that you add yourself in addition to this class's declared ones.
     *
     * @return the custom headers
     */
    public HttpHeaders customHeaders() {
        return customHeaders;
    }

    /**
     * Adds a new header with the specified name and value.
     *
     * @param name  the name of the header
     * @param value the value of the header
     */
    public void addCustomHeader(String name, Object value) {
        customHeaders.add(name, value);
    }

    /**
     * Adds all header entries of the specified custom headers.
     *
     * @param customHeaders the custom headers to add
     */
    public void addCustomHeaders(HttpHeaders customHeaders) {
        this.customHeaders.add(customHeaders);
    }
}
