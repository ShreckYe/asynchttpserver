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
import shreckye.asynchttpserver.ConnectionHandler;

import java.io.IOException;

/**
 * An HTTP service that serves an HTTP request and sends back an HTTP response.
 * What this class does is similar to a Javax Servlet.
 * When an exception occurs during a {@link Service}, it will be handled by
 * {@link ConnectionHandler#onServiceThrowable(ConnectionContext, Service, Throwable)}
 * and {@link #release()} will be called.
 *
 * @author Yongshun Ye
 */
public interface Service {
    /**
     * Initialize any resources needed by this service
     *
     * @throws IOException
     */
    void init() throws IOException;

    /**
     * Releases any resources held by this service
     *
     * @throws IOException
     */
    void release() throws IOException;
}
