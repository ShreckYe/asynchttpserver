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

import io.netty.handler.codec.http.HttpHeaderNames;

/**
 * HTTP header names in an HTTP response.
 *
 * @author Yongshun Ye
 * @see HttpHeaderNames
 */
public class ResponseHeaderNames {
    public final static String CONNECTION = HttpHeaderNames.CONNECTION.toString(),
            SET_COOKIE = HttpHeaderNames.SET_COOKIE.toString(),
            CONTENT_TYPE = HttpHeaderNames.CONTENT_TYPE.toString(),
            CONTENT_LENGTH = HttpHeaderNames.CONTENT_LENGTH.toString();
}
