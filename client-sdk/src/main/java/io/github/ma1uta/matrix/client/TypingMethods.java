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
import io.github.ma1uta.matrix.client.api.TypingApi;
import io.github.ma1uta.matrix.client.model.typing.TypingRequest;

/**
 * Typing methods.
 */
public class TypingMethods {

    private final MatrixClient matrixClient;

    TypingMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * This tells the server that the user is typing for the next N milliseconds where N is the value specified in the timeout key.
     * Alternatively, if typing is false, it tells the server that the user has stopped typing.
     *
     * @param roomId  The user who has started to type.
     * @param typing  Whether the user is typing or not. If false, the timeout key can be omitted.
     * @param timeout The length of time in milliseconds to mark this user as typing.
     */
    public void typing(String roomId, Boolean typing, Long timeout) {
        String userId = getMatrixClient().getUserId();
        RequestParams params = new RequestParams().pathParam("userId", userId).pathParam("roomId", roomId);
        TypingRequest request = new TypingRequest();
        request.setTyping(typing);
        request.setTimeout(timeout);
        getMatrixClient().getRequestMethods().put(TypingApi.class, "typing", params, request, EmptyResponse.class);
    }
}
