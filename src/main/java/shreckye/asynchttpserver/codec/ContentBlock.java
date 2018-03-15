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
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;

/**
 * A block of data in either an HTTP request or an HTTP response's content.
 *
 * @author Yongshun Ye
 */
public interface ContentBlock extends HttpObject {
    /**
     * Returns the content data of this block.
     *
     * @return the content data as a {@link ByteBuf}
     */
    ByteBuf content();

    /**
     * Converts this instance to a Netty {@link HttpContent}.
     *
     * @return the Netty {@link HttpContent}
     */
    default HttpContent toNettyHttpContent() {
        return new DefaultHttpContent(content());
    }
}
