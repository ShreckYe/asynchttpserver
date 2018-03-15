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

public class MediaType {
    public final static String TEXT_HTML = "text/html", TEXT_HTML_UTF8 = TEXT_HTML + "; charset=utf-8",
            APPLICATION_X_WWW_FORM_URLENCODED = "application/x-www-form-urlencoded",
            APPLICATION_JSON = "application/json",
            APPLICATION_OCTET_STREAM = "application/octet-stream";
}
