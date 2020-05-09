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

import io.github.ma1uta.matrix.client.model.search.SearchRequest;
import io.github.ma1uta.matrix.client.model.search.SearchResponse;
import io.github.ma1uta.matrix.client.rest.async.SearchApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Search methods.
 */
public class SearchAsyncMethods {

    private final SearchApi searchApi;

    public SearchAsyncMethods(RestClientBuilder restClientBuilder) {
        this.searchApi = restClientBuilder.build(SearchApi.class);
    }

    /**
     * Performs a full text search across different categories.
     *
     * @param request   The search request.
     * @param nextBatch The point to return events from. If given, this should be a next_batch result from a previous call
     *                  to this endpoint.
     * @return Results of the search.
     */
    public CompletableFuture<SearchResponse> search(SearchRequest request, String nextBatch) {
        Objects.requireNonNull(request.getSearchCategories(), "Search categories cannot be empty.");

        return searchApi.search(nextBatch, request).toCompletableFuture();
    }
}
