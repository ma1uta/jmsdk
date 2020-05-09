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

package io.github.ma1uta.matrix.client.methods.async;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.account.AuthenticationData;
import io.github.ma1uta.matrix.client.model.account.AvailableResponse;
import io.github.ma1uta.matrix.client.model.account.DeactivateRequest;
import io.github.ma1uta.matrix.client.model.account.DeactivateResponse;
import io.github.ma1uta.matrix.client.model.account.Delete3PidRequest;
import io.github.ma1uta.matrix.client.model.account.EmailRequestToken;
import io.github.ma1uta.matrix.client.model.account.MsisdnRequestToken;
import io.github.ma1uta.matrix.client.model.account.PasswordRequest;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidResponse;
import io.github.ma1uta.matrix.client.model.account.WhoamiResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.rest.async.AccountApi;
import io.github.ma1uta.matrix.thirdpid.SessionResponse;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Account methods.
 */
public class AccountAsyncMethods {

    private final AccountApi accountApi;
    private final Function<LoginResponse, LoginResponse> afterLogin;

    public AccountAsyncMethods(RestClientBuilder restClientBuilder, Function<LoginResponse, LoginResponse> afterLogin) {
        this.accountApi = restClientBuilder.build(AccountApi.class);
        this.afterLogin = afterLogin;
    }

    /**
     * Register a new user.
     *
     * @param request The registration request.
     * @return The date of the registered user..
     */
    public CompletableFuture<LoginResponse> register(RegisterRequest request) {
        return accountApi.register(io.github.ma1uta.matrix.client.api.AccountApi.RegisterType.USER, request).thenApply(afterLogin)
            .toCompletableFuture();
    }

    /**
     * Request the email token.
     *
     * @param requestToken The request token.
     * @return The session.
     */
    public CompletableFuture<SessionResponse> emailRequestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return accountApi.emailRequestToken(requestToken).toCompletableFuture();
    }

    /**
     * Request the SMS token.
     *
     * @param requestToken The request token.
     * @return The session.
     */
    public CompletableFuture<SessionResponse> msisdnRequestToken(MsisdnRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getCountry(), "Country cannot be empty.");
        Objects.requireNonNull(requestToken.getPhoneNumber(), "Phone number cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return accountApi.msisdnRequestToken(requestToken).toCompletableFuture();
    }

    /**
     * Change password.
     *
     * @param password The new password.
     * @param auth     Additional authentication information for the user-interactive authentication API.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> password(char[] password, AuthenticationData auth) {
        Objects.requireNonNull(password, "Password cannot be empty.");

        PasswordRequest request = new PasswordRequest();
        request.setNewPassword(password);
        request.setAuth(auth);

        return accountApi.password(request).toCompletableFuture();
    }

    /**
     * Request the email validation tokens.
     *
     * @param requestToken The request token.
     * @return The session.
     */
    public CompletableFuture<SessionResponse> passwordEmailRequestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return accountApi.passwordEmailRequestToken(requestToken).toCompletableFuture();
    }

    /**
     * Request the SMS validation tokens.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<SessionResponse> passwordMsisdnRequestToken(MsisdnRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getCountry(), "Country cannot be empty.");
        Objects.requireNonNull(requestToken.getPhoneNumber(), "Phone number cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return accountApi.passwordMsisdnRequestToken(requestToken).toCompletableFuture();
    }

    /**
     * Deactivate user.
     *
     * @param auth The authentication data.
     * @return The unbind result.
     */
    public CompletableFuture<DeactivateResponse> deactivate(AuthenticationData auth) {
        DeactivateRequest request = new DeactivateRequest();
        request.setAuth(auth);

        return accountApi.deactivate(request).toCompletableFuture();
    }

    /**
     * Checks to see if a username is available, and valid, for the server.
     *
     * @param username The checked username.
     * @return {@code true} if available, else {@code false}.
     */
    public CompletableFuture<AvailableResponse> available(String username) {
        Objects.requireNonNull(username, "Username cannot be empty");

        return accountApi.available(username).toCompletableFuture();
    }

    /**
     * Gets a list of the third party identifiers that the homeserver has associated with the user's account.
     *
     * @return The third party identifiers.
     */
    public CompletableFuture<ThreePidResponse> getThreePid() {
        return accountApi.getThreePid().toCompletableFuture();
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

        return accountApi.updateThreePid(request).toCompletableFuture();
    }

    /**
     * Delete the 3pid.
     *
     * @param medium  The medium of the 3pid identifier.
     * @param address The 3pid address (email of phone).
     * @return The unbind resule.
     */
    public CompletableFuture<DeactivateResponse> deleteThreePid(String medium, String address) {
        Objects.requireNonNull(medium, "Medium cannot be empty.");
        Objects.requireNonNull(address, "Address cannot be empty.");

        Delete3PidRequest request = new Delete3PidRequest();
        request.setMedium(medium);
        request.setAddress(address);

        return accountApi.deleteThreePid(request).toCompletableFuture();
    }

    /**
     * Proxies the identity server API validate/email/emailRequestToken.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<SessionResponse> threePidEmailRequestToken(EmailRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getEmail(), "Email cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return accountApi.threePidEmailRequestToken(requestToken).toCompletableFuture();
    }

    /**
     * Proxies the identity server API validate/msisdn/msisdnRequestToken.
     *
     * @param requestToken The request token.
     * @return The empty response.
     */
    public CompletableFuture<SessionResponse> threePidMsisdnRequestToken(MsisdnRequestToken requestToken) {
        Objects.requireNonNull(requestToken.getClientSecret(), "Client secret cannot be empty.");
        Objects.requireNonNull(requestToken.getCountry(), "Country cannot be empty.");
        Objects.requireNonNull(requestToken.getPhoneNumber(), "Phone number cannot be empty.");
        Objects.requireNonNull(requestToken.getSendAttempt(), "Send attempt cannot be empty.");

        return accountApi.threePidMsisdnRequestToken(requestToken).toCompletableFuture();
    }

    /**
     * Gets information about the owner of a given access token.
     *
     * @return The information about the owner of a given access token.
     */
    public CompletableFuture<WhoamiResponse> whoami() {
        return accountApi.whoami().toCompletableFuture();
    }
}
