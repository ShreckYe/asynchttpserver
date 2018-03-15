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

import io.netty.handler.codec.http.HttpResponseStatus;
import shreckye.asynchttpserver.ConnectionContext;
import shreckye.asynchttpserver.codec.*;

import java.io.File;

/**
 * A {@link FullRequestService} that serves files.
 */
public abstract class SimpleFileService extends FullRequestService {
    @Override
    public void onServeFullRequest(FullRequest fullRequest, ConnectionContext connectionContext) throws Exception {
        if (!fullRequest.method().equals(Method.GET)) {
            connectionContext.sendFullResponse(new DefaultOutboundFullResponse(HttpResponseStatus.METHOD_NOT_ALLOWED));
            return;
        }

        File file = file(fullRequest);
        if (!file.exists()) {
            connectionContext.sendFullResponse(new DefaultOutboundFullResponse(HttpResponseStatus.NOT_FOUND));
            return;
        }

        connectionContext.sendResponseWithoutBody(DefaultOutboundResponseWithoutBody.newAutoFileInstance(HttpResponseStatus.OK, file));

        BodyInput bodyInput = DefaultBodyInput.newNioFileInstance(file);
        connectionContext.sendBodyInput(bodyInput);
    }

    /**
     * Gets the file from the request information, especially the URI.
     *
     * @param fullRequest the {@link FullRequest}
     * @return the file
     */
    public abstract File file(FullRequest fullRequest);

    /*protected void send(ConnectionContext connectionContext, FileChannel fileChannel, ByteBuffer byteBuffer, int blockSize) throws Exception {
        try {
            int bytesRead = fileChannel.read(byteBuffer);
            if (bytesRead >= blockSize)
                connectionContext.sendContentBlock(new DefaultContentBlock(byteBuffer)).addListener(future ->
                        clearAndSend(connectionContext, fileChannel, byteBuffer, blockSize));
            else
                connectionContext.sendLastContentBlock(new DefaultLastContentBlock(byteBuffer));
        } catch (Exception e) {
            fileChannel.close();
        }
    }

    protected void clearAndSend(ConnectionContext connectionContext, FileChannel fileChannel, ByteBuffer byteBuffer, int blockSize) throws Exception {
        byteBuffer.clear();
        send(connectionContext, fileChannel, byteBuffer, blockSize);
    }*/

}
