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

import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.ThirdPartyProtocolApi;
import io.github.ma1uta.matrix.protocol.Protocol;
import io.github.ma1uta.matrix.protocol.ProtocolLocation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.core.GenericType;

/**
 * Protocol methods.
 */
public class ProtocolMethods {

    private final MatrixClient matrixClient;

    public ProtocolMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Fetches the overall metadata about protocols supported by the homeserver. Includes both the available protocols and all
     * fields required for queries against each protocol.
     *
     * @return <p>Status code 200: The protocols supported by the homeserver.</p>
     */
    public Map<String, Protocol> protocols() {
        return getMatrixClient().getRequestMethods()
            .get(ThirdPartyProtocolApi.class, "protocols", new RequestParams(), new GenericType<Map<String, Protocol>>() {
            });
    }

    /**
     * Fetches the metadata from the homeserver about a particular third party protocol.
     *
     * @param protocol Required. The name of the protocol.
     * @return <p>Status code 200: The protocol was found and metadata returned.</p>
     * <p>Status code 404: The protocol is unknown.</p>
     */
    public Protocol protocol(String protocol) {
        Objects.requireNonNull(protocol, "Protocol cannot be empty.");
        RequestParams params = new RequestParams().pathParam("protocol", protocol);
        return getMatrixClient().getRequestMethods().get(ThirdPartyProtocolApi.class, "protocol", params, Protocol.class);
    }


    /**
     * Requesting this endpoint with a valid protocol name results in a list of successful mapping results in a JSON array.
     * Each result contains objects to represent the Matrix room or rooms that represent a portal to this third party network.
     * Each has the Matrix room alias string, an identifier for the particular third party network protocol, and an object
     * containing the network-specific fields that comprise this identifier. It should attempt to canonicalise the identifier
     * as much as reasonably possible given the network type.
     *
     * @param protocol    Required. The protocol used to communicate to the third party network.
     * @param queryParams query params.
     * @return <p>Status code 200: At least one portal room was found.</p>
     * <p>Status code 404: No portal rooms were found.</p>
     */
    public List<Protocol> locations(String protocol, Map<String, String> queryParams) {
        Objects.requireNonNull(protocol, "Protocol cannot be empty.");
        RequestParams params = new RequestParams().pathParam("protocol", protocol);
        params.getQueryParams().putAll(queryParams);
        return getMatrixClient().getRequestMethods()
            .get(ThirdPartyProtocolApi.class, "locationProtocol", params, new GenericType<List<Protocol>>() {
            });
    }

    /**
     * Retrieve a Matrix User ID linked to a user on the third party service, given a set of user parameters.
     *
     * @param protocol    Required. The name of the protocol.
     * @param queryParams query params.
     * @return <p>Status code 200: The Matrix User IDs found with the given parameters.</p>
     * <p>Status code 404: The Matrix User ID was not found.</p>
     */
    public List<Protocol> users(String protocol, Map<String, String> queryParams) {
        Objects.requireNonNull(protocol, "Protocol cannot be empty.");
        RequestParams params = new RequestParams().pathParam("protocol", protocol);
        params.getQueryParams().putAll(queryParams);
        return getMatrixClient().getRequestMethods()
            .get(ThirdPartyProtocolApi.class, "userProtocol", params, new GenericType<List<Protocol>>() {
            });
    }

    /**
     * Retrieve an array of third party network locations from a Matrix room alias.
     *
     * @param alias Required. The Matrix room alias to look up.
     * @return <p>Status code 200: At least one portal room was found.</p>
     * <p>Status code 404: No portal rooms were found.</p>
     */
    public List<ProtocolLocation> location(String alias) {
        Objects.requireNonNull(alias, "Alias cannot be empty.");
        RequestParams params = new RequestParams().queryParam("alias", alias);
        return getMatrixClient().getRequestMethods()
            .get(ThirdPartyProtocolApi.class, "location", params, new GenericType<List<ProtocolLocation>>() {
            });
    }

    /**
     * Retreive an array of third party users from a Matrix User ID.
     *
     * @param userId Required. The Matrix User ID to look up.
     * @return <p>Status code 200: The Matrix User IDs found with the given parameters.</p>
     * <p>Status code 404: The Matrix User ID was not found.</p>
     */
    public List<ProtocolLocation> user(String userId) {
        Objects.requireNonNull(userId, "userId cannot be empty.");
        RequestParams params = new RequestParams().queryParam("userid", userId);
        return getMatrixClient().getRequestMethods()
            .get(ThirdPartyProtocolApi.class, "user", params, new GenericType<List<ProtocolLocation>>() {
            });
    }
}
