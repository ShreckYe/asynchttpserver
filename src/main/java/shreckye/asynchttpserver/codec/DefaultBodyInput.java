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

package shreckye.asynchttpserver.codec;

import io.netty.buffer.ByteBuf;
import io.netty.handler.stream.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public class DefaultBodyInput implements BodyInput {
    ChunkedInput<ByteBuf> contentInput;

    public DefaultBodyInput(ChunkedInput<ByteBuf> contentInput) {
        this.contentInput = contentInput;
    }

    public static DefaultBodyInput newInputStreamInstance(InputStream inputStream) throws IOException {
        return new DefaultBodyInput(new ChunkedStream(inputStream));
    }

    public static DefaultBodyInput newChannelInstance(ReadableByteChannel channel) {
        return new DefaultBodyInput(new ChunkedNioStream(channel));
    }

    public static DefaultBodyInput newBioFileInstance(File file) throws IOException {
        return new DefaultBodyInput(new ChunkedFile(file));
    }

    public static DefaultBodyInput newNioFileInstance(File file) throws IOException {
        return new DefaultBodyInput(new ChunkedNioFile(file));
    }

    @Override
    public ChunkedInput<ByteBuf> contentInput() {
        return contentInput;
    }

    @Override
    public void close() throws Exception {
        contentInput.close();
    }
}
