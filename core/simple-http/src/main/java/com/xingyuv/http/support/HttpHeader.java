/*
 * Copyright (c) 2019-2029, xkcoding & Yangkai.Shen & 沈扬凯 (237497819@qq.com & xkcoding.com).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xingyuv.http.support;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 请求头封装
 * </p>
 *
 * @author yangkai.shen
 * @date Created in 2019/12/24 18:24
 */
public class HttpHeader {
    private final Map<String, String> headers;

    public HttpHeader() {
        this.headers = new HashMap<>(16);
    }

    public HttpHeader(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeader add(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public HttpHeader addAll(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }
}
