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
import io.netty.handler.codec.http.HttpContent;


/**
 * A default {@link ContentBlock} implementation.
 *
 * @author Yongshun Ye
 */
public class DefaultContentBlock implements ContentBlock {
    ByteBuf content;

    public DefaultContentBlock(ByteBuf content) {
        this.content = content;
    }

    /**
     * Decodes a new instance from a Netty {@link HttpContent} instance.
     *
     * @param httpContent the Netty {@link HttpContent} instance
     * @return the decoded instance
     */
    public static DefaultLastContentBlock fromNettyHttpContent(HttpContent httpContent) {
        return new DefaultLastContentBlock(httpContent.content());
    }

    @Override
    public ByteBuf content() {
        return content;
    }
}
