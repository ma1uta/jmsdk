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
import io.github.ma1uta.matrix.client.api.TagApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.tag.Tags;
import io.github.ma1uta.matrix.event.nested.TagInfo;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * TagInfo methods.
 */
public class TagMethods extends AbstractMethods {

    public TagMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * List the tags set by a user on a room.
     *
     * @param roomId The id of the room to get tags for.
     * @return The list of tags for the user for the room.
     */
    public CompletableFuture<Tags> show(Id roomId) {
        Id userId = defaults().getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults()
            .clone().path("userId", userId.toString())
            .path("roomId", roomId.toString());
        return factory().get(TagApi.class, "showTags", params, Tags.class);
    }

    /**
     * Add a tag to the room.
     *
     * @param roomId The id of the room to add a tag to.
     * @param tag    The tag to add.
     * @param order  The tag order.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> add(Id roomId, String tag, Long order) {
        Id userId = defaults().getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(tag, "TagInfo cannot be empty.");

        TagInfo tagInfo = new TagInfo();
        tagInfo.setOrder(order);
        RequestParams params = defaults().clone()
            .path("userId", userId.toString())
            .path("roomId", roomId.toString())
            .path("tag", tag);
        return factory().put(TagApi.class, "addTag", params, tagInfo, EmptyResponse.class);
    }

    /**
     * Remove a tag from the room.
     *
     * @param roomId The id of the room to remove a tag from.
     * @param tag    The tag to remove.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> delete(Id roomId, String tag) {
        Id userId = defaults().getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(tag, "TagContent cannot be empty.");

        RequestParams params = defaults().clone()
            .path("userId", userId.toString())
            .path("roomId", roomId.toString())
            .path("tag", tag);
        return factory().delete(TagApi.class, "deleteTag", params);
    }
}
