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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.stream.ChunkedWriteHandler;
import shreckye.asynchttpserver.codec.*;
import shreckye.asynchttpserver.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * An asynchronous HTTP server that you can build with various properties and services.
 * This is the central class of this library.
 * <p>
 * This server is registered with Services to serve different request. The server will find service matches in the following order:
 * 1. ones registered to URIs
 * 2. ones registered to URI paths
 * 3. ones registered to URI directory paths
 * 4. ones registered to URI path regular expressions
 * 5. the default one
 * and choose the first match to serve the request.
 *
 * @author Yongshun Ye
 */
public class AsyncHttpServer implements AutoCloseable {
    public final static int DEFAULT_PORT = 80;
    public final static int DEFAULT_MAX_FULL_MESSAGE_LENGTH = 65536;

    NioEventLoopGroup bossGroup, workerGroup;
    Channel serverChannel;

    private AsyncHttpServer(Integer port,
                            Integer nThreads,
                            Integer maxFullContentLength,
                            ConnectionHandlerFactory handlerFactory,
                            HashMap<String, ServiceFactory> uriServices,
                            HashMap<String, ServiceFactory> uriPathServices,
                            TreeMap<String, ServiceFactory> uriDirectoryPathServices,
                            ArrayList<PatternServiceFactoryEntry> urlRegexServices,
                            ServiceFactory defaultService) {
        port = port == null ? DEFAULT_PORT : port;
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = nThreads == null ? new NioEventLoopGroup() : new NioEventLoopGroup(nThreads);
        int maxLength = maxFullContentLength == null ? DEFAULT_MAX_FULL_MESSAGE_LENGTH : maxFullContentLength;
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ConnectionHandlerFactory finalHandlerFactory = handlerFactory == null ? DefaultServiceConnectionHandler::new : handlerFactory;
        ServiceFactory finalDefaultService = defaultService == null ? new DefaultNotFoundService() : defaultService;
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    ConnectionHandler handler = finalHandlerFactory.createConnectionHandler();
                    Service currentService;
                    FullResponse fullResponse;

                    private void resetService(ConnectionContext connectionContext) {
                        if (handler.serviceResponseState() != ConnectionHandler.STATE_LAST_CONTENT_BLOCK_SENT)
                            handler.onServiceThrowable(connectionContext, currentService, new Exception("response data hasn't been not completely sent"));

                        handler.setServiceResponseState(ConnectionHandler.STATE_INITIAL);
                        currentService = null;
                        fullResponse = null;
                    }

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline channelPipeline = ch.pipeline();
                        channelPipeline.addLast(new HttpServerCodec())
                                .addLast(new ChunkedWriteHandler())
                                .addLast(new ChannelOutboundHandlerAdapter() {
                                    @Override
                                    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                                        super.write(ctx, msg, promise);

                                        System.out.println("Written: " + ctx + ", " + msg);
                                    }

                                    @Override
                                    public void flush(ChannelHandlerContext ctx) throws Exception {
                                        super.flush(ctx);

                                        System.out.println("Flushed: " + ctx);
                                    }
                                })
                                .addLast(new SimpleChannelInboundHandler<HttpObject>() {

                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        handler.onConnected(new ConnectionContext(handler, ctx));
                                    }

                                    @Override
                                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                                        handler.onDisconnected(new ConnectionContext(handler, ctx));
                                    }

                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        handler.onConnectionThrowable(new ConnectionContext(handler, ctx), cause);
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
                                        HttpResponse httpResponse = null;

