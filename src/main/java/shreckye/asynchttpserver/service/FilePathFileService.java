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

import shreckye.asynchttpserver.codec.FullRequest;

import java.io.File;

/**
 * Serves a single file.
 */
public class FilePathFileService extends SimpleFileService implements NoResourcesService {
    final File file;

    /**
     * Creates a new instance with the file to serve.
     *
     * @param file the file to serve
     */
    public FilePathFileService(File file) {
        this.file = file;
    }

    public FilePathFileService(String filePath) {
        this(new File(filePath));
    }

    @Override
    public File file(FullRequest fullRequest) {
        return file;
    }
}
