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
import io.github.ma1uta.matrix.client.api.ProfileApi;
import io.github.ma1uta.matrix.client.model.profile.AvatarUrl;
import io.github.ma1uta.matrix.client.model.profile.DisplayName;
import io.github.ma1uta.matrix.client.model.profile.Profile;

/**
 * Profile methods.
 */
public class ProfileMethods {

    private final MatrixClient matrixClient;

    public ProfileMethods(MatrixClient matrixClient) {
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
        RequestMethods requestMethods = getMatrixClient().getRequestMethods();
        RequestParams params = new RequestParams().pathParam("userId", requestMethods.getUserId());
        DisplayName request = new DisplayName();
        request.setDisplayname(displayName);
        requestMethods.put(ProfileApi.class, "setDisplayName", params, request, EmptyResponse.class);
    }

    /**
     * Get the user's display name. This API may be used to fetch the user's own displayname or to query the name of other users; either
     * locally or on remote homeservers.
     *
     * @param userId The user whose display name to get.
     * @return The display name for this user.
     */
    public String showDisplayName(String userId) {
        RequestParams params = new RequestParams().pathParam("userId", userId);
        return getMatrixClient().getRequestMethods().get(ProfileApi.class, "showDisplayName", params, DisplayName.class).getDisplayname();
    }

    /**
     * This API sets the given user's avatar URL. You must have permission to set this user's avatar URL, e.g. you need to have
     * their access_token.
     *
     * @param avatarUrl The user whose avatar URL to set.
     */
    public void setAvaterUrl(String avatarUrl) {
        String userId = getMatrixClient().getUserId();
        RequestParams params = new RequestParams().pathParam("userId", userId);
        AvatarUrl request = new AvatarUrl();
        request.setAvatarUrl(avatarUrl);
        getMatrixClient().getRequestMethods().put(ProfileApi.class, "setAvatarUrl", params, request, EmptyResponse.class);
    }

    /**
     * Get the user's avatar URL. This API may be used to fetch the user's own avatar URL or to query the URL of other users;
     * either locally or on remote homeservers.
     *
     * @param userId The user whose avatar URL to get.
     * @return The avatar URL for this user.
     */
    public String showAvatarUrl(String userId) {
        RequestParams params = new RequestParams().pathParam("userId", userId);
        return getMatrixClient().getRequestMethods().get(ProfileApi.class, "showAvatarUrl", params, AvatarUrl.class).getAvatarUrl();
    }

    /**
     * Get the combined profile information for this user. This API may be used to fetch the user's own profile information or
     * other users; either locally or on remote homeservers. This API may return keys which are not limited to displayname or avatar_url.
     *
     * @param userId The user whose profile information to get.
     * @return The profile info for this user.
     */
    public Profile profile(String userId) {
        RequestParams params = new RequestParams().pathParam("userId", userId);
        return getMatrixClient().getRequestMethods().get(ProfileApi.class, "profile", params, Profile.class);
    }
}
