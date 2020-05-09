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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.ConnectionInfo;
import io.github.ma1uta.matrix.client.rest.async.ClientConfigApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Client config methods.
 */
public class ClientConfigAsyncMethods {

    private final ClientConfigApi clientConfigApi;

    private final ConnectionInfo connectionInfo;

    public ClientConfigAsyncMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.clientConfigApi = restClientBuilder.build(ClientConfigApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData The account data.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> addConfig(String type, Map<String, Object> accountData) {
        Objects.requireNonNull(type, "Type cannot be empty.");
        Objects.requireNonNull(connectionInfo.getUserId(), "UserId cannot be empty.");

        return clientConfigApi.addConfig(connectionInfo.getUserId(), type, accountData).toCompletableFuture();
    }

    /**
     * Get some account_data for the client. This config is only visible to the user that get the account_data.
     *
     * @param type The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @return The account_data.
     */
    public CompletableFuture<Map> getConfig(String type) {
        Objects.requireNonNull(type, "Type cannot be empty.");
        Objects.requireNonNull(connectionInfo.getUserId(), "UserId cannot be empty.");

        return clientConfigApi.getConfig(connectionInfo.getUserId(), type).toCompletableFuture();
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
    public CompletableFuture<EmptyResponse> addRoomConfig(String roomId, String type, Map<String, Object> accountData) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(type, "Type cannot be empty");
        Objects.requireNonNull(connectionInfo.getUserId(), "UserId cannot be empty");

        return clientConfigApi.addRoomConfig(connectionInfo.getUserId(), roomId, type, accountData).toCompletableFuture();
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
        Objects.requireNonNull(connectionInfo.getUserId(), "UserId cannot be empty");

        return clientConfigApi.getRoomConfig(connectionInfo.getUserId(), roomId, type).toCompletableFuture();
    }
}
