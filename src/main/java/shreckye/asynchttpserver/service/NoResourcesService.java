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

/**
 * A {@link Service} with no system resources to initialize or release.
 *
 * @author Yongshun Ye
 */
public interface NoResourcesService extends Service {
    /**
     * Do nothing since there are no resources to initialize.
     */
    @Override
    default void init() {
    }

    /**
     * Do nothing since there are no resources to release.
     */
    @Override
    default void release() {
    }
}
