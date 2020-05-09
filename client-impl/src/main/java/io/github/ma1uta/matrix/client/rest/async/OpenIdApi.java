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

package io.github.ma1uta.matrix.client.rest.async;

import io.github.ma1uta.matrix.client.model.openid.OpenIdResponse;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This module allows users to verify their identity with a third party service. The third party service does need to be matrix-aware
 * in that it will need to know to resolve matrix homeservers to exchange the user's token for identity information.
 */
@Path("/_matrix/client/r0")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface OpenIdApi {

    /**
     * Gets an OpenID token object that the requester may supply to another service to verify their identity in Matrix.
     * The generated token is only valid for exchanging for user information from the federation API for OpenID.
     * <br>
     * The access token generated is only valid for the OpenID API.
     * It cannot be used to request another OpenID access token or call /sync, for example.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: OpenID token information. This response is nearly compatible with the response documented
     * in the OpenID 1.0 Specification with the only difference being the lack of an id_token. Instead, the Matrix homeserver's name
     * is provided.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param userId Required. The user to request and OpenID token for. Should be the user who is authenticated for the request.
     * @return {@link OpenIdResponse}.
     */
    @POST
    @Path("/user/{userId}/openid/request_token")
    CompletionStage<OpenIdResponse> requestToken(
        @PathParam("userId") String userId
    );
}
