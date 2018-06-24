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
import io.github.ma1uta.matrix.client.api.ClientConfigApi;

import java.util.HashMap;
import java.util.Map;

/**
 * Client config methods.
 */
public class ClientConfigMethods {

    private final MatrixClient matrixClient;

    ClientConfigMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param userId      The id of the user to set account_data for. The access token must be authorized to make requests for this user id.
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData account data.
     */
    public void addConfig(String userId, String type, Map<String, String> accountData) {
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        pathParams.put("type", type);
        getMatrixClient().getRequestMethods().put(ClientConfigApi.class, "addConfig", pathParams, null, accountData, EmptyResponse.class);
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param userId      The id of the user to set account_data for. The access token must be authorized to make requests for this user id.
     * @param roomId      The id of the room to set account_data on.
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData account data.
     */
    public void addRoomConfig(String userId, String roomId, String type, Map<String, String> accountData) {
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("userId", userId);
        pathParams.put("roomId", roomId);
        pathParams.put("type", type);
        getMatrixClient().getRequestMethods()
            .put(ClientConfigApi.class, "addRoomConfig", pathParams, null, accountData, EmptyResponse.class);
    }
}
