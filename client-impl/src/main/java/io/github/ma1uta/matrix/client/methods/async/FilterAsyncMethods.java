/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

package io.github.ma1uta.matrix.client.methods.async;

import io.github.ma1uta.matrix.client.ConnectionInfo;
import io.github.ma1uta.matrix.client.model.filter.FilterData;
import io.github.ma1uta.matrix.client.model.filter.FilterResponse;
import io.github.ma1uta.matrix.client.rest.async.FilterApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Filter methods.
 */
public class FilterAsyncMethods {

    private final FilterApi filterApi;

    private final ConnectionInfo connectionInfo;

    public FilterAsyncMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.filterApi = restClientBuilder.build(FilterApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * Upload new filter.
     *
     * @param filter An new filter.
     * @return The filter id.
     */
    public CompletableFuture<FilterResponse> uploadFilter(FilterData filter) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        return filterApi.uploadFilter(userId, filter).toCompletableFuture();
    }

    /**
     * Get specified filter.
     *
     * @param filterId The filter id.
     * @return The filter data.
     */
    public CompletableFuture<FilterData> getFilter(String filterId) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(filterId, "FilterId cannot be empty.");

        return filterApi.getFilter(userId, filterId).toCompletableFuture();
    }
}