                                        ConnectionContext connectionContext = new ConnectionContext(handler, ctx);
                                        try {
                                            if (msg instanceof HttpRequest) {
                                                HttpRequest httpRequest = (HttpRequest) msg;
                                                DefaultInboundRequestWithoutBody requestWithoutBody = DefaultInboundRequestWithoutBody.fromNettyHttpRequest(httpRequest);

                                                ServiceFactory serviceFactory;
                                                if ((serviceFactory = uriServices.get(requestWithoutBody.uri())) == null) {

                                                    String path = requestWithoutBody.path();
                                                    if ((serviceFactory = uriPathServices.get(path)) == null) {

                                                        Map.Entry<String, ServiceFactory> floorEntry = uriDirectoryPathServices.floorEntry(path);
                                                        if (floorEntry != null && path.startsWith(floorEntry.getKey()))
                                                            serviceFactory = floorEntry.getValue();

                                                        else {
                                                            for (PatternServiceFactoryEntry entry : urlRegexServices)
                                                                if (entry.pattern.matcher(path).matches()) {
                                                                    serviceFactory = entry.serviceFactory;
                                                                    break;
                                                                }

                                                            if (serviceFactory == null)
                                                                serviceFactory = finalDefaultService;
                                                        }
                                                    }
                                                }
                                                currentService = serviceFactory.createService();
                                                handler.onServiceCreated(connectionContext, requestWithoutBody, currentService);

                                                if (currentService instanceof FullResponseService) {
                                                    FullResponseService fullResponseService = (FullResponseService) currentService;
                                                    fullResponse = fullResponseService.onCreateFullResponse();
                                                    fullResponseService.onServeRequestWithoutBody(DefaultInboundRequestWithoutBody.fromNettyHttpRequest(httpRequest), fullResponse);
                                                } else if (currentService instanceof GeneralService)
                                                    ((GeneralService) currentService).onServeRequestWithoutBody(DefaultInboundRequestWithoutBody.fromNettyHttpRequest(httpRequest), connectionContext);
                                                else if (currentService instanceof LightweightService || currentService instanceof FullRequestService)
                                                    ctx.fireChannelRead(httpRequest);
                                                else
                                                    throw new Exception("invalid service");
                                            } else if (msg instanceof HttpContent) {
                                                if (msg instanceof LastHttpContent) {
                                                    LastHttpContent lastHttpContent = (LastHttpContent) msg;
                                                    if (currentService instanceof FullResponseService) {
                                                        ((FullResponseService) currentService).onServeLastContentBlock(DefaultLastContentBlock.fromNettyLastHttpContent(lastHttpContent), fullResponse);
                                                        connectionContext.sendFullResponse(fullResponse);
                                                        resetService(connectionContext);
                                                    } else if (currentService instanceof GeneralService) {
                                                        ((GeneralService) currentService).onServeLastContentBlock(DefaultLastContentBlock.fromNettyLastHttpContent(lastHttpContent), connectionContext);
                                                        resetService(connectionContext);
                                                    } else if (currentService instanceof LightweightService || currentService instanceof FullRequestService)
                                                        ctx.fireChannelRead(lastHttpContent);
                                                } else {
                                                    HttpContent httpContent = (HttpContent) msg;
                                                    if (currentService instanceof FullResponseService)
                                                        ((FullResponseService) currentService).onServeContentBlock(DefaultContentBlock.fromNettyHttpContent(httpContent), fullResponse);
                                                    else if (currentService instanceof GeneralService)
                                                        ((GeneralService) currentService).onServeContentBlock(DefaultContentBlock.fromNettyHttpContent(httpContent), connectionContext);
                                                    else if (currentService instanceof LightweightService || currentService instanceof FullRequestService)
                                                        ctx.fireChannelRead(httpContent);
                                                }
                                            } else
                                                handler.onConnectionThrowable(connectionContext, new Exception("an unknown object received"));
                                        } catch (Throwable t) {
                                            handler.onServiceThrowable(connectionContext, currentService, t);
                                        }
                                    }
                                })
                                .addLast(new HttpObjectAggregator(maxLength))
                                .addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                    @Override
                                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                        handler.onConnectionThrowable(new ConnectionContext(handler, ctx), cause);
                                    }

                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                                        ConnectionContext connectionContext = new ConnectionContext(handler, ctx);
                                        try {
                                            if (currentService instanceof LightweightService)
                                                connectionContext.sendFullResponse(((LightweightService) currentService).onServeFullRequest(DefaultInboundFullRequest.fromNettyFullHttpResponse(msg)));
                                            else if (currentService instanceof FullRequestService)
                                                ((FullRequestService) currentService).onServeFullRequest(DefaultInboundFullRequest.fromNettyFullHttpResponse(msg), connectionContext);
                                        } catch (Throwable t) {
                                            handler.onServiceThrowable(connectionContext, currentService, t);
                                        }
                                        resetService(connectionContext);
                                    }
                                });
                    }
                });

        serverChannel = serverBootstrap.bind(port)
                .channel();
    }

    /**
     * Closes the server. This method blocks until the server is closed.
     *
     * @throws InterruptedException if the synchronization is interrupted
     */
    @Override
    public void close() throws InterruptedException {
        serverChannel.close().sync();
    }

    /**
     * Closes the server asynchronously.
     *
     * @return the future of this operation
     */
    public ChannelFuture closeAsync() {
        return serverChannel.close();
    }

    /**
     * The builder class to build and start an {@link AsyncHttpServer}.
     */
    public static class Builder {
        Integer port = null;
        Integer nThreads = null;
        Integer maxFullMessageLength = null;
        ConnectionHandlerFactory handlerFactory = null;
        HashMap<String, ServiceFactory> uriServices = new HashMap<>();
        HashMap<String, ServiceFactory> uriPathServices = new HashMap<>();
        TreeMap<String, ServiceFactory> uriDirectoryPathServices = new TreeMap<>();
        ArrayList<PatternServiceFactoryEntry> urlRegexServices = new ArrayList<>();
        ServiceFactory defaultService = null;


        /**
         * Creates a new {@link Builder} instance.
         */
        public Builder() {
        }

        /**
         * Builds the {@link AsyncHttpServer} instance and starts it.
         *
         * @return the {@link AsyncHttpServer} instance
         */
        public AsyncHttpServer buildAndStart() {
            return new AsyncHttpServer(port, nThreads, maxFullMessageLength, handlerFactory, uriServices, uriPathServices, uriDirectoryPathServices, urlRegexServices, defaultService);
        }

        /**
         * Specifies the port that the server binds to.
         *
         * @param port the port that the server binds to
         * @return this {@link Builder}
         */
        public Builder port(int port) {
            this.port = port;
            return this;
        }

        /**
         * Specifies the number of threads of the NIO thread pool.
         *
         * @param nThreads the number of threads of the NIO thread pool
         * @return this {@link Builder}
         */
        public Builder nThreads(int nThreads) {
            this.nThreads = nThreads;
            return this;
        }

        /**
         * Specifies the maximum value of the length that a {@link FullRequest} can be.
         * Memory issues may occur if this value is too big.
         *
         * @param maxFullRequestLength the maximum value of the length that a {@link FullRequest} can be
         * @return this {@link Builder}
         */
        public Builder maxFullMessageLength(int maxFullRequestLength) {
            this.maxFullMessageLength = maxFullRequestLength;
            return this;
        }

        /**
         * Specifies the {@link ConnectionHandlerFactory} that generates {@link ConnectionHandler}s for this server.
         *
         * @param handlerFactory the {@link ConnectionHandlerFactory}
         * @return this {@link Builder}
         */
        public Builder hanlderFactory(ConnectionHandlerFactory handlerFactory) {
            this.handlerFactory = handlerFactory;
            return this;
        }

        /**
         * Registers a {@link ServiceFactory} to serve all requests to a certain URI.
         *
         * @param uri            the URI
         * @param serviceFactory the {@link ServiceFactory} to register
         * @return this {@link Builder}
         * @throws IllegalArgumentException if the URI conflicts with an existing one
         */
        public Builder registerUri(String uri, ServiceFactory serviceFactory) throws IllegalArgumentException {
            if (uriServices.putIfAbsent(uri, serviceFactory) != null)
                throw new IllegalArgumentException("the URI conflicts with an existing one");
            return this;
        }

        /**
         * Registers a {@link ServiceFactory} to serve all requests to a URIs with a certain path.
         *
         * @param path           the path
         * @param serviceFactory the {@link ServiceFactory} to register
         * @return this {@link Builder}
         * @throws IllegalArgumentException if the path conflicts with an existing one
         */
        public Builder registerUriPath(String path, ServiceFactory serviceFactory) throws IllegalArgumentException {
            if (uriPathServices.putIfAbsent(path, serviceFactory) != null)
                throw new IllegalArgumentException("the URI path conflicts with an existing one");
            return this;
        }

        /**
         * Registers a {@link ServiceFactory} to serve all requests to URIs with a certain directory path prefix.
         *
         * @param directoryPath  the directory path
         * @param serviceFactory the {@link ServiceFactory} to register
         * @return this {@link Builder}
         * @throws IllegalArgumentException if the path conflicts with an existing one
         */
        public Builder registerUriDirectoryPath(String directoryPath, ServiceFactory serviceFactory) throws IllegalArgumentException {
            if (uriDirectoryPathServices.putIfAbsent(directoryPath, serviceFactory) != null)
                throw new IllegalArgumentException("the URI directory path conflicts with an existing one");
            return this;
        }

        /**
         * Registers a {@link ServiceFactory} to serve all requests to URIs that match a regular expression.
         *
         * @param regex          the regular expression string
         * @param serviceFactory the {@link ServiceFactory} to register
         * @return this {@link Builder}
         * @throws PatternSyntaxException
         */
        public Builder registerPathRegex(String regex, ServiceFactory serviceFactory) throws PatternSyntaxException {
            return registerPathRegex(Pattern.compile(regex), serviceFactory);
        }

        /**
         * Registers a {@link ServiceFactory} to serve all requests to URIs that match a regular expression.
         *
         * @param pattern        the regular expression {@link Pattern}
         * @param serviceFactory the {@link ServiceFactory} to register
         * @return this {@link Builder}
         * @throws PatternSyntaxException
         */
        public Builder registerPathRegex(Pattern pattern, ServiceFactory serviceFactory) {
            urlRegexServices.add(new PatternServiceFactoryEntry(pattern, serviceFactory));
            return this;
        }

        /**
         * Registers a default {@link ServiceFactory} to serve all requests that couldn't find a match.
         *
         * @param serviceFactory the {@link ServiceFactory} to register
         * @return this {@link Builder}
         */
        public Builder registerDefault(ServiceFactory serviceFactory) {
            defaultService = serviceFactory;
            return this;
        }
    }

    private static class PatternServiceFactoryEntry {
        Pattern pattern;
        ServiceFactory serviceFactory;

        private PatternServiceFactoryEntry(Pattern pattern, ServiceFactory serviceFactory) {
            this.pattern = pattern;
            this.serviceFactory = serviceFactory;
        }
    }
}
