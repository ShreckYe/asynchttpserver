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

package shreckye.asynchttpserver;

import shreckye.asynchttpserver.codec.*;

import java.net.SocketAddress;

import static shreckye.asynchttpserver.ConnectionHandler.*;

/**
 * A connection context to do operations on the connection send response data.
 *
 * @param <Future> the future type that each asynchronous operation returns
 * @author Yongshun Ye
 */
public abstract class AbstractConnectionContext<Future> {

    public abstract byte serviceResponseState();

    public abstract void setServiceResponseState(byte serviceResponseState);

    /**
     * Closes this connection asynchronously.
     *
     * @return the future of this close operation
     */
    public abstract Future closeAsync();

    public abstract SocketAddress remoteAddress();

    /**
     * Sends a {@link ResponseWithoutBody} to the client through the connection.
     * This method must be called before {@link #sendContentBlock(ContentBlock)} and {@link #sendLastContentBlock(LastContentBlock)}.
     *
     * @param responseWithoutBody the {@link ResponseWithoutBody} to send
     * @return the future of this operation
     * @throws IllegalStateException if this method is called at the wrong time
     */
    public Future sendResponseWithoutBody(ResponseWithoutBody responseWithoutBody) throws IllegalStateException {
        if (serviceResponseState() == STATE_INITIAL) {
            setServiceResponseState(STATE_RESPONSE_WITHOUT_BODY_SENT);
            return sendResponseWithoutBodyImpl(responseWithoutBody);
        } else
            throw new IllegalStateException("a response without body already sent");
    }

    public abstract Future sendResponseWithoutBodyImpl(ResponseWithoutBody responseWithoutBody);

    /**
     * Sends a {@link ContentBlock} to the client through the connection.
     * This method must be called after {@link #sendResponseWithoutBody(ResponseWithoutBody)} and before {@link #sendLastContentBlock(LastContentBlock)} if called.
     * It can be called 0 to multiple times to send all the data.
     *
     * @param contentBlock the {@link ContentBlock} to send
     * @return the future of this operation
     * @throws IllegalStateException if this method is called at the wrong time
     */
    public Future sendContentBlock(ContentBlock contentBlock) throws IllegalStateException {
        byte serviceResponseState = serviceResponseState();
        if (serviceResponseState == STATE_RESPONSE_WITHOUT_BODY_SENT)
            return sendContentBlockImpl(contentBlock);
        else if (serviceResponseState == STATE_INITIAL)
            throw new IllegalStateException("a response without body must be sent before content blocks");
        else
            throw new IllegalStateException("no more content blocks can be sent after the last content block is sent");
    }

    public abstract Future sendContentBlockImpl(ContentBlock contentBlock);

    /**
     * Sends a {@link LastContentBlock} to the client through the connection.
     * This method must be called after {@link #sendResponseWithoutBody(ResponseWithoutBody)} and {@link #sendContentBlock(ContentBlock)} if any.
     *
     * @param lastContentBlock the {@link LastContentBlock} to send
     * @return the future of this operation
     * @throws IllegalStateException if this method is called at the wrong time
     */
    public Future sendLastContentBlock(LastContentBlock lastContentBlock) throws IllegalStateException {
        if (serviceResponseState() == STATE_RESPONSE_WITHOUT_BODY_SENT) {
            setServiceResponseState(STATE_LAST_CONTENT_BLOCK_SENT);
            return sendLastContentBlockImpl(lastContentBlock);
        } else
            throw new IllegalStateException("a response without body must be sent before the last content block");
    }

    public abstract Future sendLastContentBlockImpl(LastContentBlock lastContentBlock);

    /**
     * Reads data in chunks from a {@link BodyInput} and sends them to the client through the connection.
     * This method must be called after {@link #sendResponseWithoutBody(ResponseWithoutBody)}.
     * It cannot be called together with {@link #sendContentBlock(ContentBlock)} or {@link #sendLastContentBlock(LastContentBlock)} in a single service.
     *
     * @param bodyInput the {@link BodyInput} to send
     * @return the future of this operation
     * @throws IllegalStateException if this method is called at the wrong time
     */
    public Future sendBodyInput(BodyInput bodyInput) throws IllegalStateException {
        byte serviceResponseState = serviceResponseState();
        if (serviceResponseState == STATE_RESPONSE_WITHOUT_BODY_SENT) {
            setServiceResponseState(STATE_LAST_CONTENT_BLOCK_SENT);
            return sendBodyInputImpl(bodyInput);
        } else if (serviceResponseState == STATE_INITIAL)
            throw new IllegalStateException("a response without body must be sent before the body");
        else
            throw new IllegalStateException("a body must be sent before content");
    }

    public abstract Future sendBodyInputImpl(BodyInput bodyInput);

    /**
     * Sends a {@link FullResponse} to the client through the connection.
     * This method can only be called once with this instance and can not be called before or after any one of
     * {@link #sendResponseWithoutBody(ResponseWithoutBody)}, {@link #sendContentBlock(ContentBlock)}, and {@link #sendLastContentBlock(LastContentBlock)}.
     *
     * @param fullResponse the {@link FullResponse} to send
     * @return the future of this operation
     * @throws IllegalStateException if this method is called at the wrong time
     */
    public Future sendFullResponse(FullResponse fullResponse) throws IllegalStateException {
        if (serviceResponseState() == STATE_INITIAL) {
            setServiceResponseState(STATE_LAST_CONTENT_BLOCK_SENT);
            return sendFullResponseImpl(fullResponse);
        } else
            throw new IllegalStateException("this full response cannot be sent after other content");
    }

    public Future forceSendFullResponse(FullResponse fullResponse) {
        return sendFullResponseImpl(fullResponse);
    }

    public abstract Future sendFullResponseImpl(FullResponse fullResponse);
}
