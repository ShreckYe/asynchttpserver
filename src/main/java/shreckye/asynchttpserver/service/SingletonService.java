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
 * A {@link Service} that will be used throughout the whole server lifecycle.
 * It will not be destroyed and will be used to serve multiple request in multiple connections,
 * thus its subclasses should contain no member variables to preserve the serving state.
 *
 * @author Yongshun Ye
 */
public interface SingletonService extends Service, ServiceFactory {
    @Override
    default Service createService() {
        return this;
    }
}
