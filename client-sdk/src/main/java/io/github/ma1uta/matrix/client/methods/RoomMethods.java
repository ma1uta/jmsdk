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
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.RoomApi;
import io.github.ma1uta.matrix.client.model.room.CreateRoomRequest;
import io.github.ma1uta.matrix.client.model.room.InviteRequest;
import io.github.ma1uta.matrix.client.model.room.JoinRequest;
import io.github.ma1uta.matrix.client.model.room.JoinedRoomsResponse;
import io.github.ma1uta.matrix.client.model.room.KickRequest;
import io.github.ma1uta.matrix.client.model.room.PublicRoomsRequest;
import io.github.ma1uta.matrix.client.model.room.PublicRoomsResponse;
import io.github.ma1uta.matrix.client.model.room.RoomId;
import io.github.ma1uta.matrix.client.model.room.RoomVisibility;

import java.util.List;

/**
 * Room api.
 */
public class RoomMethods {

    private final MatrixClient matrixClient;

    public RoomMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Create a new room with various configuration options.
     *
     * @param request the create room request.
     * @return Information about the newly created room.
     */
    public RoomId create(CreateRoomRequest request) {
        return getMatrixClient().getRequestMethods().post(RoomApi.class, "create", new RequestParams(), request, RoomId.class);
    }

    /**
     * Create a new mapping from room alias to room ID.
     *
     * @param roomId The information of the room.
     * @param alias  The room alias to set.
     */
    public void newAlias(RoomId roomId, String alias) {
        RequestParams params = new RequestParams().pathParam("roomAlias", alias);
        getMatrixClient().getRequestMethods().put(RoomApi.class, "newAlias", params, roomId, EmptyResponse.class);
    }

    /**
     * Requests that the server resolve a room alias to a room ID.
     *
     * @param alias The room alias.
     * @return The room ID and other information for this alias.
     */
    public RoomId resolve(String alias) {
        RequestParams params = new RequestParams().pathParam("roomAlias", alias);
        return getMatrixClient().getRequestMethods().get(RoomApi.class, "resolve", params, RoomId.class);
    }

    /**
     * Remove a mapping of room alias to room ID.
     *
     * @param alias The room alias to remove.
     */
    public void delete(String alias) {
        RequestParams params = new RequestParams().pathParam("roomAlias", alias);
        getMatrixClient().getRequestMethods().delete(RoomApi.class, "delete", params, EmptyResponse.class);
    }

    /**
     * Get joined rooms.
     *
     * @return joined room ids.
     */
    public List<String> joinedRooms() {
        return getMatrixClient().getRequestMethods().get(RoomApi.class, "joinedRooms", new RequestParams(), JoinedRoomsResponse.class)
            .getJoinedRooms();
    }

