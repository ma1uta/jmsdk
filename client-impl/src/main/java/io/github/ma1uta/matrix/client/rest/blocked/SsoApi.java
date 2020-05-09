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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

/**
 * Single Sign-On (SSO) is a generic term which refers to protocols which allow users to log into applications via a single web-based
 * authentication portal. Examples include "Central Authentication Service" (CAS) and SAML.
 * <br>
 * An overview of the process, as used in Matrix, is as follows:
 * <ul>
 * <li>The Matrix client instructs the user's browser to navigate to the /login/sso/redirect endpoint on the user's homeserver.</li>
 * <li>The homeserver responds with an HTTP redirect to the CAS user interface, which the browser follows.</li>
 * <li>The SSO system authenticates the user.</li>
 * <li>The SSO server responds to the user's browser with a redirect back to the /login/cas/ticket endpoint on the homeserver,
 * which the browser follows. A 'ticket' identifier is passed as a query parameter in the redirect.</li>
 * <li>The homeserver receives the ticket ID from the user's browser, and makes a request to the SSO server to validate the ticket.</li>
 * <li>Having validated the ticket, the homeserver responds to the browser with a third HTTP redirect, back to the Matrix
 * client application. A login token is passed as a query parameter in the redirect.</li>
 * <li>The Matrix client receives the login token and passes it to the /login API.</li>
 * </ul>
 */
@Path("/_matrix/client/r0/login/sso")
public interface SsoApi {

    /**
     * A web-based Matrix client should instruct the user's browser to navigate to this endpoint in order to log in via CAS.
     * <br>
     * The server MUST respond with an HTTP redirect to the SSO interface.
     * <br>
     * Return: {@link EmptyResponse}.
     * A redirect to the SSO interface.
     *
     * @param redirectUrl   Required. URI to which the user will be redirected after the homeserver has authenticated the user with CAS.
     * @param uriInfo       Request Information.
     * @param httpHeaders   Http headers.
     * @param asyncResponse Asynchronous response.
     */
    @Operation(
        summary = "A web-based Matrix client should instruct the user's browser to navigate to this endpoint in order to log in via CAS.",
        description = "The server MUST respond with an HTTP redirect to the CAS interface. The URI MUST include a service parameter giving "
            + "the path of the /login/cas/ticket endpoint (including the redirectUrl query parameter).",
        responses = {
            @ApiResponse(
                responseCode = "302",
                description = "A redirect to the SSO interface",
                content = @Content(
                    schema = @Schema(
                        implementation = EmptyResponse.class
                    )
                )
            )
        },
        tags = {
            "Session management"
        }
    )
    @GET
    @Path("/redirect")
    void redirect(
        @Parameter(
            description = "URI to which the user will be redirected after the homeserver has authenticated the user with CAS.",
            required = true
        ) @QueryParam("redirectUrl") String redirectUrl,

        @Context UriInfo uriInfo,
        @Context HttpHeaders httpHeaders,
        @Suspended AsyncResponse asyncResponse
    );
}
