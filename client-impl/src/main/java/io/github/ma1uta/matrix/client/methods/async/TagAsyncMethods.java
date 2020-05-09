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

package io.github.ma1uta.matrix.client.methods.async;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.ConnectionInfo;
import io.github.ma1uta.matrix.client.model.tag.Tags;
import io.github.ma1uta.matrix.client.rest.async.TagApi;
import io.github.ma1uta.matrix.event.nested.TagInfo;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * TagInfo methods.
 */
public class TagAsyncMethods {

    private final TagApi tagApi;

    private final ConnectionInfo connectionInfo;

    public TagAsyncMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.tagApi = restClientBuilder.build(TagApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * List the tags set by a user on a room.
     *
     * @param roomId The id of the room to get tags for.
     * @return The list of tags for the user for the room.
     */
    public CompletableFuture<Tags> show(String roomId) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        return tagApi.showTags(userId, roomId).toCompletableFuture();
    }

    /**
     * Add a tag to the room.
     *
     * @param roomId The id of the room to add a tag to.
     * @param tag    The tag to add.
     * @param order  The tag order.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> add(String roomId, String tag, Long order) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(tag, "TagInfo cannot be empty.");

        TagInfo tagInfo = new TagInfo();
        tagInfo.setOrder(order);

        return tagApi.addTag(userId, roomId, tag, tagInfo).toCompletableFuture();
    }

    /**
     * Remove a tag from the room.
     *
     * @param roomId The id of the room to remove a tag from.
     * @param tag    The tag to remove.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> delete(String roomId, String tag) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(tag, "TagContent cannot be empty.");

        return tagApi.deleteTag(userId, roomId, tag).toCompletableFuture();
    }
}
