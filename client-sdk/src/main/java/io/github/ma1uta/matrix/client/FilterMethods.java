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

package io.github.ma1uta.matrix.client;

import io.github.ma1uta.matrix.client.api.FilterApi;
import io.github.ma1uta.matrix.client.model.filter.FilterData;
import io.github.ma1uta.matrix.client.model.filter.FilterResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Filter methods.
 */
public class FilterMethods {

    private final MatrixClient matrixClient;

    FilterMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Upload new filter.
     *
     * @param filter new filter.
     * @return filter id.
     */
    public FilterResponse uploadFilter(FilterData filter) {
        Map<String, String> pathParams = new HashMap<>();
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        pathParams.put("userId", requestMethods.getUserId());
        return requestMethods.post(FilterApi.class, "uploadFilter", pathParams, null, filter, FilterResponse.class);
    }

    /**
     * Get specified filter.
     *
     * @param filterId filter id.
     * @return filter.
     */
    public FilterData getFilter(String filterId) {
        Map<String, String> pathParams = new HashMap<>();
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        pathParams.put("userId", requestMethods.getUserId());
        pathParams.put("filterId", filterId);
        return requestMethods.get(FilterApi.class, "getFilter", pathParams, null, FilterData.class);
    }
}
