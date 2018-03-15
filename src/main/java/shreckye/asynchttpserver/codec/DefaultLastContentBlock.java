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
import io.netty.handler.codec.http.LastHttpContent;

/**
 * A default {@link LastContentBlock} implementation.
 *
 * @author Yongshun Ye
 */
public class DefaultLastContentBlock extends DefaultContentBlock implements LastContentBlock {
    public DefaultLastContentBlock(ByteBuf content) {
        super(content);
    }

    /**
     * Decodes a new instance from a Netty {@link LastHttpContent} instance.
     *
     * @param lastHttpContent the Netty {@link LastHttpContent} instance
     * @return the decoded instance
     */
    public static DefaultLastContentBlock fromNettyLastHttpContent(LastHttpContent lastHttpContent) {
        return new DefaultLastContentBlock(lastHttpContent.content());
    }
}
