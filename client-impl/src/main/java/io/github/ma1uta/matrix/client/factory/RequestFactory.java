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

package io.github.ma1uta.matrix.client.factory;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.event.content.EventContent;

import java.net.HttpURLConnection;
import java.util.concurrent.CompletableFuture;
import javax.ws.rs.core.GenericType;

/**
 * Factory to invoke API.
 */
public interface RequestFactory {

    /**
     * Status code when request was rate-limited.
     */
    int RATE_LIMITED = 429;

    /**
     * Status code when request was finished with success.
     */
    int SUCCESS = HttpURLConnection.HTTP_OK;

    /**
     * Status code when homeserver requires additional authentication information.
     */
    int UNAUTHORIZED = HttpURLConnection.HTTP_UNAUTHORIZED;

    /**
     * When an one request finish with rate-limited response the next request will be send
     * after {@code delay * DELAY_FACTOR} milliseconds.
     */
    long DELAY_FACTOR = 2;

    /**
     * Maximum timeout when client will stop send request.
     */
    long MAX_DELAY = 5 * 60 * 1000L;

    /**
     * Get the homeserver.
     *
     * @return The homeserver.
     */
    String getHomeserverUrl();

    /**
     * Send the POST request.
     *
     * @param apiClass      The target API.
     * @param apiMethod     The concrete API method.
     * @param params        The request params (query, path and header).
     * @param payload       The request body.
     * @param responseClass The class instance of the response body.
     * @param <T>           The class of the request body.
     * @param <R>           The class of the response body.
     * @return the {@link CompletableFuture} instance to make async request.
     */
    <T, R> CompletableFuture<R> post(Class<?> apiClass, String apiMethod, RequestParams params, T payload, Class<R> responseClass);

    /**
     * Send the POST request.
     *
     * @param apiClass      The target API.
     * @param apiMethod     The concrete API method.
     * @param params        The request params (query, path and header).
     * @param payload       The request body.
     * @param responseClass The class instance of the response body.
     * @param <T>           The class of the request body.
     * @param <R>           The class of the response body.
     * @param requestType   The 'Content-Type' of the request.
     * @return the {@link CompletableFuture} instance to make async request.
     */
    <T, R> CompletableFuture<R> post(Class<?> apiClass, String apiMethod, RequestParams params, T payload, Class<R> responseClass,
                                     String requestType);

    /**
     * Send the GET request.
     *
     * @param apiClass    The target API.
     * @param apiMethod   The concrete API method.
     * @param params      The request params (query, path and header).
     * @param genericType The generic type of the response body.
     * @param <R>         The class of the response body.
     * @return the {@link CompletableFuture} instance to make async request.
     */
    <R> CompletableFuture<R> get(Class<?> apiClass, String apiMethod, RequestParams params, GenericType<R> genericType);

    /**
     * Send the GET request.
     *
     * @param apiClass      The target API.
     * @param apiMethod     The concrete API method.
     * @param params        The request params (query, path and header).
     * @param responseClass The class instance of the response body.
     * @param <R>           The class of the response body.
     * @return the {@link CompletableFuture} instance to make async request.
     */
    <R> CompletableFuture<R> get(Class<?> apiClass, String apiMethod, RequestParams params, Class<R> responseClass);

    /**
     * Send the PUT request.
     *
     * @param apiClass      The target API.
     * @param apiMethod     The concrete API method.
     * @param params        The request params (query, path and header).
     * @param payload       the request body.
     * @param responseClass The class instance of the response body.
     * @param <T>           The class of the request body.
     * @param <R>           The class of the response body.
     * @return the {@link CompletableFuture} instance to make async request.
     */
    <T, R> CompletableFuture<R> put(Class<?> apiClass, String apiMethod, RequestParams params, T payload, Class<R> responseClass);

    /**
     * Send the DELETE request.
     *
     * @param apiClass  The target API.
     * @param apiMethod The concrete API method.
     * @param params    The request params (query, path and header).
     * @return the {@link CompletableFuture} instance to make async request.
     */
    CompletableFuture<EmptyResponse> delete(Class<?> apiClass, String apiMethod, RequestParams params);

    /**
     * Deserialize an event content by type.
     *
     * @param content   The event content in serialized state.
     * @param eventType The event content type.
     * @return The deserialized event content.
     */
    EventContent deserialize(byte[] content, String eventType);
}


