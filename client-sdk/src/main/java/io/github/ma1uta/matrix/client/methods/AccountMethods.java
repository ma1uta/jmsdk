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
import io.github.ma1uta.matrix.client.api.AccountApi;
import io.github.ma1uta.matrix.client.model.account.AuthenticationData;
import io.github.ma1uta.matrix.client.model.account.AvailableResponse;
import io.github.ma1uta.matrix.client.model.account.DeactivateRequest;
import io.github.ma1uta.matrix.client.model.account.PasswordRequest;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.account.RequestToken;
import io.github.ma1uta.matrix.client.model.account.ThreePidRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidResponse;
import io.github.ma1uta.matrix.client.model.account.WhoamiResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;

import java.util.Objects;

/**
 * Account methods.
 */
public class AccountMethods {

    private final MatrixClient matrixClient;

    public AccountMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Register a new user.
     *
     * @param request registration request.
     * @return date of the registered user..
     */
    public LoginResponse register(RegisterRequest request) {
        RequestParams params = new RequestParams().queryParam("kind", AccountApi.RegisterType.USER);
        LoginResponse registered = getMatrixClient().getRequestMethods()
            .post(AccountApi.class, "register", params, request, LoginResponse.class);

        getMatrixClient().updateCredentials(registered);
        return registered;
    }

    /**
     * Request token.
     *
     * @param requestToken request token.
     */
    public void requestToken(RequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");
        getMatrixClient().getRequestMethods()
            .post(AccountApi.class, "requestToken", new RequestParams(), requestToken, EmptyResponse.class);
    }

    /**
     * Change password.
     *
     * @param password new password.
     */
    public void password(String password) {
        Objects.requireNonNull(password, "Password cannot be empty.");
        PasswordRequest request = new PasswordRequest();
        request.setNewPassword(password);
        getMatrixClient().getRequestMethods().post(AccountApi.class, "password", new RequestParams(), request, EmptyResponse.class);
    }

    /**
     * Request validation tokens.
     */
    public void passwordRequestToken() {
        getMatrixClient().getRequestMethods().post(AccountApi.class, "passwordRequestToken", new RequestParams(), "", EmptyResponse.class);
    }

    /**
     * Deactivate user.
     *
     * @param auth authentication data.
     */
    public void deactivate(AuthenticationData auth) {
        getMatrixClient().getRequestMethods()
            .post(AccountApi.class, "deactivate", new RequestParams(), new DeactivateRequest(), EmptyResponse.class);
    }

    /**
     * Checks to see if a username is available, and valid, for the server.
     *
     * @param username checked username.
     * @return {@code} if available, else {@code false}.
     */
    public boolean available(String username) {
        Objects.requireNonNull(username, "Username cannot be empty");
        RequestParams params = new RequestParams().queryParam("username", username);
        return getMatrixClient().getRequestMethods().get(AccountApi.class, "available", params, AvailableResponse.class).getAvailable();
    }

    /**
     * Gets a list of the third party identifiers that the homeserver has associated with the user's account.
     *
     * @return third party identifiers.
     */
    public ThreePidResponse showThreePid() {
        return getMatrixClient().getRequestMethods().get(AccountApi.class, "showThreePid", new RequestParams(), ThreePidResponse.class);
    }

    /**
     * Adds contact information to the user's account.
     *
     * @param request new contact information.
     */
    public void updateThreePid(ThreePidRequest request) {
        String error = "Threepids cannot be empty.";
        Objects.requireNonNull(request.getThreePidCreds(), error);
        if (request.getThreePidCreds().length == 0) {
            throw new NullPointerException(error);
        }
        getMatrixClient().getRequestMethods().post(AccountApi.class, "updateThreePid", new RequestParams(), request, EmptyResponse.class);
    }

    /**
     * Proxies the identity server API validate/email/requestToken.
     */
    public void threePidRequestToken() {
        getMatrixClient().getRequestMethods().post(AccountApi.class, "threePidRequestToken", new RequestParams(), "", EmptyResponse.class);
    }

    /**
     * Gets information about the owner of a given access token.
     *
     * @return information about the owner of a given access token.
     */
    public WhoamiResponse whoami() {
        return getMatrixClient().getRequestMethods().get(AccountApi.class, "whoami", new RequestParams(), WhoamiResponse.class);
    }
}
