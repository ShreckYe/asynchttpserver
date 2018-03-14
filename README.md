# asynchttpserver
A simple and lightweight asynchronous HTTP server library based on Netty for Java


Don't know how to start with building a simple HTTP application server? Tired of using old-fashioned JSP or Servlet for implementation of very lightweight HTTP services? Maybe you should try this library. It's simple and fast.

This library allows Java applications to easily receive HTTP requests, process them, and send back HTTP responses asynchronously. It's built on top of [Netty](https://netty.io/) and supports Java 8 and above.
## Features
1. Direct integration into your Java project.
1. High performance with the power of [Netty](https://netty.io/) and Java NIO.
2. Very lightweight with almost zero overhead.
3. Suitable for building simple application services, user content services, and public API services.
## Guide
### HTTP Objects
There are different kinds of HTTP objects you will receive or send when building an HTTP server. These HTTP object classed and there corresponding functions are listed below:
1. `FullRequest`:
2. `FullResponse`: 
3. `RequestWithoutBody`:
4. `ResponseWithoutBody`:
5. `BodyInput`:
6. `ContentBlock`:
7. `LastContentBlock`:
### Service
A `Service` is an instance that that serves an HTTP request and sends back an HTTP response. What it does is similar to a Javax Servlet. It has 2 methods `init()` and `release()` to initialize or release system resources. There are 4 kinds of `Service`s that you can register to the server:
1. `LightweightService`
2. `FullRequestService`
3. `FullResponseService`
4. `DefaultService`
To build your own `Service`, you must extend one of the 4 classes listed above and implement its `onServe` method(s). Because a `Service` only serves one request, the member variables you declare in a `Service` instance is only valid during serving that request.

In order to build a server that can serve many requests from many clients, a `ServiceFactory` is needed to generate `Service`s for each incoming request. Just implement `createService()` to build a `ServiceFactory`. To simplify this, you can do it with lambda
```java
() -> new ImplementedService(params)
```
or method inference
```java
ImplementedService::new
```.

There are 2 `Service` interfaces to simplify your job. Implement `SingletonService` if your `Service` doesn't have any member variables and one instance can be used for all requests in all connections. It is actually a `ServiceFactory` that returns itself in `createService()`. If your `Service` doesn't hold any system resources that can leak and therefore `init()` and `release()` are empty, you can implement `NoResourcesService` to save some code.

Some partially or fully implemented `Service`s are there in the same package as examples. You can alse extend them to build your own `Service`s.
### Build the Server
`AsyncHttpServer` is the actual class that interacts with the Netty framework and hold the `Services`. You need an `AsyncHttpServer.Builder` to build and start an `AsyncHttpServer` instance. With the builder, you can register `Service`s to listen for a URI, a URI path, a URI path directory, or a regular expression that matches the URI. If none of these match, a default one will be used.
## Samples

## License
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
