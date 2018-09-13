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

import io.github.ma1uta.matrix.client.methods.RequestParams;

import javax.ws.rs.client.Client;

/**
 * Application Service Client.
 */
public class AppServiceClient extends MatrixClient {

    public AppServiceClient(String homeserverUrl, Client client, String userId) {
        super(homeserverUrl, client, userId);
    }

    public AppServiceClient(String homeserverUrl, Client client, RequestParams defaultParams) {
        super(homeserverUrl, client, defaultParams);
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
