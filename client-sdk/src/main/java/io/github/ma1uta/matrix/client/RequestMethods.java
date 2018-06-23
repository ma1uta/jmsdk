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

import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.jeon.exception.RateLimitedException;
import io.github.ma1uta.matrix.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

/**
 * Helper to construst requests.
 */
public class RequestMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMethods.class);

    /**
     * Status code when request was rate-limited.
     */
    public static final int RATE_LIMITED = 429;

    /**
     * Status code when request was finished with success.
     */
    public static final int SUCCESS = 200;

    /**
     * Initial waiting timeout when rate-limited response is occurred.
     */
    public static final long INITIAL_TIMEOUT = 5 * 1000L;

    /**
     * When an one request finish with rate-limited response the next request will be send
     * after {@code timeout * TIMEOUT_FACTOR} milliseconds.
     */
    public static final long TIMEOUT_FACTOR = 2;

    /**
     * Maximum timeout when client will stop send request.
     */
    public static final long MAX_TIMEOUT = 5 * 60 * 1000;

    private final Client client;
    private final String homeserverUrl;
    private final boolean addUserIdToRequests;
    private String userId;
    private String accessToken;

    public RequestMethods(Client client, String homeserverUrl, boolean addUserIdToRequests) {
        this.client = client;
        this.homeserverUrl = homeserverUrl;
        this.addUserIdToRequests = addUserIdToRequests;
    }

    public Client getClient() {
        return client;
    }

    public String getHomeserverUrl() {
        return homeserverUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isAddUserIdToRequests() {
        return addUserIdToRequests;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    protected Invocation.Builder prepare(Class<?> apiClass, String apiMethod, Map<String, String> pathParams,
                                         Map<String, String> queryParams, String requestType) {
        UriBuilder builder = UriBuilder.fromResource(apiClass).path(apiClass, apiMethod);
        Map<String, String> encoded = new HashMap<>();
        if (pathParams != null) {
            for (Map.Entry<String, String> entry : pathParams.entrySet()) {
                encoded.put(entry.getKey(), encode(entry.getValue()));
            }
        }
        URI uri = builder.buildFromEncodedMap(encoded);

        WebTarget path;
        path = getClient().target(getHomeserverUrl()).path(uri.toString());
        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                path = path.queryParam(entry.getKey(), entry.getValue());
            }
        }
        if (isAddUserIdToRequests()) {
            path = path.queryParam("user_id", encode(getUserId()));
        }
        Invocation.Builder request = path.request(requestType);
        if (getAccessToken() != null) {
            request = request.header("Authorization", "Bearer " + getAccessToken());
        }
        return request;
    }

    protected String encode(String origin) {
        if (origin == null) {
            String msg = "Empty value";
            LOGGER.error(msg);
            throw new IllegalArgumentException(msg);
        }
        try {
            return URLEncoder.encode(origin, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            String msg = "Unsupported encoding";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    protected <R> R extractResponseModel(Supplier<Response> action, Class<R> responseClass) {
        return extractResponseModel(action, responseClass, INITIAL_TIMEOUT);
    }

    protected <R> R extractResponseModel(Supplier<Response> action, Class<R> responseClass, long timeout) {
        Response response = action.get();
        switch (response.getStatus()) {
            case SUCCESS:
                return response.readEntity(responseClass);
            case RATE_LIMITED:
                try {
                    long newTimeout = timeout * TIMEOUT_FACTOR;
                    if (newTimeout > MAX_TIMEOUT) {
                        throw new RateLimitedException("Cannot send request, maximum timeout was reached.");
                    } else {
                        Thread.sleep(newTimeout);
                        return extractResponseModel(action, responseClass, newTimeout);
                    }
                } catch (InterruptedException e) {
                    LOGGER.error("Interrupted", e);
                    throw new RateLimitedException("Rate-limited request was interrupted", e);
                }
            default:
                ErrorResponse errorResponse = response.readEntity(ErrorResponse.class);
                throw new MatrixException(errorResponse.getErrcode(), errorResponse.getError(), response.getStatus());
        }
    }

    protected <T, R> R post(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams, T payload,
                            Class<R> responseClass) {
        return post(apiClass, apiMethod, pathParams, queryParams, payload, responseClass, MediaType.APPLICATION_JSON);
    }

    protected <T, R> R post(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams, T payload,
                            Class<R> responseClass, String requestType) {
        return extractResponseModel(() -> prepare(apiClass, apiMethod, pathParams, queryParams, requestType).post(Entity.json(payload)),
            responseClass);
    }

    protected <R> R get(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams,
                        Class<R> responseClass) {
        return extractResponseModel(() -> {
            try {
                return prepare(apiClass, apiMethod, pathParams, queryParams, MediaType.APPLICATION_JSON).async().get().get();
            } catch (InterruptedException | ExecutionException e) {
                String msg = "Failed invoke get request";
                LOGGER.error(msg, e);
                throw new RuntimeException(msg, e);
            }
        }, responseClass);
    }

    protected <T, R> R put(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams, T payload,
                           Class<R> responseClass) {
        return extractResponseModel(
            () -> prepare(apiClass, apiMethod, pathParams, queryParams, MediaType.APPLICATION_JSON).put(Entity.json(payload)),
            responseClass);
    }

    protected <T, R> R delete(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams,
                              Class<R> responseClass) {
        return extractResponseModel(() -> prepare(apiClass, apiMethod, pathParams, queryParams, MediaType.APPLICATION_JSON).delete(),
            responseClass);
    }
}


