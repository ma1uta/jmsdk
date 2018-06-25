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

import io.github.ma1uta.matrix.client.api.EncryptionApi;
import io.github.ma1uta.matrix.client.model.encryption.ChangesResponse;
import io.github.ma1uta.matrix.client.model.encryption.ClaimRequest;
import io.github.ma1uta.matrix.client.model.encryption.ClaimResponse;
import io.github.ma1uta.matrix.client.model.encryption.QueryRequest;
import io.github.ma1uta.matrix.client.model.encryption.QueryResponse;
import io.github.ma1uta.matrix.client.model.encryption.UploadRequest;
import io.github.ma1uta.matrix.client.model.encryption.UploadResponse;

/**
 * Admin methods.
 */
public class EncryptionMethods {

    private final MatrixClient matrixClient;

    EncryptionMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Publishes end-to-end encryption keys for the device.
     *
     * @param request devices key to publish.
     * @return For each key algorithm, the number of unclaimed one-time keys of that type currently held on the server for this device.
     */
    public UploadResponse uploadKey(UploadRequest request) {
        return getMatrixClient().getRequestMethods()
            .post(EncryptionApi.class, "uploadKey", new RequestParams(), request, UploadResponse.class);
    }

    /**
     * Returns the current devices and identity keys for the given users.
     *
     * @param request query request.
     * @return query result.
     */
    public QueryResponse query(QueryRequest request) {
        return getMatrixClient().getRequestMethods().post(EncryptionApi.class, "query", new RequestParams(), request, QueryResponse.class);
    }

    /**
     * Claims one-time keys for use in pre-key messages.
     *
     * @param request claim request.
     * @return claim response.
     */
    public ClaimResponse claim(ClaimRequest request) {
        return getMatrixClient().getRequestMethods().post(EncryptionApi.class, "claim", new RequestParams(), request, ClaimResponse.class);
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
    public ChangesResponse changes(String from, String to) {
        RequestParams params = new RequestParams().queryParam("from", from).queryParam("to", to);
        return getMatrixClient().getRequestMethods().get(EncryptionApi.class, "changes", params, ChangesResponse.class);
    }
}
