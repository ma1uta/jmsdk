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
import io.github.ma1uta.matrix.client.model.presence.PresenceRequest;
import io.github.ma1uta.matrix.client.model.presence.PresenceStatus;
import io.github.ma1uta.matrix.client.rest.async.PresenceApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Presence methods.
 */
public class PresenceAsyncMethods {

    private final PresenceApi presenceApi;

    private final ConnectionInfo connectionInfo;

    public PresenceAsyncMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.presenceApi = restClientBuilder.build(PresenceApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * This API sets the given user's presence state. When setting the status, the activity time is updated to reflect
     * that activity; the client does not need to specify the last_active_ago field. You cannot set the presence state of
     * another user.
     *
     * @param presence The new presence.
     * @param status   The new presence status.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setPresenceStatus(String presence, String status) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(presence, "Presence cannot be empty.");

        PresenceRequest request = new PresenceRequest();
        request.setPresence(presence);
        request.setStatusMsg(status);

        return presenceApi.setPresenceStatus(userId, request).toCompletableFuture();
    }

    /**
     * Get the given user's presence state.
     *
     * @param userId The user whose presence state to get.
     * @return The presence state for this user.
     */
    public CompletableFuture<PresenceStatus> getPresenceStatus(String userId) {
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        return presenceApi.getPresenceStatus(userId).toCompletableFuture();
    }
}
