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
import io.github.ma1uta.matrix.client.api.AccountApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.account.AuthenticationData;
import io.github.ma1uta.matrix.client.model.account.AvailableResponse;
import io.github.ma1uta.matrix.client.model.account.DeactivateRequest;
import io.github.ma1uta.matrix.client.model.account.EmailRequestToken;
import io.github.ma1uta.matrix.client.model.account.PasswordRequest;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidResponse;
import io.github.ma1uta.matrix.client.model.account.WhoamiResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Account methods.
 */
public class AccountMethods extends AbstractMethods {

    private final Function<LoginResponse, LoginResponse> callback;

    public AccountMethods(RequestFactory factory, RequestParams defaultParams, Function<LoginResponse, LoginResponse> callback) {
        super(factory, defaultParams);
        this.callback = callback;
    }

    protected Function<LoginResponse, LoginResponse> getCallback() {
        return callback;
    }

    /**
     * Register a new user.
     *
     * @param request registration request.
     * @return date of the registered user..
     */
    public CompletableFuture<LoginResponse> register(RegisterRequest request) {
        RequestParams params = defaults().clone().query("kind", AccountApi.RegisterType.USER);
        return factory().post(AccountApi.class, "register", params, request, LoginResponse.class).thenApply(getCallback());
    }

    /**
     * Request token.
     *
     * @param requestToken request token.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> requestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");
        return factory().post(AccountApi.class, "requestToken", defaults(), requestToken, EmptyResponse.class);
    }

    /**
     * Change password.
     *
     * @param password new password.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> password(String password) {
        Objects.requireNonNull(password, "Password cannot be empty.");
        PasswordRequest request = new PasswordRequest();
        request.setNewPassword(password);
        return factory().post(AccountApi.class, "password", defaults(), request, EmptyResponse.class);
    }

    /**
     * Request validation tokens.
     *
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> passwordRequestToken() {
        return factory().post(AccountApi.class, "passwordRequestToken", defaults(), "", EmptyResponse.class);
    }

    /**
     * Deactivate user.
     *
     * @param auth authentication data.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> deactivate(AuthenticationData auth) {
        DeactivateRequest request = new DeactivateRequest();
        request.setAuth(auth);
        return factory().post(AccountApi.class, "deactivate", defaults(), request, EmptyResponse.class);
    }

    /**
     * Checks to see if a username is available, and valid, for the server.
     *
     * @param username checked username.
     * @return {@code} if available, else {@code false}.
     */
    public CompletableFuture<Boolean> available(String username) {
        Objects.requireNonNull(username, "Username cannot be empty");
        RequestParams params = defaults().clone().query("username", username);
        return factory().get(AccountApi.class, "available", params, AvailableResponse.class).thenApply(AvailableResponse::getAvailable);
    }

    /**
     * Gets a list of the third party identifiers that the homeserver has associated with the user's account.
     *
     * @return third party identifiers.
     */
    public CompletableFuture<ThreePidResponse> showThreePid() {
        return factory().get(AccountApi.class, "showThreePid", defaults(), ThreePidResponse.class);
    }

    /**
     * Adds contact information to the user's account.
     *
     * @param request new contact information.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> updateThreePid(ThreePidRequest request) {
        String error = "Threepids cannot be empty.";
        Objects.requireNonNull(request.getThreePidCreds(), error);
        if (request.getThreePidCreds().length == 0) {
            throw new NullPointerException(error);
        }
        return factory().post(AccountApi.class, "updateThreePid", defaults(), request, EmptyResponse.class);
    }

    /**
     * Proxies the identity server API validate/email/requestToken.
     *
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> threePidRequestToken() {
        return factory().post(AccountApi.class, "threePidRequestToken", defaults(), "", EmptyResponse.class);
    }

    /**
     * Gets information about the owner of a given access token.
     *
     * @return information about the owner of a given access token.
     */
    public CompletableFuture<WhoamiResponse> whoami() {
        return factory().get(AccountApi.class, "whoami", defaults(), WhoamiResponse.class);
    }
}
