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

import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Client Behaviour.
 */
@Path("/_matrix/client/r0/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ClientConfigApi {

    /**
     * Get some account_data for the client. This config is only visible to the user that set the account_data.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link Map}.
     * <p>Status code 200: The account data content for the given type.</p>
     *
     * @param userId Required. The id of the user to set account_data for. The access token must be authorized to make
     *               requests for this user id.
     * @param type   Required. The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @return {@link Map}
     */
    @GET
    @Path("/{userId}/account_data/{type}")
    Map getConfig(
        @PathParam("userId") String userId,
        @PathParam("type") String type
    );

    /**
     * Set some account_data for the client. This config is only visible to the user that set the account_data. The config will be
     * synced to clients in the top-level account_data.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link EmptyResponse}.
     * <p>Status code 200: The account_data was successfully added.</p>
     *
     * @param userId      Required. The id of the user to set account_data for. The access token must be authorized to make
     *                    requests for this user id.
     * @param type        Required. The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData Account data.
     * @return {@link EmptyResponse}
     */
    @PUT
    @Path("/{userId}/account_data/{type}")
    EmptyResponse addConfig(
        @PathParam("userId") String userId,
        @PathParam("type") String type,
        Map<String, Object> accountData
    );

    /**
     * Get some account_data for the client on a given room. This config is only visible to the user that set the account_data.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link Map}.
     * <p>Status code 200: The account data content for the given type.</p>
     *
     * @param userId Required. The id of the user to set account_data for. The access token must be authorized to make requests for
     *               this user id.
     * @param roomId Required. The id of the room to set account_data on.
     * @param type   Required. The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @return {@link Map}.
     */
    @GET
    @Path("/{userId}/rooms/{roomId}/account_data/{type}")
    Map getRoomConfig(
        @PathParam("userId") String userId,
        @PathParam("roomId") String roomId,
        @PathParam("type") String type
    );

    /**
     * Set some account_data for the client on a given room. This config is only visible to the user that set the account_data.
     * The config will be synced to clients in the per-room account_data.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link EmptyResponse}.
     * <p>Status code 200: The account_data was successfully added.</p>
     *
     * @param userId      Required. The id of the user to set account_data for. The access token must be authorized to make requests for
     *                    this user id.
     * @param roomId      Required. The id of the room to set account_data on.
     * @param type        Required. The event type of the account_data to set. Custom types should be namespaced to avoid clashes.
     * @param accountData account data.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/{userId}/rooms/{roomId}/account_data/{type}")
    EmptyResponse addRoomConfig(
        @PathParam("userId") String userId,
        @PathParam("roomId") String roomId,
        @PathParam("type") String type,
        Map<String, Object> accountData
    );
}
