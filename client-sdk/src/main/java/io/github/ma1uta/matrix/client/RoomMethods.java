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

package io.github.ma1uta.matrix.client;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.api.RoomApi;
import io.github.ma1uta.matrix.client.model.room.JoinRequest;
import io.github.ma1uta.matrix.client.model.room.JoinedRoomsResponse;
import io.github.ma1uta.matrix.client.model.room.RoomId;

import java.util.List;

/**
 * Room api.
 */
public class RoomMethods {

    private final MatrixClient matrixClient;

    RoomMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Join to the room.
     *
     * @param idOrAlias room id or alias.
     * @return room id.
     */
    public RoomId joinRoomByIdOrAlias(String idOrAlias) {
        RequestParams params = new RequestParams().pathParam("roomIdOrAlias", idOrAlias);
        return getMatrixClient().getRequestMethods().post(RoomApi.class, "joinByIdOrAlias", params, new JoinRequest(), RoomId.class);
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
     * Leave room.
     *
     * @param roomId room id.
     */
    public void leaveRoom(String roomId) {
        RequestParams params = new RequestParams().pathParam("roomId", roomId);
        getMatrixClient().getRequestMethods().post(RoomApi.class, "leave", params, "", EmptyResponse.class);
    }
}
