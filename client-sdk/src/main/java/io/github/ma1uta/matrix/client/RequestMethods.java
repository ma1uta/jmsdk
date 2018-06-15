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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

/**
 * Helper to construst requests.
 */
public class RequestMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMethods.class);

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
        try {
            return URLEncoder.encode(origin, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            String msg = "Unsupported encoding";
            LOGGER.error(msg, e);
            throw new RuntimeException(e);
        }
    }

    protected <T, R> R post(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams, T payload,
                            Class<R> responseClass) {
        return post(apiClass, apiMethod, pathParams, queryParams, payload, responseClass, MediaType.APPLICATION_JSON);
    }

    protected <T, R> R post(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams, T payload,
                            Class<R> responseClass, String requestType) {
        return prepare(apiClass, apiMethod, pathParams, queryParams, requestType).post(Entity.json(payload), responseClass);
    }

    protected <R> R get(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams,
                        Class<R> responseClass) {
        try {
            return prepare(apiClass, apiMethod, pathParams, queryParams, MediaType.APPLICATION_JSON).async().get(responseClass).get();
        } catch (InterruptedException | ExecutionException e) {
            String msg = "Failed invoke get request";
            LOGGER.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }

    protected <T, R> R put(Class<?> apiClass, String apiMethod, Map<String, String> pathParams, Map<String, String> queryParams, T payload,
                           Class<R> responseClass) {
        return prepare(apiClass, apiMethod, pathParams, queryParams, MediaType.APPLICATION_JSON) .put(Entity.json(payload), responseClass);
    }

}


