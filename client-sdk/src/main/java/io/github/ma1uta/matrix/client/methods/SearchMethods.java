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

import io.github.ma1uta.matrix.client.api.SearchApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.search.SearchRequest;
import io.github.ma1uta.matrix.client.model.search.SearchResponse;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Search methods.
 */
public class SearchMethods extends AbstractMethods {

    public SearchMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Performs a full text search across different categories.
     *
     * @param request   the search request.
     * @param nextBatch The point to return events from. If given, this should be a next_batch result from a previous call
     *                  to this endpoint.
     * @return Results of the search.
     */
    public CompletableFuture<SearchResponse> search(SearchRequest request, String nextBatch) {
        Objects.requireNonNull(request.getSearchCategories(), "Search categories cannot be empty.");
        RequestParams params = defaults().clone().query("nextBatch", nextBatch);
        return factory().post(SearchApi.class, "search", params, request, SearchResponse.class);
    }
}
