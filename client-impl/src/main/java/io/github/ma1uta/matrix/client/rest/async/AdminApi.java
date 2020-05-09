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

import io.github.ma1uta.matrix.client.model.admin.AdminResponse;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Gets information about a particular user.
 */
@Path("/_matrix/client/r0/admin")
@Produces(MediaType.APPLICATION_JSON)
public interface AdminApi {

    /**
     * This API may be restricted to only be called by the user being looked up, or by a server admin. Server-local administrator
     * privileges are not specified in this document.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link AdminResponse}.
     * <p>Status code 200: The lookup was successful.</p>
     *
     * @param userId Required. The user to look up.
     * @return {@link AdminResponse}
     */
    @GET
    @Path("/whois/{userId}")
    CompletionStage<AdminResponse> whois(
        @PathParam("userId") String userId
    );
}
