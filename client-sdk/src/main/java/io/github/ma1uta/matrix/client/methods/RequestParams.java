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

package io.github.ma1uta.matrix.client.methods;

import java.util.HashMap;
import java.util.Map;

/**
 * Aggregator for path, query and header parameters.
 */
public class RequestParams {

    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, String> headerParams = new HashMap<>();

    /**
     * Add a path parameter.
     *
     * @param paramName  name of the parameter.
     * @param paramValue value of the parameter.
     * @return parameters.
     */
    public RequestParams pathParam(String paramName, String paramValue) {
        if (paramValue != null) {
            pathParams.put(paramName, paramValue);
        }
        return this;
    }

    /**
     * Add a query parameter.
     *
     * @param paramName  name of the parameter.
     * @param paramValue value of the parameter.
     * @return parameters.
     */
    public RequestParams queryParam(String paramName, String paramValue) {
        if (paramValue != null) {
            queryParams.put(paramName, paramValue);
        }
        return this;
    }

    /**
     * Add a query parameter.
     *
     * @param paramName  name of the parameter.
     * @param paramValue value of the parameter.
     * @return parameters.
     */
    public RequestParams queryParam(String paramName, Long paramValue) {
        if (paramValue != null) {
            queryParams.put(paramName, Long.toString(paramValue));
        }
        return this;
    }

    /**
     * Add a header parameter.
     *
     * @param paramName  name of the parameter.
     * @param paramValue value of the parameter.
     * @return parameters.
     */
    public RequestParams headerParam(String paramName, String paramValue) {
        if (paramValue != null) {
            headerParams.put(paramName, paramValue);
        }
        return this;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, String> getHeaderParams() {
        return headerParams;
    }
}