    /**
     * This API invites a user to participate in a particular room. They do not start participating in the room until they actually
     * join the room.
     *
     * @param roomId  The room identifier (not alias) to which to invite the user.
     * @param request The invite information.
     */
    public void invite(String roomId, InviteRequest request) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "invite", params, request, EmptyResponse.class);
    }

    /**
     * This API starts a user participating in a particular room, if that user is allowed to participate in that room.
     *
     * @param roomId  The room identifier (not alias) to join.
     * @param request The join information.
     * @return The room has been joined. The joined room ID must be returned in the room_id field.
     */
    public RoomId join(String roomId, JoinRequest request) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        return getMatrixClient().getRequestMethods().post(RoomApi.class, "join", params, request, RoomId.class);
    }

    /**
     * Join to the room.
     *
     * @param idOrAlias room id or alias.
     * @return room id.
     */
    public RoomId joinByIdOrAlias(String idOrAlias) {
        RequestParams params = new RequestParams().pathParam("roomIdOrAlias", idOrAlias);
        return getMatrixClient().getRequestMethods().post(RoomApi.class, "joinByIdOrAlias", params, new JoinRequest(), RoomId.class);
    }

    /**
     * Leave room.
     *
     * @param roomId room id.
     */
    public void leave(String roomId) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "leave", params, "", EmptyResponse.class);
    }

    /**
     * This API stops a user remembering about a particular room.
     *
     * @param roomId The room identifier to forget.
     */
    public void forget(String roomId) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "forget", params, "", EmptyResponse.class);
    }

    /**
     * Kick a user from the room.
     *
     * @param roomId The room identifier (not alias) from which the user should be kicked.
     * @param userId The fully qualified user ID of the user being kicked.
     * @param reason The reason the user has been baned.
     */
    public void kick(String roomId, String userId, String reason) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        KickRequest request = new KickRequest();
        request.setUserId(userId);
        request.setReason(reason);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "kick", params, request, EmptyResponse.class);
    }

    /**
     * Ban a user in the room. If the user is currently in the room, also kick them.
     *
     * @param roomId The room identifier (not alias) from which the user should be banned.
     * @param userId The fully qualified user ID of the user being baned.
     * @param reason The reason the user has been kicked.
     */
    public void ban(String roomId, String userId, String reason) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        KickRequest request = new KickRequest();
        request.setUserId(userId);
        request.setReason(reason);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "ban", params, request, EmptyResponse.class);
    }

    /**
     * Unban a user from the room. This allows them to be invited to the room, and join if they would otherwise be allowed to join
     * according to its join rules.
     *
     * @param roomId The room identifier (not alias) from which the user should be unbanned.
     * @param userId The fully qualified user ID of the user being unbaned.
     * @param reason The reason the user has been unbaned.
     */
    public void unban(String roomId, String userId, String reason) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        KickRequest request = new KickRequest();
        request.setUserId(userId);
        request.setReason(reason);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "unban", params, request, EmptyResponse.class);
    }

    /**
     * Gets the visibility of a given room on the server's public room directory.
     *
     * @param roomId The room ID.
     * @return The visibility of the room in the directory.
     */
    public String getVisibility(String roomId) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        return getMatrixClient().getRequestMethods().get(RoomApi.class, "getVisibility", params, RoomVisibility.class).getVisibility();
    }

    /**
     * Sets the visibility of a given room in the server's public room directory.
     *
     * @param roomId     The room ID.
     * @param visibility The visibility of the room in the directory. One of: ["private", "public"].
     */
    public void setVisibility(String roomId, String visibility) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        RoomVisibility request = new RoomVisibility();
        request.setVisibility(visibility);
        getMatrixClient().getRequestMethods().put(RoomApi.class, "setVisibility", params, request, EmptyResponse.class);
    }

    /**
     * Lists the public rooms on the server.
     *
     * @param limit  Limit the number of results returned.
     * @param since  A pagination token from a previous request, allowing clients to get the next (or previous) batch of rooms.
     *               The direction of pagination is specified solely by which token is supplied, rather than via an explicit flag.
     * @param server The server to fetch the public room lists from. Defaults to the local server.
     * @return A list of the rooms on the server.
     */
    public PublicRoomsResponse showPublicRooms(Long limit, String since, String server) {
        RequestParams params = new RequestParams().queryParam("since", since).queryParam("server", server);
        if (limit != null) {
            params.queryParam("limit", Long.toString(limit));
        }
        return getMatrixClient().getRequestMethods().get(RoomApi.class, "showPublicRooms", params, PublicRoomsResponse.class);
    }

    /**
     * Lists the public rooms on the server, with optional filter.
     *
     * @param server  The server to fetch the public room lists from. Defaults to the local server.
     * @param request The search request.
     * @return A list of the rooms on the server.
     */
    public PublicRoomsResponse searchPublicRooms(String server, PublicRoomsRequest request) {
        RequestParams params = new RequestParams().queryParam("server", server);
        return getMatrixClient().getRequestMethods().post(RoomApi.class, "searchPublicRooms", params, request, PublicRoomsResponse.class);
    }
}
