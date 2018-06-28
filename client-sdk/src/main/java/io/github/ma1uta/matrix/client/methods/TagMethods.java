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
import io.github.ma1uta.matrix.client.api.TagApi;
import io.github.ma1uta.matrix.client.model.tag.Tags;

import java.util.Map;
import java.util.Objects;

/**
 * Tag methods.
 */
public class TagMethods {

    private final MatrixClient matrixClient;

    public TagMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * List the tags set by a user on a room.
     *
     * @param roomId The id of the room to get tags for.
     * @return The list of tags for the user for the room.
     */
    public Tags show(String roomId) {
        String userId = getMatrixClient().getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        RequestParams params = new RequestParams().pathParam("userId", userId).pathParam("roomId", roomId);
        return getMatrixClient().getRequestMethods().get(TagApi.class, "showTags", params, Tags.class);
    }

    /**
     * Add a tag to the room.
     *
     * @param roomId  The id of the room to add a tag to.
     * @param tag     The tag to add.
     * @param tagData the tag data.
     */
    public void add(String roomId, String tag, Map<String, String> tagData) {
        String userId = getMatrixClient().getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(tag, "Tag cannot be empty.");
        RequestParams params = new RequestParams().pathParam("userId", userId).pathParam("roomId", roomId).pathParam("tag", tag);
        getMatrixClient().getRequestMethods().put(TagApi.class, "addTag", params, tagData, EmptyResponse.class);
    }

    /**
     * Remove a tag from the room.
     *
     * @param roomId The id of the room to remove a tag from.
     * @param tag    The tag to remove.
     */
    public void delete(String roomId, String tag) {
        String userId = getMatrixClient().getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(tag, "Tag cannot be empty.");
        RequestParams params = new RequestParams().pathParam("userId", userId).pathParam("roomId", roomId).pathParam("tag", tag);
        getMatrixClient().getRequestMethods().delete(TagApi.class, "deleteTag", params, "");
    }
}
