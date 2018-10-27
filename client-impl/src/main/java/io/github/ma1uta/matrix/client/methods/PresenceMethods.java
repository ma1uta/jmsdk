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
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.PresenceApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.presence.PresenceList;
import io.github.ma1uta.matrix.client.model.presence.PresenceStatus;
import io.github.ma1uta.matrix.event.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Presence methods.
 */
public class PresenceMethods extends AbstractMethods {

    public PresenceMethods(RequestFactory factory,
                           RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * This API sets the given user's presence state. When setting the status, the activity time is updated to reflect
     * that activity; the client does not need to specify the last_active_ago field. You cannot set the presence state of
     * another user.
     *
     * @param status The new presence status
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setPresenceStatus(PresenceStatus status) {
        String userId = defaults().getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(status.getPresence(), "Presence cannot be empty.");

        RequestParams params = defaults().clone().path("userId", userId);
        return factory().put(PresenceApi.class, "setPresenceStatus", params, status, EmptyResponse.class);
    }

    /**
     * Get the given user's presence state.
     *
     * @param userId The user whose presence state to get.
     * @return The presence state for this user.
     */
    public CompletableFuture<PresenceStatus> getPresenceStatus(String userId) {
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("userId", userId);
        return factory().get(PresenceApi.class, "getPresenceStatus", params, PresenceStatus.class);
    }

    /**
     * Adds or removes users from this presence list.
     *
     * @param presenceList The presence list.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setPresenceList(PresenceList presenceList) {
        String userId = defaults().getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("userId", userId);
        return factory().post(PresenceApi.class, "setPresenceList", params, presenceList, EmptyResponse.class);
    }

    /**
     * Retrieve a list of presence events for every user on this list.
     *
     * @param userId The user whose presence list should be retrieved.
     * @return A list of presence events for this list.
     */
    public CompletableFuture<List<Event>> getPresenceList(String userId) {
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        RequestParams params = defaults().clone().path("userId", userId);
        return factory().get(PresenceApi.class, "getPresenceList", params, new ArrayList<Event>());
    }
}
