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
import io.github.ma1uta.matrix.client.model.profile.AvatarUrl;
import io.github.ma1uta.matrix.client.model.profile.DisplayName;
import io.github.ma1uta.matrix.client.model.profile.Profile;
import io.github.ma1uta.matrix.client.rest.async.ProfileApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Profile methods.
 */
public class ProfileAsyncMethods {

    private final ProfileApi profileApi;

    private final ConnectionInfo connectionInfo;

    public ProfileAsyncMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.profileApi = restClientBuilder.build(ProfileApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * Set new display name.
     *
     * @param displayName A new display name.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> setDisplayName(String displayName) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        DisplayName request = new DisplayName();
        request.setDisplayName(displayName);

        return profileApi.setDisplayName(userId, request).toCompletableFuture();
    }

    /**
     * Get the user's display name. This API may be used to fetch the user's own displayname or to query the name of other users; either
     * locally or on remote homeservers.
     *
     * @param userId The user whose display name to get.
     * @return The display name for this user.
     */
    public CompletableFuture<DisplayName> showDisplayName(String userId) {
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        return profileApi.showDisplayName(userId).toCompletableFuture();
    }

    /**
     * This API sets the given user's avatar URL. You must have permission to set this user's avatar URL, e.g. you need to have
     * their access_token.
     *
     * @param avatarUrl The user whose avatar URL to set.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> setAvaterUrl(String avatarUrl) {
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        AvatarUrl request = new AvatarUrl();
        request.setAvatarUrl(avatarUrl);

        return profileApi.setAvatarUrl(userId, request).toCompletableFuture();
    }

    /**
     * Get the user's avatar URL. This API may be used to fetch the user's own avatar URL or to query the URL of other users;
     * either locally or on remote homeservers.
     *
     * @param userId The user whose avatar URL to get.
     * @return The avatar URL for this user.
     */
    public CompletableFuture<AvatarUrl> showAvatarUrl(String userId) {
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        return profileApi.showAvatarUrl(userId).toCompletableFuture();
    }

    /**
     * Get the combined profile information for this user. This API may be used to fetch the user's own profile information or
     * other users; either locally or on remote homeservers. This API may return keys which are not limited to displayname or avatar_url.
     *
     * @param userId The user whose profile information to get.
     * @return The profile info for this user.
     */
    public CompletableFuture<Profile> profile(String userId) {
        Objects.requireNonNull(userId, "UserId cannot be empty.");

        return profileApi.profile(userId).toCompletableFuture();
    }
}
