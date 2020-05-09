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

import io.github.ma1uta.matrix.client.model.userdirectory.SearchRequest;
import io.github.ma1uta.matrix.client.model.userdirectory.SearchResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * User directory.
 * <br>
 * Provides search over all users.
 */
@Path("/_matrix/client/r0/user_directory")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserDirectoryApi {

    /**
     * Performs a search for users on the homeserver. The homeserver may determine which subset of users are searched, however
     * the homeserver MUST at a minimum consider the users the requesting user shares a room with and those who reside in public
     * rooms (known to the homeserver). The search MUST consider local users to the homeserver, and SHOULD query remote users as
     * part of the search.
     * <br>
     * The search is performed case-insensitively on user IDs and display names preferably using a collation determined based upon
     * the Accept-Language header provided in the request, if present.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The results of the search.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param request JSON body request.
     * @return {@link SearchResponse}.
     */
    @POST
    @Path("/search")
    SearchResponse searchUsers(
        SearchRequest request
    );
}
