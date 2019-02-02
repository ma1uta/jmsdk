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
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.RoomApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.room.CreateRoomRequest;
import io.github.ma1uta.matrix.client.model.room.InviteRequest;
import io.github.ma1uta.matrix.client.model.room.JoinRequest;
import io.github.ma1uta.matrix.client.model.room.JoinedRoomsResponse;
import io.github.ma1uta.matrix.client.model.room.KickRequest;
import io.github.ma1uta.matrix.client.model.room.NewVersion;
import io.github.ma1uta.matrix.client.model.room.PublicRoomsRequest;
import io.github.ma1uta.matrix.client.model.room.PublicRoomsResponse;
import io.github.ma1uta.matrix.client.model.room.ReplacementRoom;
import io.github.ma1uta.matrix.client.model.room.RoomId;
import io.github.ma1uta.matrix.client.model.room.RoomVisibility;
import io.github.ma1uta.matrix.client.model.room.UnbanRequest;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Room api.
 */
public class RoomMethods extends AbstractMethods {

    public RoomMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Create a new room with various configuration options.
     *
     * @param request The create room request.
     * @return Information about the newly created room.
     */
    public CompletableFuture<RoomId> create(CreateRoomRequest request) {
        return factory().post(RoomApi.class, "create", defaults(), request, RoomId.class);
    }

    /**
     * Create a new mapping from room alias to room ID.
     *
     * @param roomId The information of the room.
     * @param alias  The room alias to set.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> createAlias(RoomId roomId, Id alias) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(alias, "Alias cannot be empty.");

        RequestParams params = defaults().clone().path("roomAlias", alias.toString());
        return factory().put(RoomApi.class, "createAlias", params, roomId, EmptyResponse.class);
    }

    /**
     * Requests that the server resolve a room alias to a room ID.
     *
     * @param alias The room alias.
     * @return The room ID and other information for this alias.
     */
    public CompletableFuture<RoomId> resolveAlias(Id alias) {
        Objects.requireNonNull(alias, "Alias cannot be empty.");

        RequestParams params = defaults().clone().path("roomAlias", alias.toString());
        return factory().get(RoomApi.class, "resolveAlias", params, RoomId.class);
    }

    /**
     * Remove a mapping of room alias to room ID.
     *
     * @param alias The room alias to remove.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> delete(Id alias) {
        Objects.requireNonNull(alias, "Alias cannot be empty.");

        RequestParams params = defaults().clone().path("roomAlias", alias.toString());
        return factory().delete(RoomApi.class, "deleteAlias", params);
    }

    /**
     * Get joined rooms.
     *
     * @return Joined room ids.
     */
    public CompletableFuture<List<Id>> joinedRooms() {
        return factory().get(RoomApi.class, "joinedRooms", defaults(), JoinedRoomsResponse.class)
            .thenApply(JoinedRoomsResponse::getJoinedRooms);
    }

