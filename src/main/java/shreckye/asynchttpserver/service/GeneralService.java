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

package shreckye.asynchttpserver.service;

import shreckye.asynchttpserver.ConnectionContext;
import shreckye.asynchttpserver.codec.ContentBlock;
import shreckye.asynchttpserver.codec.LastContentBlock;
import shreckye.asynchttpserver.codec.RequestWithoutBody;
import shreckye.asynchttpserver.codec.ResponseWithoutBody;

/**
 * The kind of {@link Service} that serves an HTTP request without its body, the content blocks, and the last content block in sequence.
 * This type of {@link Service} is recommended for serving HTTP requests with large content and sending back HTTP responses with large content so they don't eat up the memory.
 *
 * @author Yongshun Ye
 */
public abstract class GeneralService implements Service {

    /**
     * Serves an HTTP request without its content when the request line and headers are received.
     * This method is called before {@link #onServeContentBlock(ContentBlock, ConnectionContext)} and {@link #onServeLastContentBlock(LastContentBlock, ConnectionContext)} by the server.
     *
     * @param requestWithoutBody the {@link ResponseWithoutBody} received
     * @param connectionContext  the context to send back HTTP objects
     * @throws Exception when an exception occurs
     */
    public abstract void onServeRequestWithoutBody(RequestWithoutBody requestWithoutBody, ConnectionContext connectionContext) throws Exception;

    /**
     * Serves a block of content data when it's received.
     * This method can be called from 0 to multiple times during serving a request.
     * When called, it is called after {@link #onServeRequestWithoutBody(RequestWithoutBody, ConnectionContext)} and before {@link #onServeLastContentBlock(LastContentBlock, ConnectionContext)} by the server.
     *
     * @param contentBlock      the block of content data received
     * @param connectionContext the context to send back HTTP objects
     * @throws Exception when an exception occurs
     */
    public abstract void onServeContentBlock(ContentBlock contentBlock, ConnectionContext connectionContext) throws Exception;

    /**
     * Serves the last block of content data when it's received.
     * This method is called after {@link #onServeRequestWithoutBody(RequestWithoutBody, ConnectionContext)} and {@link #onServeContentBlock(ContentBlock, ConnectionContext)} if any.
     *
     * @param lastContentBlock  the lastblock of content data received
     * @param connectionContext the context to send back HTTP objects
     * @throws Exception when an exception occurs
     */
    public abstract void onServeLastContentBlock(LastContentBlock lastContentBlock, ConnectionContext connectionContext) throws Exception;
}
