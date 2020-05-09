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

import io.github.ma1uta.matrix.Secured;
import io.github.ma1uta.matrix.protocol.Protocol;
import io.github.ma1uta.matrix.protocol.ProtocolLocation;
import io.github.ma1uta.matrix.protocol.ProtocolUser;

import java.util.List;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Application services can provide access to third party networks via bridging. This allows Matrix users to communicate with users
 * on other communication platforms, with messages ferried back and forth by the application service. A single application service
 * may bridge multiple third party networks, and many individual locations within those networks. A single third party network
 * location may be bridged to multiple Matrix rooms.
 */
@Path("/_matrix/client/r0/thirdparty")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ThirdPartyProtocolApi {

    /**
     * Fetches the overall metadata about protocols supported by the homeserver. Includes both the available protocols and all
     * fields required for queries against each protocol.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The protocols supported by the homeserver.</p>
     *
     * @return {@link Map}.
     */
    @GET
    @Path("/protocols")
    Map<String, Protocol> protocols();

    /**
     * Fetches the metadata from the homeserver about a particular third party protocol.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The protocol was found and metadata returned.</p>
     * <p>Status code 404: The protocol is unknown.</p>
     *
     * @param protocol Required. The name of the protocol.
     * @return {@link Protocol}.
     */
    @GET
    @Secured
    @Path("/protocol/{protocol}")
    Protocol protocol(
        @PathParam("protocol") String protocol
    );

    /**
     * Requesting this endpoint with a valid protocol name results in a list of successful mapping results in a JSON array.
     * Each result contains objects to represent the Matrix room or rooms that represent a portal to this third party network.
     * Each has the Matrix room alias string, an identifier for the particular third party network protocol, and an object
     * containing the network-specific fields that comprise this identifier. It should attempt to canonicalise the identifier
     * as much as reasonably possible given the network type.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: At least one portal room was found.</p>
     * <p>Status code 404: No portal rooms were found.</p>
     *
     * @param protocol Required. The protocol used to communicate to the third party network.
     * @return {@link List} of the {@link ProtocolLocation}.
     */
    @GET
    @Path("/location/{protocol}")
    List<ProtocolLocation> locationProtocol(
        @PathParam("protocol") String protocol
    );

    /**
     * Retrieve a Matrix User ID linked to a user on the third party service, given a set of user parameters.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The Matrix User IDs found with the given parameters.</p>
     * <p>Status code 404: The Matrix User ID was not found.</p>
     *
     * @param protocol Required. The name of the protocol.
     * @return {@link List} of the {@link ProtocolUser}s.
     */
    @GET
    @Path("/user/{protocol}")
    List<ProtocolUser> userProtocol(
        @PathParam("protocol") String protocol
    );

    /**
     * Retrieve an array of third party network locations from a Matrix room alias.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: At least one portal room was found.</p>
     * <p>Status code 404: No portal rooms were found.</p>
     *
     * @param alias Required. The Matrix room alias to look up.
     * @return {@link List} of the {@link ProtocolLocation}s.
     */
    @GET
    @Path("/location")
    List<ProtocolLocation> location(
        @QueryParam("alias") String alias
    );

    /**
     * Retrieve an array of third party users from a Matrix User ID.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The Matrix User IDs found with the given parameters.</p>
     * <p>Status code 404: The Matrix User ID was not found.</p>
     *
     * @param userId Required. The Matrix User ID to look up.
     * @return {@link List} of the {@link ProtocolUser}s.
     */
    @GET
    @Path("/user")
    List<ProtocolUser> user(
        @QueryParam("userid") String userId
    );
}
