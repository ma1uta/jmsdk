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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.ClientConfigApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Client config methods.
 */
public class ClientConfigMethods extends AbstractMethods {

    public ClientConfigMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData The account data.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> addConfig(String type, Map<String, String> accountData) {
        Objects.requireNonNull(type, "Type cannot be empty.");
        Objects.requireNonNull(defaults().getUserId(), "UserId cannot be empty.");
        RequestParams params = defaults().clone()
            .path("userId", defaults().getUserId())
            .path("type", type);

        return factory().put(ClientConfigApi.class, "addConfig", params, accountData, EmptyResponse.class);
    }

    /**
     * Get some account_data for the client. This config is only visible to the user that get the account_data.
     *
     * @param type The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @return The account_data.
     */
    public CompletableFuture<Map> getConfig(String type) {
        Objects.requireNonNull(type, "Type cannot be empty.");
        Objects.requireNonNull(defaults().getUserId(), "UserId cannot be empty.");
        RequestParams params = defaults().clone()
            .path("userId", defaults().getUserId())
            .path("type", type);

        return factory().get(ClientConfigApi.class, "getConfig", params, Map.class);
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param roomId      The id of the room to set account_data on.
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData The account data.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> addRoomConfig(String roomId, String type, Map<String, String> accountData) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(type, "Type cannot be empty");
        Objects.requireNonNull(defaults().getUserId(), "UserId cannot be empty");

        RequestParams params = defaults().clone()
            .path("userId", defaults().getUserId())
            .path("roomId", roomId)
            .path("type", type);
        return factory().put(ClientConfigApi.class, "addRoomConfig", params, accountData, EmptyResponse.class);
    }

    /**
     * Get some account_data for the client. This config is only visible to the user that get the account_data.
     *
     * @param roomId The id of the room to set account_data on.
     * @param type   The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @return The account_data.
     */
    public CompletableFuture<Map> getRoomConfig(String roomId, String type) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(type, "Type cannot be empty");
        Objects.requireNonNull(defaults().getUserId(), "UserId cannot be empty");

        RequestParams params = defaults().clone()
            .path("userId", defaults().getUserId())
            .path("roomId", roomId)
            .path("type", type);
        return factory().get(ClientConfigApi.class, "getRoomConfig", params, Map.class);
    }
}
