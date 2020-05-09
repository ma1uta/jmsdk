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

package io.github.ma1uta.matrix.client.rest.blocked;

import io.github.ma1uta.matrix.EmptyResponse;
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
import io.github.ma1uta.matrix.thirdpid.SessionResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Account registration and management.
 */
@Path("/_matrix/client/r0")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface AccountApi {

    /**
     * Register for an account on this homeserver.
     *
     * @param kind            The kind of account to register. Defaults to user. One of: ["guest", "user"].
     * @param registerRequest JSON body parameters.
     * @return {@link LoginResponse}.
     */
    @POST
    @Path("/register")
    LoginResponse register(
        @QueryParam("kind") String kind,
        RegisterRequest registerRequest
    );

    /**
     * Proxies the identity server API validate/email/emailRequestToken, but first checks that the given email address is not already
     * associated with an account on this Home Server. Note that, for consistency, this API takes JSON objects, though the
     * Identity Server API takes x-www-form-urlencoded parameters. See the Identity Server API for further information.
     *
     * @param emailRequestToken JSON body request.
     * @return {@link SessionResponse}.
     */
    @POST
    @Path("/register/email/requestToken")
    SessionResponse emailRequestToken(
        EmailRequestToken emailRequestToken
    );

    /**
     * Proxies the Identity Service API validate/msisdn/requestToken, but first checks that the given phone number is not already
     * associated with an account on this homeserver. See the Identity Service API for further information.
     *
     * @param msisdnRequestToken JSON body request.
     * @return {@link SessionResponse}.
     */
    @POST
    @Path("/register/msisdn/requestToken")
    SessionResponse msisdnRequestToken(
        MsisdnRequestToken msisdnRequestToken
    );

    /**
     * Changes the password for an account on this homeserver.
     *
     * @param passwordRequest Password.
     * @return {@link EmptyResponse}.
     */
    @POST
    @Path("/account/password")
    EmptyResponse password(
        PasswordRequest passwordRequest
    );

    /**
     * Proxies the identity server API validate/email/requestToken, but first checks that the given email address is associated
     * with an account on this Home Server. This API should be used to request validation tokens when authenticating for
     * the account/password endpoint. This API's parameters and response are identical to that of the HS API
     * /register/email/requestToken except that M_THREEPID_NOT_FOUND may be returned if no account matching the given email
     * address could be found. The server may instead send an email to the given address prompting the user to create an account.
     * M_THREEPID_IN_USE may not be returned.
     *
     * @param requestToken JSON body request.
     * @return {@link SessionResponse}.
     */
    @POST
    @Path("/account/password/email/requestToken")
    SessionResponse passwordEmailRequestToken(
        EmailRequestToken requestToken
    );

    /**
     * Proxies the Identity Service API validate/msisdn/requestToken, but first checks that the given phone number is associated
     * with an account on this homeserver. This API should be used to request validation tokens when authenticating for
     * the account/password endpoint. This API's parameters and response are identical to that of the HS API /register/msisdn/requestToken
     * except that M_THREEPID_NOT_FOUND may be returned if no account matching the given phone number could be found. The server may
     * instead send an SMS message to the given address prompting the user to create an account. M_THREEPID_IN_USE may not be returned.
     *
     * @param requestToken JSON body request.
     * @return {@link SessionResponse}.
     */
    @POST
    @Path("/account/password/msisdn/requestToken")
    SessionResponse passwordMsisdnRequestToken(
        MsisdnRequestToken requestToken
    );

    /**
     * Deactivate the user's account, removing all ability for the user to login again.
     * <br>
     * This API endpoint uses the User-Interactive Authentication API.
     * <br>
     * An access token should be submitted to this endpoint if the client has an active session.
     * <br>
     * The homeserver may change the flows available depending on whether a valid access token is provided.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link DeactivateResponse}.
     * <p>Status code 200: The account has been deactivated.</p>
     * <p>Status code 401: The homeserver requires additional authentication information.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param deactivateRequest JSON body request.
     * @return {@link DeactivateResponse}.
     */
    @POST
    @Path("/account/deactivate")
    DeactivateResponse deactivate(
        DeactivateRequest deactivateRequest
    );

    /**
     * Checks to see if a username is available, and valid, for the server.
     * <br>
     * The server should check to ensure that, at the time of the request, the username requested is available for use.
     * This includes verifying that an application service has not claimed the username and that the username fits the server's
     * desired requirements (for example, a server could dictate that it does not permit usernames with underscores).
     * <br>
     * Matrix clients may wish to use this API prior to attempting registration, however the clients must also be aware
     * that using this API does not normally reserve the username. This can mean that the username becomes unavailable
     * between checking its availability and attempting to register it.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * Return: {@link AvailableResponse}.
     * <p>Status code 200: The username is available.</p>
     * <p>Status code 400: Part of the request was invalid or the username is not available. This may include one of the following error
     * codes:</p>
     * <ul>
     * <li>M_USER_IN_USE : The desired username is already taken.</li>
     * <li>M_INVALID_USERNAME : The desired username is not a valid user name.</li>
     * <li>M_EXCLUSIVE : The desired username is in the exclusive namespace claimed by an application service.</li>
     * </ul>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param username Required. The username to check the availability of.
     * @return {@link AvailableResponse}.
     */
    @GET
    @Path("/register/available")
    AvailableResponse available(
        @QueryParam("username") String username
    );

    /**
     * Gets a list of the third party identifiers that the homeserver has associated with the user's account.
     * <br>
     * This is not the same as the list of third party identifiers bound to the user's Matrix ID in Identity Servers.
     * <br>
     * Identifiers in this list may be used by the homeserver as, for example, identifiers that it will accept to reset the user's
     * account password.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link ThreePidResponse}.
     * <p>Status code 200: The lookup was successful.</p>
     *
     * @return {@link ThreePidResponse}.
     */
    @GET
    @Path("/account/3pid")
    ThreePidResponse getThreePid();

    /**
     * Adds contact information to the user's account.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link EmptyResponse}.
     * <p>Status code 200: The addition was successful.</p>
     * <p>Status code 403: The credentials could not be verified with the identity server.</p>
     *
     * @param threePidRequest New contact information.
     * @return {@link EmptyResponse}
     */
    @POST
    @Path("/account/3pid")
    EmptyResponse updateThreePid(
        ThreePidRequest threePidRequest
    );

    /**
     * Removes a third party identifier from the user's account. This might not cause an unbind of the identifier from the identity server.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link DeactivateResponse}.
     * <p>Status code 200: The homeserver has disassociated the third party identifier from the user.</p>
     *
     * @param request JSON body request to delete 3pid.
     * @return {@link DeactivateResponse}
     */
    @POST
    @Path("/account/3pid/delete")
    DeactivateResponse deleteThreePid(
        Delete3PidRequest request
    );

    /**
     * Proxies the identity server API validate/email/requestToken, but first checks that the given email address is not already
     * associated with an account on this Home Server. This API should be used to request validation tokens when adding an email
     * address to an account. This API's parameters and response is identical to that of the HS API /register/email/requestToken endpoint.
     * <br>
     * Return: {@link SessionResponse}.
     * <p>Status code 200: An email was sent to the given address.</p>
     * <p>Status code 400: The third party identifier is already in use on the homeserver, or the request was invalid.</p>
     * <p>Status code 403: The homeserver does not allow the third party identifier as a contact option.</p>
     *
     * @param requestToken JSON body request.
     * @return {@link SessionResponse}
     */
    @POST
    @Path("/account/3pid/email/requestToken")
    SessionResponse threePidEmailRequestToken(
        EmailRequestToken requestToken
    );

    /**
     * Proxies the Identity Service API validate/msisdn/requestToken, but first checks that the given phone number is not already
     * associated with an account on this homeserver. This API should be used to request validation tokens when adding a phone number
     * to an account. This API's parameters and response are identical to that of the /register/msisdn/requestToken endpoint.
     * <br>
     * Return: {@link SessionResponse}.
     * <p>Status code 200: An SMS message was sent to the given phone number.</p>
     * <p>Status code 400: The third party identifier is already in use on the homeserver, or the request was invalid.</p>
     * <p>Status code 403: The homeserver does not allow the third party identifier as a contact option.</p>
     *
     * @param requestToken JSON body request.
     * @return {@link SessionResponse}
     */
    @POST
    @Path("/account/3pid/msisdn/requestToken")
    SessionResponse threePidMsisdnRequestToken(
        MsisdnRequestToken requestToken
    );

    /**
     * Gets information about the owner of a given access token.
     * <br>
     * Note that, as with the rest of the Client-Server API, Application Services may masquerade as users within their namespace
     * by giving a user_id query parameter. In this situation, the server should verify that the given user_id is registered by
     * the appservice, and return it in the response body.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link WhoamiResponse}.
     * <p>Status code 200: The token belongs to a known user.</p>
     * <p>Status code 401: The token is not recognised.</p>
     * <p>Status code 403: The appservice cannot masquerade as the user or has not registered them.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @return {@link WhoamiResponse}.
     */
    @GET
    @Path("/account/whoami")
    WhoamiResponse whoami();
}
