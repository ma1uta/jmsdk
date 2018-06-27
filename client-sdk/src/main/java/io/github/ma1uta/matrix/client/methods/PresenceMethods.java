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
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.PresenceApi;
import io.github.ma1uta.matrix.client.model.presence.PresenceList;
import io.github.ma1uta.matrix.client.model.presence.PresenceStatus;

import java.util.List;

/**
 * Presence methods.
 */
public class PresenceMethods {

    private final MatrixClient matrixClient;

    public PresenceMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * This API sets the given user's presence state. When setting the status, the activity time is updated to reflect
     * that activity; the client does not need to specify the last_active_ago field. You cannot set the presence state of
     * another user.
     *
     * @param status the new presence status.
     */
    public void setPresenceStatus(PresenceStatus status) {
        String userId = getMatrixClient().getUserId();
        RequestParams params = new RequestParams().pathParam("userId", userId);
        getMatrixClient().getRequestMethods().put(PresenceApi.class, "setPresenceStatus", params, status, EmptyResponse.class);
    }

    /**
     * Get the given user's presence state.
     *
     * @param userId The user whose presence state to get.
     * @return The presence state for this user.
     */
    public PresenceStatus getPresenceStatus(String userId) {
        RequestParams params = new RequestParams().pathParam("userId", userId);
        return getMatrixClient().getRequestMethods().get(PresenceApi.class, "getPresenceStatus", params, PresenceStatus.class);
    }

    /**
     * Adds or removes users from this presence list.
     *
     * @param presenceList the presence list.
     */
    public void setPresenceList(PresenceList presenceList) {
        String userId = getMatrixClient().getUserId();
        RequestParams params = new RequestParams().pathParam("userId", userId);
        getMatrixClient().getRequestMethods().post(PresenceApi.class, "setPresenceList", params, presenceList, EmptyResponse.class);
    }

    /**
     * Retrieve a list of presence events for every user on this list.
     *
     * @param userId The user whose presence list should be retrieved.
     * @return A list of presence events for this list.
     */
    @SuppressWarnings("unchecked")
    public List<Event> getPresenceList(String userId) {
        RequestParams params = new RequestParams().pathParam("userId", userId);
        return getMatrixClient().getRequestMethods().get(PresenceApi.class, "getPresenceList", params, List.class);
    }
}