    /**
     * This API invites a user to participate in a particular room. They do not start participating in the room until they actually
     * join the room.
     *
     * @param roomId  The room identifier (not alias) to which to invite the user.
     * @param request The invite information.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> invite(Id roomId, InviteRequest request) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(request.getIdServer(), "IdServer cannot be empty.");
        Objects.requireNonNull(request.getAddress(), "Address cannot be empty.");
        Objects.requireNonNull(request.getMedium(), "Medium cannot be empty.");
        Objects.requireNonNull(request.getUserId(), "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        return factory().post(RoomApi.class, "invite", params, request, EmptyResponse.class);
    }

    /**
     * This API starts a user participating in a particular room, if that user is allowed to participate in that room.
     *
     * @param roomId  The room identifier (not alias) to join.
     * @param request The join information.
     * @return The room has been joined. The joined room ID must be returned in the room_id field.
     */
    public CompletableFuture<RoomId> joinById(Id roomId, JoinRequest request) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        return factory().post(RoomApi.class, "joinById", params, request, RoomId.class);
    }

    /**
     * Join to the room.
     *
     * @param idOrAlias The room id or alias.
     * @return The joined room id.
     */
    public CompletableFuture<RoomId> joinByIdOrAlias(Id idOrAlias) {
        Objects.requireNonNull(idOrAlias, "IdOrAlias cannot be empty.");

        RequestParams params = defaults().clone().path("roomIdOrAlias", idOrAlias.toString());
        return factory().post(RoomApi.class, "joinByIdOrAlias", params, new JoinRequest(), RoomId.class);
    }

    /**
     * Leave room.
     *
     * @param roomId The room id.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> leave(Id roomId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        return factory().post(RoomApi.class, "leave", params, "", EmptyResponse.class);
    }

    /**
     * This API stops a user remembering about a particular room.
     *
     * @param roomId The room identifier to forget.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> forget(Id roomId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        return factory().post(RoomApi.class, "forget", params, "", EmptyResponse.class);
    }

    /**
     * Kick a user from the room.
     *
     * @param roomId The room identifier (not alias) from which the user should be kicked.
     * @param userId The fully qualified user ID of the user being kicked.
     * @param reason The reason the user has been baned.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> kick(Id roomId, Id userId, String reason) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        KickRequest request = new KickRequest();
        request.setUserId(userId);
        request.setReason(reason);
        return factory().post(RoomApi.class, "kick", params, request, EmptyResponse.class);
    }

    /**
     * Ban a user in the room. If the user is currently in the room, also kick them.
     *
     * @param roomId The room identifier (not alias) from which the user should be banned.
     * @param userId The fully qualified user ID of the user being baned.
     * @param reason The reason the user has been kicked.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> ban(Id roomId, Id userId, String reason) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        KickRequest request = new KickRequest();
        request.setUserId(userId);
        request.setReason(reason);
        return factory().post(RoomApi.class, "ban", params, request, EmptyResponse.class);
    }

    /**
     * Unban a user from the room. This allows them to be invited to the room, and join if they would otherwise be allowed to join
     * according to its join rules.
     *
     * @param roomId The room identifier (not alias) from which the user should be unbanned.
     * @param userId The fully qualified user ID of the user being unbaned.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> unban(Id roomId, Id userId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        UnbanRequest request = new UnbanRequest();
        request.setUserId(userId);
        return factory().post(RoomApi.class, "unban", params, request, EmptyResponse.class);
    }

    /**
     * Gets the visibility of a given room on the server's public room directory.
     *
     * @param roomId The room ID.
     * @return The visibility of the room in the directory.
     */
    public CompletableFuture<String> getVisibility(Id roomId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        return factory().get(RoomApi.class, "getVisibility", params, RoomVisibility.class).thenApply(RoomVisibility::getVisibility);
    }

    /**
     * Sets the visibility of a given room in the server's public room directory.
     *
     * @param roomId     The room ID.
     * @param visibility The visibility of the room in the directory. One of: ["private", "public"].
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setVisibility(Id roomId, String visibility) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        RoomVisibility request = new RoomVisibility();
        request.setVisibility(visibility);
        return factory().put(RoomApi.class, "setVisibility", params, request, EmptyResponse.class);
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
    public CompletableFuture<PublicRoomsResponse> showPublicRooms(Long limit, String since, String server) {
        RequestParams params = defaults().clone().query("since", since).query("server", server).query("limit", limit);
        return factory().get(RoomApi.class, "showPublicRooms", params, PublicRoomsResponse.class);
    }

    /**
     * Lists the public rooms on the server, with optional filter.
     *
     * @param server  The server to fetch the public room lists from. Defaults to the local server.
     * @param request The search request.
     * @return A list of the rooms on the server.
     */
    public CompletableFuture<PublicRoomsResponse> searchPublicRooms(String server, PublicRoomsRequest request) {
        RequestParams params = defaults().clone().query("server", server);
        return factory().post(RoomApi.class, "searchPublicRooms", params, request, PublicRoomsResponse.class);
    }

    /**
     * Upgrades the given room to a particular room version, migrating as much data as possible over to the new room.
     *
     * @param roomId     The ID of the room to upgrade.
     * @param newVersion The new version for the room.
     * @return The ID of the new room.
     */
    public CompletableFuture<ReplacementRoom> upgrade(Id roomId, String newVersion) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(newVersion, "New version cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId.toString());
        NewVersion request = new NewVersion();
        request.setNewVersion(newVersion);
        return factory().post(RoomApi.class, "upgrade", params, request, ReplacementRoom.class);
    }
}
