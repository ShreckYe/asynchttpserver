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
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpResponseStatus;
import shreckye.asynchttpserver.service.SimpleFileService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class DefaultOutboundFullResponse extends DefaultOutboundResponseWithoutBody implements FullResponse {
    @Nonnull
    ByteBuf bodyContent;

    /**
     * Creates a new instance with the specified status.
     *
     * @param status
     */
    public DefaultOutboundFullResponse(HttpResponseStatus status) {
        super(status);
        bodyContent = Unpooled.EMPTY_BUFFER;
    }

    /**
     * Creates a new instance with the specified status, content-length, content-type, and body content.
     *
     * @param status
     * @param contentLength
     * @param contentType
     * @param bodyContent
     */
    public DefaultOutboundFullResponse(HttpResponseStatus status, long contentLength, String contentType, ByteBuf bodyContent) {
        super(status, contentLength, contentType);
        this.bodyContent = bodyContent;
    }

    /**
     * Creates a new instance with the specified status, content-type, and body content
     *
     * @param status
     * @param contentType
     * @param bodyContent
     */
    public DefaultOutboundFullResponse(HttpResponseStatus status, @Nullable String contentType, @Nonnull ByteBuf bodyContent) {
        super(status, bodyContent.readableBytes(), contentType);
        this.bodyContent = bodyContent;
    }

    public DefaultOutboundFullResponse(HttpResponseStatus status, @Nullable String contentType, @Nonnull byte[] bytes) {
        this(status, contentType, Unpooled.wrappedBuffer(bytes));
    }

    public DefaultOutboundFullResponse(HttpResponseStatus status, @Nullable String contentType, @Nonnull String string) {
        this(status, contentType, string.getBytes());
    }

    public static DefaultOutboundFullResponse newHtmlInstance(HttpResponseStatus status, String html) {
        return new DefaultOutboundFullResponse(status, MediaType.TEXT_HTML, html);
    }

    public static DefaultOutboundFullResponse newJsonInstance(HttpResponseStatus status, String json) {
        return new DefaultOutboundFullResponse(status, MediaType.APPLICATION_JSON, json);
    }

    public static DefaultOutboundFullResponse newArbitraryBinaryDataInstance(HttpResponseStatus status, byte[] data) {
        return new DefaultOutboundFullResponse(status, MediaType.APPLICATION_OCTET_STREAM, data);
    }

    public static DefaultOutboundFullResponse newArbitraryBinaryDataInstance(HttpResponseStatus status, ByteBuf data) {
        return new DefaultOutboundFullResponse(status, MediaType.APPLICATION_OCTET_STREAM, data);
    }

    /**
     * Creates a new instance of with a file's content as body and its "content-type" determined automatically from the file.
     * Note that this method loads the whole file into memory and therefore is not recommended for large files.
     * If you want to serve files simply, you should use or extend {@link SimpleFileService}.
     *
     * @param status the status
     * @param file   the file
     * @return the full response
     */
    public static DefaultOutboundFullResponse newAutoFileInstance(HttpResponseStatus status, File file) throws FileNotFoundException, IOException {
        String contentType = MimeUtil.getMostSpecificMimeType(MimeUtil.getMimeTypes(file)).getMediaType();
        try (FileChannel fileChannel = new FileInputStream(file).getChannel()) {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) file.length());
            fileChannel.read(byteBuffer);
            return new DefaultOutboundFullResponse(status, contentType, Unpooled.wrappedBuffer(byteBuffer));
        }
    }

    @Override
    public ByteBuf bodyContent() {
        return bodyContent;
    }

    /**
     * Sets the body content of this response.
     *
     * @param bodyContent the body content
     */
    public void setBodyContent(ByteBuf bodyContent) {
        this.bodyContent = bodyContent;
    }
}
