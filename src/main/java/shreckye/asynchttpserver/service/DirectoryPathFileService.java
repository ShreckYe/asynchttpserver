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
 * Serves all files in a directory and its subdirectories.
 *
 * @author Yongshun Ye
 */
public class DirectoryPathFileService extends SimpleFileService implements NoResourcesService {
    final String uriPathRoot, directoryPath;

    /**
     * Creates a new instance with the URI path root to remove and the corresponding file system directory to serve.
     *
     * @param uriPathRoot   the URI path root
     * @param directoryPath the corresponding file system directory path
     */
    public DirectoryPathFileService(String uriPathRoot, String directoryPath) {
        this.uriPathRoot = uriPathRoot.endsWith("/") ? uriPathRoot : uriPathRoot + "/";
        this.directoryPath = directoryPath;
    }

    @Override
    public File file(FullRequest fullRequest) {
        String uri = fullRequest.uri();
        return uri.startsWith(uriPathRoot) ? new File(directoryPath, uri.substring(uriPathRoot.length())) : null;
    }
}
