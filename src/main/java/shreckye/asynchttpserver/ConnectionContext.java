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

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import shreckye.asynchttpserver.codec.*;

import java.net.SocketAddress;

/**
 * A default {@link AbstractConnectionContext} that wraps a {@link ConnectionHandler} and a Netty {@link ChannelHandlerContext}.
 *
 * @author Yongshun Ye
 */
public class ConnectionContext extends AbstractConnectionContext<ChannelFuture> {
    ConnectionHandler connectionHandler;
    ChannelHandlerContext nettyChannelHandlerContext;

    public ConnectionContext(ConnectionHandler connectionHandler, ChannelHandlerContext nettyChannelHandlerContext) {
        this.connectionHandler = connectionHandler;
        this.nettyChannelHandlerContext = nettyChannelHandlerContext;
    }

    public ConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    public ChannelHandlerContext getNettyChannelHandlerContext() {
        return nettyChannelHandlerContext;
    }

    @Override
    public byte serviceResponseState() {
        return connectionHandler.serviceResponseState();
    }

    @Override
    public void setServiceResponseState(byte serviceResponseState) {
        connectionHandler.setServiceResponseState(serviceResponseState);
    }

    @Override
    public ChannelFuture closeAsync() {
        return nettyChannelHandlerContext.close();
    }

    @Override
    public SocketAddress remoteAddress() {
        return nettyChannelHandlerContext.channel().remoteAddress();
    }

    /* TODO: use the pooled allocator to allocate byte buffers
    public ByteBufAllocator alloc() {
        return nettyChannelHandlerContext.alloc();
    }*/

    @Override
    public ChannelFuture sendResponseWithoutBodyImpl(ResponseWithoutBody responseWithoutBody) {
        return nettyChannelHandlerContext.writeAndFlush(responseWithoutBody.toNettyHttpResponse());
    }

    @Override
    public ChannelFuture sendContentBlockImpl(ContentBlock contentBlock) {
        return nettyChannelHandlerContext.writeAndFlush(contentBlock.toNettyHttpContent());
    }

    @Override
    public ChannelFuture sendLastContentBlockImpl(LastContentBlock lastContentBlock) {
        return nettyChannelHandlerContext.writeAndFlush(lastContentBlock.toNettyLastHttpContent());
    }

    @Override
    public ChannelFuture sendBodyInputImpl(BodyInput bodyInput) {
        return nettyChannelHandlerContext.writeAndFlush(bodyInput.toNettyHttpChunkedInput());
    }

    @Override
    public ChannelFuture sendFullResponseImpl(FullResponse fullResponse) {
        return nettyChannelHandlerContext.writeAndFlush(fullResponse.toNettyFullHttpResponse());
    }

    @Override
    public String toString() {
        return "ConnectionContext{" +
                "nettyChannelHandlerContext=" + nettyChannelHandlerContext +
                '}';
    }
}
