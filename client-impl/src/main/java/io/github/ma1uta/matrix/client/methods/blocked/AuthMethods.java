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

package io.github.ma1uta.matrix.client.methods.blocked;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.model.auth.SupportedLoginResponse;
import io.github.ma1uta.matrix.client.model.auth.UserIdentifier;
import io.github.ma1uta.matrix.client.rest.blocked.AuthApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Function;

/**
 * Authentication methods.
 */
public class AuthMethods {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthMethods.class);

    private final Function<LoginResponse, LoginResponse> afterLogin;
    private final Function<EmptyResponse, EmptyResponse> afterLogout;
    private final AuthApi authApi;

    public AuthMethods(RestClientBuilder restClientBuilder,
                       Function<LoginResponse, LoginResponse> afterLogin,
                       Function<EmptyResponse, EmptyResponse> afterLogout) {
        this.authApi = restClientBuilder.build(AuthApi.class);
        this.afterLogin = afterLogin;
        this.afterLogout = afterLogout;
    }

    /**
     * Login.
     *
     * @param login    The user MXID.
     * @param password The password.
     * @return The login response.
     */
    public LoginResponse login(String login, char[] password) {
        LOGGER.debug("Login with username: ''{}'' and password: ''<redacted>''", login);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setType(io.github.ma1uta.matrix.client.api.AuthApi.AuthType.PASSWORD);
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
    public LoginResponse login(LoginRequest loginRequest) {
        Objects.requireNonNull(loginRequest.getType(), "Type cannot be empty.");

        LoginResponse response = authApi.login(loginRequest);
        return afterLogin != null ? afterLogin.apply(response) : response;
    }

    /**
     * Logout.
     *
     * @return The empty response.
     */
    public EmptyResponse logout() {
        EmptyResponse response = authApi.logout();
        return afterLogout != null ? afterLogout.apply(response) : response;
    }

    /**
     * Logout from all devices and invalidate all access tokens.
     *
     * @return The empty response.
     */
    public EmptyResponse logoutAll() {
        EmptyResponse response = authApi.logoutAll();
        return afterLogout != null ? afterLogout.apply(response) : response;
    }

    /**
     * Get supported login types.
     *
     * @return The supported login types.
     */
    public SupportedLoginResponse loginTypes() {
        return authApi.supportedLoginTypes();
    }
}
