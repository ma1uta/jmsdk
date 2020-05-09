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
import io.github.ma1uta.matrix.client.model.typing.TypingRequest;
import io.github.ma1uta.matrix.client.rest.async.TypingApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Typing methods.
 */
public class TypingAsyncMethods {

    private final TypingApi typingApi;

    private final ConnectionInfo connectionInfo;

    public TypingAsyncMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.typingApi = restClientBuilder.build(TypingApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * This tells the server that the user is typing for the next N milliseconds where N is the value specified in the timeout key.
     * Alternatively, if typing is false, it tells the server that the user has stopped typing.
     *
     * @param roomId  The user who has started to type.
     * @param typing  Whether the user is typing or not. If false, the timeout key can be omitted.
     * @param timeout The length of time in milliseconds to mark this user as typing.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> typing(String roomId, Boolean typing, Long timeout) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(userId, "UserId cannot be empty.");
        Objects.requireNonNull(typing, "Typing cannot be empty.");

        TypingRequest request = new TypingRequest();
        request.setTyping(typing);
        request.setTimeout(timeout);

        return typingApi.typing(roomId, userId, request).toCompletableFuture();
    }
}
