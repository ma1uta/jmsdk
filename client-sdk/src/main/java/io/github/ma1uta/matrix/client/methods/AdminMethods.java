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

import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.AdminApi;
import io.github.ma1uta.matrix.client.model.admin.AdminResponse;

/**
 * Admin methods.
 */
public class AdminMethods {

    private final MatrixClient matrixClient;

    public AdminMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * This API may be restricted to only be called by the user being looked up, or by a server admin. Server-local administrator
     * privileges are not specified in this document.
     *
     * @param userId user mxid
     * @return user information.
     */
    public AdminResponse whois(String userId) {
        RequestParams params = new RequestParams().pathParam("userId", userId);
        return getMatrixClient().getRequestMethods().get(AdminApi.class, "whois", params, AdminResponse.class);
    }
}
