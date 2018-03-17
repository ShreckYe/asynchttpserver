# asynchttpserver
A simple and lightweight asynchronous HTTP server library based on Netty for Java


Don't know how to start with building a simple HTTP application server? Tired of using old-fashioned JSP or Servlet for implementation of very lightweight HTTP services? Maybe you should try this library. It's simple and fast.

This library allows Java applications to easily receive HTTP requests, process them, and send back HTTP responses asynchronously. It's built on top of [Netty](https://netty.io/) and supports Java 8 and above.
## Features
1. Direct integration into your Java project. No Java EE framework required.
1. High performance with the power of [Netty](https://netty.io/) and Java NIO.
2. Very lightweight with almost zero overhead.
3. Suitable for building simple application services, user content services, and public API services.
## Install
[Download JAR](/build/libs/asynchttpserver-1.0-alpha-20180315.jar)

Gradle identifier (available yet, but coming soon):
```
shreckye.asynchttpserver:asynchttpserver:1.0-alpha-20180315
```
## Guide
### HTTP Objects
There are different kinds of HTTP objects you will receive or send when building an HTTP server. These HTTP object classes and their corresponding functions are listed below:
1. `FullRequest`: a complete HTTP request with its request line, header fields, and body.
2. `FullResponse`: a complete HTTP response with its status line, header fields, and body.
3. `RequestWithoutBody`: representing the request line and the header fields of an HTTP response.
4. `ResponseWithoutBody`: representing the status line and the header fields of an HTTP response.
5. `BodyInput`: an HTTP body as a stream or channel input for either an HTTP request or an HTTP response.
6. `ContentBlock`: a block of data in either an HTTP request or an HTTP response's content.
7. `LastContentBlock`: the last block of data in either an HTTP request or an HTTP response's content.
### Service
A `Service` is an instance that that serves an HTTP request and sends back an HTTP response, much similar to a Javax `Servlet`. It has 2 methods `init()` and `release()` to initialize or release system resources. There are 4 kinds of `Service`s that you can register to the server:
1. `LightweightService`: the simplest kind of `Service` that serves after a full HTTP request is received and sends back a full HTTP response.

Methods to implement:
```java
public abstract FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception;
```
2. `FullRequestService`: the kind of `Service` that serves after a full HTTP request is received.

Methods to implement:
```java
public abstract void onServeFullRequest(FullRequest fullRequest, ConnectionContext connectionContext) throws Exception;
```
3. `FullResponseService`: the kind of `Service` that serves an HTTP request without its body, the content blocks, and the last content block in sequence, and builds and sends back a full HTTP response.

This type of `Service` is recommended for serving HTTP requests with large content so they don't eat up the memory.

Methods to implement:
```java
public abstract FullResponseImpl onCreateFullResponse();
public abstract void onServeRequestWithoutBody(RequestWithoutBody requestWithoutBody, FullResponseImpl fullResponse) throws Exception;
public abstract void onServeContentBlock(ContentBlock contentBlock, FullResponseImpl fullResponse) throws Exception;
public abstract void onServeLastContentBlock(LastContentBlock lastContentBlock, FullResponseImpl fullResponse) throws Exception;
```
4. `GeneralService`: The kind of `Service` that serves an HTTP request without its body, the content blocks, and the last content block in sequence.

This type of `Service` is recommended for serving HTTP requests with large content and sending back HTTP responses with large content so they don't eat up the memory.

Methods to implement:
```java
public abstract void onServeRequestWithoutBody(RequestWithoutBody requestWithoutBody, ConnectionContext connectionContext) throws Exception;
public abstract void onServeContentBlock(ContentBlock contentBlock, ConnectionContext connectionContext) throws Exception;
public abstract void onServeLastContentBlock(LastContentBlock lastContentBlock, ConnectionContext connectionContext) throws Exception;
```

To build your own `Service`, you must extend one of the 4 classes listed above and implement its `onServe` method(s). Because a `Service` only serves one request, the member variables you declare in a `Service` instance is only valid during serving that request.

In order to build a server that can serve many requests from many clients, a `ServiceFactory` is needed to generate `Service`s for each incoming request. Just implement `createService()` to build a `ServiceFactory`. To simplify this, you can do it with lambda
```java
() -> new ImplementedService(params)
```
or method inference
```java
ImplementedService::new
```
.

There are 2 `Service` interfaces to simplify your job. Implement `SingletonService` if your `Service` doesn't have any member variables and one instance can be used for all requests in all connections. A `SingletonService` is actually a `ServiceFactory` that returns itself in `createService()`, so an instance can be registered directly. If your `Service` doesn't hold any system resources that can leak and therefore `init()` and `release()` are empty, you can implement `NoResourcesService` to save some code.

Some partially or fully implemented `Service`s are there in the same package as examples. You can also use or extend them to build your own `Service`s.
### Build and Start the Server
`AsyncHttpServer` is the actual class that interacts with the Netty framework and hold the `Services`. You need an `AsyncHttpServer.Builder` to build and start an `AsyncHttpServer` instance. With the builder, you can register `Service`s to listen for a URI, a URI path, a URI path directory, or a regular expression that matches the URI. If none of these match, a default one will be used.
## Examples
Here is a complete example in a single Java file that starts a server.

`ExampleServer.java`:
```java
package shreckye.asynchttpserver.example;

import io.netty.handler.codec.http.HttpResponseStatus;
import shreckye.asynchttpserver.AsyncHttpServer;
import shreckye.asynchttpserver.codec.DefaultOutboundFullResponse;
import shreckye.asynchttpserver.codec.FullRequest;
import shreckye.asynchttpserver.codec.FullResponse;
import shreckye.asynchttpserver.service.*;

public class ExampleServer {
    public static void main(String[] args) {
        new AsyncHttpServer.Builder()
                .registerUri("/hello_world.html?param=special", new HelloWorldWithSpecialParamService())
                .registerUriPath("/hello_world.html", new HelloWorldService())
                .registerUriDirectoryPath("/files/", () -> new DirectoryPathFileService("/files/", "/home/username/files/"))
                .registerPathRegex("\\/\\w*bingo\\w*", new BingoService())
                .registerDefault(new NotFoundService())
                .buildAndStart();
    }

    public static class HelloWorldWithSpecialParamService extends HelloWorldService {
        @Override
        public FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception {
            return DefaultOutboundFullResponse.newHtmlInstance(HttpResponseStatus.OK,
                    "<html><head><title>Hello world!</title></head><body><h1>Hello world with a special param!</h1></body></html>");
        }
    }

    public static class HelloWorldService extends LightweightService implements NoResourcesService, SingletonService {
        @Override
        public FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception {
            return DefaultOutboundFullResponse.newHtmlInstance(HttpResponseStatus.OK,
                    "<html><head><title>Hello world!</title></head><body><h1>Hello world!</h1></body></html>");
        }
    }

    public static class BingoService extends LightweightService implements NoResourcesService, SingletonService {
        @Override
        public FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception {
            return DefaultOutboundFullResponse.newHtmlInstance(HttpResponseStatus.OK,
                    "<html><head><title>Bingo!</title></head><body><h1>Bingo! You just found an easter egg.</h1></body></html>");
        }
    }

    public static class NotFoundService extends DefaultNotFoundService {
        @Override
        public FullResponse onServeFullRequest(FullRequest fullRequest) throws Exception {
            return DefaultOutboundFullResponse.newHtmlInstance(HttpResponseStatus.NOT_FOUND,
                    "<html><head><title>404 Not Found</title></head><body><h1>The URI you requested \"" + fullRequest.uri() + "\" is not found.</h1></body></html>");
        }
    }
}

```

## Acknowledgements
Thank Huiqi Xue for his help on computer network knowledge and reviews on this project.
## Apache License
```
   Copyright 2018 Yongshun Ye

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
   ```
