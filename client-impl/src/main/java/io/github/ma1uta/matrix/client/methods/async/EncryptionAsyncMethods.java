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

import io.github.ma1uta.matrix.client.model.encryption.ChangesResponse;
import io.github.ma1uta.matrix.client.model.encryption.ClaimRequest;
import io.github.ma1uta.matrix.client.model.encryption.ClaimResponse;
import io.github.ma1uta.matrix.client.model.encryption.QueryRequest;
import io.github.ma1uta.matrix.client.model.encryption.QueryResponse;
import io.github.ma1uta.matrix.client.model.encryption.UploadRequest;
import io.github.ma1uta.matrix.client.model.encryption.UploadResponse;
import io.github.ma1uta.matrix.client.rest.async.EncryptionApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Admin methods.
 */
public class EncryptionAsyncMethods {

    private final EncryptionApi encryptionApi;

    public EncryptionAsyncMethods(RestClientBuilder restClientBuilder) {
        this.encryptionApi = restClientBuilder.build(EncryptionApi.class);
    }

    /**
     * Publishes end-to-end encryption keys for the device.
     *
     * @param request The devices key to publish.
     * @return For each key algorithm, the number of unclaimed one-time keys of that type currently held on the server for this device.
     */
    public CompletableFuture<UploadResponse> uploadKey(UploadRequest request) {
        return encryptionApi.uploadKey(request).toCompletableFuture();
    }

    /**
     * Returns the current devices and identity keys for the given users.
     *
     * @param request The query request.
     * @return The query result.
     */
    public CompletableFuture<QueryResponse> query(QueryRequest request) {
        if (request.getDeviceKeys() == null || request.getDeviceKeys().isEmpty()) {
            throw new NullPointerException("DeviceKeys cannot be empty.");
        }
        return encryptionApi.query(request).toCompletableFuture();
    }

    /**
     * Claims one-time keys for use in pre-key messages.
     *
     * @param request The claim request.
     * @return The claim response.
     */
    public CompletableFuture<ClaimResponse> claim(ClaimRequest request) {
        if (request.getOneTimeKeys().isEmpty()) {
            throw new NullPointerException("OneTimeKeys cannot be empty.");
        }
        return encryptionApi.claim(request).toCompletableFuture();
    }

    /**
     * Gets a list of users who have updated their device identity keys since a previous sync token.
     *
     * @param from The desired start point of the list. Should be the next_batch field from a response to an earlier
     *             call to /sync. Users who have not uploaded new device identity keys since this point, nor deleted existing
     *             devices with identity keys since then, will be excluded from the results.
     * @param to   The desired end point of the list. Should be the next_batch field from a recent call to /sync -
     *             typically the most recent such call. This may be used by the server as a hint to check its caches are up to
     *             date.
     * @return The list of users who updated their devices.
     */
    public CompletableFuture<ChangesResponse> changes(String from, String to) {
        Objects.requireNonNull(from, "From cannot be empty.");
        Objects.requireNonNull(to, "To cannot be empty.");

        return encryptionApi.changes(from, to).toCompletableFuture();
    }
}
