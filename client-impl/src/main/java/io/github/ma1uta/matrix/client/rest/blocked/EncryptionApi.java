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

package io.github.ma1uta.matrix.client.rest.blocked;

import io.github.ma1uta.matrix.client.model.encryption.ChangesResponse;
import io.github.ma1uta.matrix.client.model.encryption.ClaimRequest;
import io.github.ma1uta.matrix.client.model.encryption.ClaimResponse;
import io.github.ma1uta.matrix.client.model.encryption.QueryRequest;
import io.github.ma1uta.matrix.client.model.encryption.QueryResponse;
import io.github.ma1uta.matrix.client.model.encryption.UploadRequest;
import io.github.ma1uta.matrix.client.model.encryption.UploadResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Matrix optionally supports end-to-end encryption, allowing rooms to be created whose conversation contents is not decryptable or
 * interceptable on any of the participating homeservers.
 */
@Path("/_matrix/client/r0/keys")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EncryptionApi {

    /**
     * Publishes end-to-end encryption keys for the device.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The provided keys were sucessfully uploaded.</p>
     *
     * @param uploadRequest JSON body parameters.
     * @return {@link UploadResponse}.
     */
    @POST
    @Path("/upload")
    UploadResponse uploadKey(
        UploadRequest uploadRequest
    );

    /**
     * Returns the current devices and identity keys for the given users.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The device information.</p>
     *
     * @param queryRequest JSON body parameters.
     * @return {@link QueryResponse}.
     */
    @POST
    @Path("/query")
    QueryResponse query(
        QueryRequest queryRequest
    );

    /**
     * Claims one-time keys for use in pre-key messages.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The claimed keys.</p>
     *
     * @param claimRequest JSON body parameters.
     * @return {@link ClaimResponse}.
     */
    @POST
    @Path("/claim")
    ClaimResponse claim(
        ClaimRequest claimRequest
    );

    /**
     * Gets a list of users who have updated their device identity keys since a previous sync token.
     * <br>
     * The server should include in the results any users who:
     * <ul>
     * <li>currently share a room with the calling user (ie, both users have membership state join); and</li>
     * <li>added new device identity keys or removed an existing device with identity keys, between from and to.</li>
     * </ul>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The list of users who updated their devices.</p>
     *
     * @param from Required. The desired start point of the list. Should be the next_batch field from a response to an earlier
     *             call to /sync. Users who have not uploaded new device identity keys since this point, nor deleted existing
     *             devices with identity keys since then, will be excluded from the results.
     * @param to   Required. The desired end point of the list. Should be the next_batch field from a recent call to /sync -
     *             typically the most recent such call. This may be used by the server as a hint to check its caches are up to
     *             date.
     * @return {@link ChangesResponse}.
     */
    @GET
    @Path("/changes")
    ChangesResponse changes(
        @QueryParam("from") String from,
        @QueryParam("to") String to
    );
}
