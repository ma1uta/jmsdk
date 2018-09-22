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
import io.github.ma1uta.matrix.client.model.account.Delete3PidRequest;
import io.github.ma1uta.matrix.client.model.account.EmailRequestToken;
import io.github.ma1uta.matrix.client.model.account.MsisdnRequestToken;
import io.github.ma1uta.matrix.client.model.account.PasswordRequest;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidResponse;
import io.github.ma1uta.matrix.client.model.account.WhoamiResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.thirdpid.SessionResponse;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Account methods.
 */
public class AccountMethods extends AbstractMethods {

    private final Function<LoginResponse, LoginResponse> afterLogin;

    public AccountMethods(RequestFactory factory, RequestParams defaultParams, Function<LoginResponse, LoginResponse> afterLogin) {
        super(factory, defaultParams);
        this.afterLogin = afterLogin;
    }

    protected Function<LoginResponse, LoginResponse> getAfterLogin() {
        return afterLogin;
    }

    /**
     * Register a new user.
     *
     * @param request The registration request.
     * @return The date of the registered user..
     */
    public CompletableFuture<LoginResponse> register(RegisterRequest request) {
        RequestParams params = defaults().clone().query("kind", AccountApi.RegisterType.USER);
        return factory().post(AccountApi.class, "register", params, request, LoginResponse.class).thenApply(getAfterLogin());
    }

    /**
     * Request the email token.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<SessionResponse> emailRequestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return factory().post(AccountApi.class, "emailRequestToken", defaults(), requestToken, SessionResponse.class);
    }

    /**
     * Request the SMS token.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> msisdnRequestToken(MsisdnRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getCountry(), "Country cannot be empty.");
        Objects.requireNonNull(requestToken.getPhoneNumber(), "Phone number cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return factory().post(AccountApi.class, "msisdnRequestToken", defaults(), requestToken, EmptyResponse.class);
    }

    /**
     * Change password.
     *
     * @param password The new password.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> password(String password) {
        Objects.requireNonNull(password, "Password cannot be empty.");

        PasswordRequest request = new PasswordRequest();
        request.setNewPassword(password);
        return factory().post(AccountApi.class, "password", defaults(), request, EmptyResponse.class);
    }

    /**
     * Request the email validation tokens.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> passwordEmailRequestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return factory().post(AccountApi.class, "passwordEmailRequestToken", defaults(), requestToken, EmptyResponse.class);
    }

    /**
     * Request the SMS validation tokens.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> passwordMsisdnRequestToken(MsisdnRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getCountry(), "Country cannot be empty.");
        Objects.requireNonNull(requestToken.getPhoneNumber(), "Phone number cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return factory().post(AccountApi.class, "passwordMsisdnRequestToken", defaults(), requestToken, EmptyResponse.class);
    }

    /**
     * Deactivate user.
     *
     * @param auth The authentication data.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> deactivate(AuthenticationData auth) {
        DeactivateRequest request = new DeactivateRequest();
        request.setAuth(auth);
        return factory().post(AccountApi.class, "deactivate", defaults(), request, EmptyResponse.class);
    }

    /**
     * Checks to see if a username is available, and valid, for the server.
     *
     * @param username The checked username.
     * @return {@code true} if available, else {@code false}.
     */
    public CompletableFuture<Boolean> available(String username) {
        Objects.requireNonNull(username, "Username cannot be empty");

        RequestParams params = defaults().clone().query("username", username);
        return factory().get(AccountApi.class, "available", params, AvailableResponse.class).thenApply(AvailableResponse::getAvailable);
    }

    /**
     * Gets a list of the third party identifiers that the homeserver has associated with the user's account.
     *
     * @return The third party identifiers.
     */
    public CompletableFuture<ThreePidResponse> getThreePid() {
        return factory().get(AccountApi.class, "getThreePid", defaults(), ThreePidResponse.class);
    }

    /**
     * Adds contact information to the user's account.
     *
     * @param request The new contact information.
     * @return The empty response.
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
     * Delete the 3pid.
     *
     * @param medium  The medium of the 3pid identifier.
     * @param address The 3pid address (email of phone).
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> deleteThreePid(String medium, String address) {
        Objects.requireNonNull(medium, "Medium cannot be empty.");
        Objects.requireNonNull(address, "Address cannot be empty.");

        Delete3PidRequest request = new Delete3PidRequest();
        request.setMedium(medium);
        request.setAddress(address);
        return factory().post(AccountApi.class, "deleteThreePid", defaults(), request, EmptyResponse.class);
    }

    /**
     * Proxies the identity server API validate/email/emailRequestToken.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> threePidEmailRequestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return factory().post(AccountApi.class, "threePidEmailRequestToken", defaults(), requestToken, EmptyResponse.class);
    }

    /**
     * Proxies the identity server API validate/msisdn/msisdnRequestToken.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> threePidMsisdnRequestToken(MsisdnRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getCountry(), "Country cannot be empty.");
        Objects.requireNonNull(requestToken.getPhoneNumber(), "Phone number cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return factory().post(AccountApi.class, "threePidMsisdnRequestToken", defaults(), requestToken, EmptyResponse.class);
    }

    /**
     * Gets information about the owner of a given access token.
     *
     * @return The information about the owner of a given access token.
     */
    public CompletableFuture<WhoamiResponse> whoami() {
        return factory().get(AccountApi.class, "whoami", defaults(), WhoamiResponse.class);
    }
}
