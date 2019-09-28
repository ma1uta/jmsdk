/*
 * Copyright sablintolya@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.ma1uta.matrix.client.filter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * Filter to specify custom headers.
 */
@Provider
public class HeaderClientFilter implements ClientRequestFilter {

    private final Map<String, String> headers = new HashMap<>();

    /**
     * Add custom header.
     *
     * @param header header name.
     * @param value  header value.
     */
    public void addHeader(String header, String value) {
        headers.put(header, value);
    }

    /**
     * Remove header.
     *
     * @param header header name.
     */
    public void removeHeader(String header) {
        headers.remove(header);
    }

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        for (Map.Entry<String, String> header : headers.entrySet()) {
            requestContext.getHeaders().putSingle(header.getKey(), header.getValue());
        }
    }
}
