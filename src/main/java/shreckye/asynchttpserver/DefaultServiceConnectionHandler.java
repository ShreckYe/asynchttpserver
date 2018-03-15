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

import io.netty.handler.codec.http.HttpResponseStatus;
import shreckye.asynchttpserver.codec.DefaultOutboundFullResponse;
import shreckye.asynchttpserver.codec.RequestWithoutBody;
import shreckye.asynchttpserver.service.Service;

public class DefaultServiceConnectionHandler extends ServiceConnectionHandler {
    @Override
    public void onConnected(ConnectionContext connectionContext) {
        System.out.println("Connected: " + connectionContext);
    }

    @Override
    public void onDisconnected(ConnectionContext connectionContext) {
        System.out.println("Disconnected: " + connectionContext);
    }

    @Override
    public void onServiceCreated(ConnectionContext connectionContext, RequestWithoutBody requestWithoutBody, Service service) {
        System.out.println("Serving request: " + connectionContext + ", " + requestWithoutBody + ", " + service);
    }

    @Override
    public void onConnectionThrowable(ConnectionContext connectionContext, Throwable t) {
        System.err.println("A connection throwable occurred: " + connectionContext + ", " + t);
        t.printStackTrace();
    }

    @Override
    public void onServiceThrowable(ConnectionContext connectionContext, Service service, Throwable t) {
        connectionContext.forceSendFullResponse(new DefaultOutboundFullResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR));
        System.err.println("A service throwable occurred: " + connectionContext + ", " + service + ", " + t);
        t.printStackTrace();
    }
}
