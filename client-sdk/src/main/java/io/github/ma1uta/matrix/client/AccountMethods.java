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
import io.github.ma1uta.matrix.client.api.AccountApi;
import io.github.ma1uta.matrix.client.model.account.DeactivateRequest;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Account methods.
 */
public class AccountMethods {

    private final MatrixClient matrixClient;

    AccountMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Register a new user.
     *
     * @param request registration request.
     */
    public void register(RegisterRequest request) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("kind", AccountApi.RegisterType.USER);
        LoginResponse registered = getMatrixClient().getRequestMethods()
            .post(AccountApi.class, "register", null, queryParams, request, LoginResponse.class);

        getMatrixClient().updateCredentials(registered);
    }

    /**
     * Deactivate user.
     */
    public void deactivate() {
        getMatrixClient().getRequestMethods()
            .post(AccountApi.class, "deactivate", null, null, new DeactivateRequest(), EmptyResponse.class);
    }
}
