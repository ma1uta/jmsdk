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
import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.auth.LoginRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginType;
import io.github.ma1uta.matrix.client.model.auth.SupportedLoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Authentication methods.
 */
public class AuthMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMethods.class);

    private final MatrixClient matrixClient;

    public AuthMethods(MatrixClient matrixClient) {
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
        loginRequest.setType(AuthApi.AuthType.PASSWORD);
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
        Objects.requireNonNull(loginRequest.getType(), "Type cannot be empty.");
        LoginResponse loginResponse = getMatrixClient().getRequestMethods()
            .post(AuthApi.class, "login", new RequestParams(), loginRequest, LoginResponse.class);

        getMatrixClient().updateCredentials(loginResponse);
    }

    /**
     * Logout.
     */
    public void logout() {
        LOGGER.debug("Logout");
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        requestMethods.post(AuthApi.class, "logout", new RequestParams(), "", EmptyResponse.class);
        if (getMatrixClient().isUpdateAccessToken()) {
            requestMethods.setAccessToken(null);
        }
    }

    /**
     * Logout from all devices and invalidate all access tokens.
     */
    public void logoutAll() {
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        requestMethods.post(AuthApi.class, "logoutAll", new RequestParams(), "", EmptyResponse.class);
        if (getMatrixClient().isUpdateAccessToken()) {
            requestMethods.setAccessToken(null);
        }
    }

    /**
     * Get supported login types.
     *
     * @return supported login types.
     */
    public List<LoginType> loginTypes() {
        return getMatrixClient().getRequestMethods()
            .get(AuthApi.class, "supportedLoginTypes", new RequestParams(), SupportedLoginResponse.class).getFlows();
    }
}
