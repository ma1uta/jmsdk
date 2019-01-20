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

import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.FilterApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.filter.FilterData;
import io.github.ma1uta.matrix.client.model.filter.FilterResponse;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Filter methods.
 */
public class FilterMethods extends AbstractMethods {

    public FilterMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Upload new filter.
     *
     * @param filter An new filter.
     * @return The filter id.
     */
    public CompletableFuture<FilterResponse> uploadFilter(FilterData filter) {
        Objects.requireNonNull(defaults().getUserId(), "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("userId", defaults().getUserId().toString());
        return factory().post(FilterApi.class, "uploadFilter", params, filter, FilterResponse.class);
    }

    /**
     * Get specified filter.
     *
     * @param filterId The filter id.
     * @return The filter data.
     */
    public CompletableFuture<FilterData> getFilter(String filterId) {
        Objects.requireNonNull(defaults().getUserId(), "UserId cannot be empty.");
        Objects.requireNonNull(filterId, "FilterId cannot be empty.");

        RequestParams params = defaults().clone()
            .path("userId", defaults().getUserId().toString())
            .path("filterId", filterId);
        return factory().get(FilterApi.class, "getFilter", params, FilterData.class);
    }
}
