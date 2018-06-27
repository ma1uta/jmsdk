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
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.ClientConfigApi;

import java.util.Map;

/**
 * Client config methods.
 */
public class ClientConfigMethods {

    private final MatrixClient matrixClient;

    public ClientConfigMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData account data.
     */
    public void addConfig(String type, Map<String, String> accountData) {
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        RequestParams params = new RequestParams().pathParam("userId", requestMethods.getUserId()).pathParam("type", type);
        requestMethods.put(ClientConfigApi.class, "addConfig", params, accountData, EmptyResponse.class);
    }

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     *
     * @param roomId      The id of the room to set account_data on.
     * @param type        The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData account data.
     */
    public void addRoomConfig(String roomId, String type, Map<String, String> accountData) {
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        RequestParams params = new RequestParams().pathParam("userId", requestMethods.getUserId()).pathParam("roomId", roomId)
            .pathParam("type", type);
        requestMethods.put(ClientConfigApi.class, "addRoomConfig", params, accountData, EmptyResponse.class);
    }
}
