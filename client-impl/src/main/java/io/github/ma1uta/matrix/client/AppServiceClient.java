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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.factory.AppRequestFactory;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;

import java.util.Objects;
import java.util.concurrent.Executor;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * Application Service Client.
 */
public class AppServiceClient extends MatrixClient {

    public AppServiceClient(String homeserverUrl, String userId) {
        this(homeserverUrl, ClientBuilder.newClient(), userId);
    }

    public AppServiceClient(String homeserverUrl, Client client, String userId) {
        this(homeserverUrl, client, new RequestParams().userId(userId));
    }

    public AppServiceClient(String homeserverUrl, Client client, RequestParams defaultParams) {
        this(homeserverUrl, client, defaultParams, null);
    }

    public AppServiceClient(String homeserverUrl, Client client, RequestParams defaultParams, Executor executor) {
        super(homeserverUrl, client, defaultParams, executor);
        if (defaultParams.getUserId() == null || defaultParams.getUserId().trim().isEmpty()) {
            throw new NullPointerException("The `user_id` should be specified.");
        }
    }

    @Override
    protected RequestFactory initFactory(Client client, String homeserverUrl, Executor executor) {
        return new AppRequestFactory(
            Objects.requireNonNull(client, "The `client` should be specified."),
            Objects.requireNonNull(homeserverUrl, "The `homeserverUrl` should be specified."),
            executor
        );
    }

    /**
     * Return a new AppService client with the specified `user_id`.
     *
     * @param userId The user MXID.
     * @return The new AppService client.
     */
    public AppServiceClient userId(String userId) {
        return new AppServiceClient(getHomeserverUrl(), getRequestFactory().getClient(), getDefaultParams().clone().userId(userId));
    }

    @Override
    public LoginResponse afterLogin(LoginResponse loginResponse) {
        return loginResponse;
    }

    @Override
    public EmptyResponse afterLogout(EmptyResponse response) {
        return response;
    }

    /**
     * App service client builder.
     */
    public static class Builder extends AbstractClientBuilder<AppServiceClient> {

        @Override
        public AppServiceClient newInstance() {
            return new AppServiceClient(getHomeserverUrl(), getClient(), getDefaultParams());
        }
    }
}
