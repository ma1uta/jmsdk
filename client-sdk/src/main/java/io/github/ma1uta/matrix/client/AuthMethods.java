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
import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.auth.AuthType;
import io.github.ma1uta.matrix.client.model.auth.LoginRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication methods.
 */
public class AuthMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMethods.class);

    private final MatrixClient matrixClient;

    AuthMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Login.
     *
     * @param login    username.
     * @param password password.
     */
    public void login(String login, String password) {
        LOGGER.debug("Login with username: ''{}'' and password: ''<redacted>''", login);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setType(AuthType.PASSWORD);
        loginRequest.setUser(login);
        loginRequest.setPassword(password);

        login(loginRequest);
    }

    /**
     * Login.
     *
     * @param loginRequest request.
     */
    public void login(LoginRequest loginRequest) {
        LoginResponse loginResponse = getMatrixClient().getRequestMethods()
            .post(AuthApi.class, "login", null, null, loginRequest, LoginResponse.class, null);

        getMatrixClient().updateCredentials(loginResponse);
    }

    /**
     * Logout.
     */
    public void logout() {
        LOGGER.debug("Logout");
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        requestMethods.post(AuthApi.class, "logout", null, null, "", EmptyResponse.class, null);
        if (getMatrixClient().isUpdateAccessToken()) {
            requestMethods.setAccessToken(null);
        }
    }
}
