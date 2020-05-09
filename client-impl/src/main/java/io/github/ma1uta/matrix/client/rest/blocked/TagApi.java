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
import io.github.ma1uta.matrix.client.model.tag.Tags;
import io.github.ma1uta.matrix.event.nested.TagInfo;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Users can add tags to rooms. Tags are short strings used to label rooms, e.g. "work", "family". A room may have multiple tags.
 * Tags are only visible to the user that set them but are shared across all their devices.
 */
@Path("/_matrix/client/r0/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TagApi {

    /**
     * List the tags set by a user on a room.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The list of tags for the user for the room.</p>
     *
     * @param userId Required. The id of the user to get tags for. The access token must be authorized to make requests for this
     *               user id.
     * @param roomId Required. The id of the room to get tags for.
     * @return {@link Tags}.
     */
    @GET
    @Path("/{userId}/rooms/{roomId}/tags")
    Tags showTags(
        @PathParam("userId") String userId,
        @PathParam("roomId") String roomId
    );

    /**
     * Add a tag to the room.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The tag was successfully added.</p>
     *
     * @param userId  Required. The id of the user to add a tag for. The access token must be authorized to make requests for this
     *                user id.
     * @param roomId  Required. The id of the room to add a tag to.
     * @param tag     Required. The tag to add.
     * @param tagData tag data.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/{userId}/rooms/{roomId}/tags/{tag}")
    EmptyResponse addTag(
        @PathParam("userId") String userId,
        @PathParam("roomId") String roomId,
        @PathParam("tag") String tag,
        TagInfo tagData
    );

    /**
     * Remove a tag from the room.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The tag was successfully removed.</p>
     *
     * @param userId Required. The id of the user to remove a tag for. The access token must be authorized to make requests
     *               for this user id.
     * @param roomId Required. The id of the room to remove a tag from.
     * @param tag    Required. The tag to remove.
     * @return {@link EmptyResponse}.
     */
    @DELETE
    @Path("/{userId}/rooms/{roomId}/tags/{tag}")
    EmptyResponse deleteTag(
        @PathParam("userId") String userId,
        @PathParam("roomId") String roomId,
        @PathParam("tag") String tag
    );
}
