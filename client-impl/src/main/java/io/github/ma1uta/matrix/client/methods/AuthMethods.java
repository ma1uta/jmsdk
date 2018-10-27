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
import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.auth.LoginRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginType;
import io.github.ma1uta.matrix.client.model.auth.SupportedLoginResponse;
import io.github.ma1uta.matrix.client.model.auth.UserIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Authentication methods.
 */
public class AuthMethods extends AbstractMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMethods.class);

    private final Function<LoginResponse, LoginResponse> afterLogin;
    private final Function<EmptyResponse, EmptyResponse> afterLogout;

    public AuthMethods(RequestFactory factory, RequestParams defaultParams,
                       Function<LoginResponse, LoginResponse> afterLogin,
                       Function<EmptyResponse, EmptyResponse> afterLogout) {
        super(factory, defaultParams);
        this.afterLogin = afterLogin;
        this.afterLogout = afterLogout;
    }

    protected Function<LoginResponse, LoginResponse> afterLogin() {
        return afterLogin;
    }

    protected Function<EmptyResponse, EmptyResponse> afterLogout() {
        return afterLogout;
    }

    /**
     * Login.
     *
     * @param login    The user MXID.
     * @param password The password.
     * @return The login response.
     */
    public CompletableFuture<LoginResponse> login(String login, char[] password) {
        LOGGER.debug("Login with username: ''{}'' and password: ''<redacted>''", login);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setType(AuthApi.AuthType.PASSWORD);
        UserIdentifier identifier = new UserIdentifier();
        identifier.setUser(login);
        loginRequest.setIdentifier(identifier);
        loginRequest.setPassword(password);

        return login(loginRequest);
    }

    /**
     * Login.
     *
     * @param loginRequest The login request.
     * @return The login response.
     */
    public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {
        Objects.requireNonNull(loginRequest.getType(), "Type cannot be empty.");

        return factory().post(AuthApi.class, "login", defaults(), loginRequest, LoginResponse.class).thenApply(afterLogin());
    }

    /**
     * Logout.
     *
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> logout() {
        LOGGER.debug("Logout");
        return factory().post(AuthApi.class, "logout", defaults(), "", EmptyResponse.class).thenApply(afterLogout());
    }

    /**
     * Logout from all devices and invalidate all access tokens.
     *
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> logoutAll() {
        return factory().post(AuthApi.class, "logoutAll", defaults(), "", EmptyResponse.class).thenApply(afterLogout());
    }

    /**
     * Get supported login types.
     *
     * @return The supported login types.
     */
    public CompletableFuture<List<LoginType>> loginTypes() {
        return factory().get(AuthApi.class, "supportedLoginTypes", defaults(), SupportedLoginResponse.class)
            .thenApply(SupportedLoginResponse::getFlows);
    }
}
