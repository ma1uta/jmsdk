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
import io.github.ma1uta.matrix.client.model.presence.PresenceRequest;
import io.github.ma1uta.matrix.client.model.presence.PresenceStatus;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Each user has the concept of presence information. This encodes:
 * <ul>
 * <li>Whether the user is currently online</li>
 * <li>How recently the user was last active (as seen by the server)</li>
 * <li>Whether a given client considers the user to be currently idle</li>
 * <li>Arbitrary information about the user's current status (e.g. "in a meeting").</li>
 * </ul>
 * <br>
 * This information is collated from both per-device (online, idle, last_active) and per-user (status) data, aggregated
 * by the user's homeserver and transmitted as an m.presence event. This is one of the few events which are sent outside
 * the context of a room. PresenceContent events are sent to all users who subscribe to this user's presence through a presence
 * list or by sharing membership of a room.
 * <br>
 * A presence list is a list of user IDs whose presence the user wants to follow. To be added to this list, the user being
 * added must be invited by the list owner who must accept the invitation.
 * <br>
 * User's presence state is represented by the presence key, which is an enum of one of the following:
 * <ul>
 * <li>online : The default state when the user is connected to an event stream.</li>
 * <li>unavailable : The user is not reachable at this time e.g. they are idle.</li>
 * <li>offline : The user is not connected to an event stream or is explicitly suppressing their profile information from being sent.</li>
 * </ul>
 */
@Path("/_matrix/client/r0/presence")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface PresenceApi {

    /**
     * This API sets the given user's presence state. When setting the status, the activity time is updated to reflect
     * that activity; the client does not need to specify the last_active_ago field. You cannot set the presence state of
     * another user.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The new presence state was set.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param userId  Required. The user whose presence state to update.
     * @param request JSON body request.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/{userId}/status")
    EmptyResponse setPresenceStatus(
        @PathParam("userId") String userId,
        PresenceRequest request
    );

    /**
     * Get the given user's presence state.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <p>Status code 200: The presence state for this user.</p>
     * <p>Status code 403: You are not allowed to see this user's presence status.</p>
     * <p>Status code 404: There is no presence state for this user. This user may not exist or isn't exposing presence information
     * to you.</p>
     *
     * @param userId Required. The user whose presence state to get.
     * @return {@link PresenceStatus}.
     */
    @GET
    @Path("/{userId}/status")
    PresenceStatus getPresenceStatus(
        @PathParam("userId") String userId
    );
}
