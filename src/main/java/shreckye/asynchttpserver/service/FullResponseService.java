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

import shreckye.asynchttpserver.codec.*;

/**
 * The kind of {@link Service} that serves an HTTP request without its body, the content blocks, and the last content block in sequence,
 * and builds and sends back a full HTTP response.
 * With this type of {@link Service}, the response is automatically send back to the client after all method callbacks.
 * This type of {@link Service} is recommended for serving HTTP requests with large content so they don't eat up the memory.
 *
 * @param <FullResponseImpl> the class of the actual instance of {@link FullResponse}
 * @author Yongshun Ye
 */
public abstract class FullResponseService<FullResponseImpl extends FullResponse> implements Service {
    //private FullResponseImpl fullResponse;

    /**
     * Creates a full HTTP response instance for edits when serving.
     *
     * @return a full HTTP response instance
     */
    public abstract FullResponseImpl onCreateFullResponse();

    /*public final void onServeRequestWithoutBody(RequestWithoutBody requestWithoutBody) {
        onServeRequestWithoutBody(requestWithoutBody, fullResponse);
    }*/

    /**
     * Serves an HTTP request without its content when the request line and headers are received.
     * This method is called before {@link #onServeContentBlock(ContentBlock, FullResponseImpl)} and {@link #onServeLastContentBlock(LastContentBlock, FullResponseImpl)} by the server.
     *
     * @param requestWithoutBody the {@link ResponseWithoutBody} received
     * @param fullResponse       the created response to edit before sent
     * @throws Exception when an exception occurs
     */
    public abstract void onServeRequestWithoutBody(RequestWithoutBody requestWithoutBody, FullResponseImpl fullResponse) throws Exception;


    /*public final void onServeContentBlock(ContentBlock contentBlock) {
        onServeContentBlock(contentBlock, fullResponse);
    }*/

    /**
     * Serves a block of content data when it's received.
     * This method can be called from 0 to multiple times during serving a request.
     * When called, it is called after {@link #onServeRequestWithoutBody(RequestWithoutBody, FullResponseImpl)} and before {@link #onServeLastContentBlock(LastContentBlock, FullResponseImpl)} by the server.
     *
     * @param contentBlock the block of content data received
     * @param fullResponse the created response to edit before sent
     * @throws Exception when an exception occurs
     */
    public abstract void onServeContentBlock(ContentBlock contentBlock, FullResponseImpl fullResponse) throws Exception;

    /*public final void onServeLastContentBlock(LastContentBlock lastContentBlock) {
        onServeLastContentBlock(lastContentBlock, fullResponse);
    }*/

    /**
     * Serves the last block of content data when it's received.
     * This method is called after {@link #onServeRequestWithoutBody(RequestWithoutBody, FullResponseImpl)} and {@link #onServeContentBlock(ContentBlock, FullResponseImpl)} if any.
     *
     * @param lastContentBlock the last block of content data received
     * @param fullResponse     the created response to edit before sent
     * @throws Exception when an exception occurs
     */
    public abstract void onServeLastContentBlock(LastContentBlock lastContentBlock, FullResponseImpl fullResponse) throws Exception;
}
