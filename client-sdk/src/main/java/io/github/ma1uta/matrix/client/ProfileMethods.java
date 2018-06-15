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
import io.github.ma1uta.matrix.client.api.ProfileApi;
import io.github.ma1uta.matrix.client.model.profile.DisplayName;

import java.util.HashMap;
import java.util.Map;

/**
 * Profile methods.
 */
public class ProfileMethods {

    private final MatrixClient matrixClient;

    ProfileMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Set new display name.
     *
     * @param displayName display name.
     */
    public void setDisplayName(String displayName) {
        Map<String, String> params = new HashMap<>();
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        params.put("userId", requestMethods.getUserId());
        DisplayName request = new DisplayName();
        request.setDisplayname(displayName);
        requestMethods.put(ProfileApi.class, "setDisplayName", params, null, request, EmptyResponse.class);
    }
}
