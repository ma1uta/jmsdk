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

import io.github.ma1uta.matrix.client.rest.async.ThirdPartyProtocolApi;
import io.github.ma1uta.matrix.protocol.Protocol;
import io.github.ma1uta.matrix.protocol.ProtocolLocation;
import io.github.ma1uta.matrix.protocol.ProtocolUser;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Protocol methods.
 */
public class ProtocolAsyncMethods {

    private final ThirdPartyProtocolApi thirdPartyProtocolApi;

    public ProtocolAsyncMethods(RestClientBuilder restClientBuilder) {
        this.thirdPartyProtocolApi = restClientBuilder.build(ThirdPartyProtocolApi.class);
    }

    /**
     * Fetches the overall metadata about protocols supported by the homeserver. Includes both the available protocols and all
     * fields required for queries against each protocol.
     *
     * @return The protocols map.
     */
    public CompletableFuture<Map<String, Protocol>> protocols() {
        return thirdPartyProtocolApi.protocols().toCompletableFuture();
    }

    /**
     * Fetches the metadata from the homeserver about a particular third party protocol.
     *
     * @param protocol Required. The name of the protocol.
     * @return The specified protocol.
     */
    public CompletableFuture<Protocol> protocol(String protocol) {
        Objects.requireNonNull(protocol, "Protocol cannot be empty.");

        return thirdPartyProtocolApi.protocol(protocol).toCompletableFuture();
    }


    /**
     * Requesting this endpoint with a valid protocol name results in a list of successful mapping results in a JSON array.
     * Each result contains objects to represent the Matrix room or rooms that represent a portal to this third party network.
     * Each has the Matrix room alias string, an identifier for the particular third party network protocol, and an object
     * containing the network-specific fields that comprise this identifier. It should attempt to canonicalise the identifier
     * as much as reasonably possible given the network type.
     *
     * @param protocol Required. The protocol used to communicate to the third party network.
     * @return Founded protocols.
     */
    public CompletableFuture<List<ProtocolLocation>> locations(String protocol) {
        Objects.requireNonNull(protocol, "Protocol cannot be empty.");

        return thirdPartyProtocolApi.location(protocol).toCompletableFuture();
    }

    /**
     * Retrieve a Matrix User ID linked to a user on the third party service, given a set of user parameters.
     *
     * @param protocol Required. The name of the protocol.
     * @return Founded users.
     */
    public CompletableFuture<List<ProtocolUser>> users(String protocol) {
        Objects.requireNonNull(protocol, "Protocol cannot be empty.");

        return thirdPartyProtocolApi.userProtocol(protocol).toCompletableFuture();
    }

    /**
     * Retrieve an array of third party network locations from a Matrix room alias.
     *
     * @param alias Required. The Matrix room alias to look up.
     * @return Founded location.
     */
    public CompletableFuture<List<ProtocolLocation>> location(String alias) {
        Objects.requireNonNull(alias, "Alias cannot be empty.");

        return thirdPartyProtocolApi.location(alias).toCompletableFuture();
    }

    /**
     * Retrieve an array of third party users from a Matrix User ID.
     *
     * @param userId Required. The Matrix User ID to look up.
     * @return Founded users.
     */
    public CompletableFuture<List<ProtocolUser>> user(String userId) {
        Objects.requireNonNull(userId, "userId cannot be empty.");

        return thirdPartyProtocolApi.user(userId).toCompletableFuture();
    }
}
