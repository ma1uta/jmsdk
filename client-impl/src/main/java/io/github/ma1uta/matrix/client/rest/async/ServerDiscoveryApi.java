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

package io.github.ma1uta.matrix.client.rest.async;

import io.github.ma1uta.matrix.client.model.serverdiscovery.ServerDiscoveryResponse;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * In order to allow users to connect to a Matrix server without needing to explicitly specify the homeserver's URL or other parameters,
 * clients SHOULD use an auto-discovery mechanism to determine the server's URL based on a user's Matrix ID. Auto-discovery should only
 * be done at login time.
 */
@Path("/.well-known/matrix/client")
@Produces(MediaType.APPLICATION_JSON)
public interface ServerDiscoveryApi {

    /**
     * Gets discovery information about the domain. The file may include additional keys, which MUST follow the Java package naming
     * convention, e.g. com.example.myapp.property. This ensures property names are suitably namespaced for each application and
     * reduces the risk of clashes.
     * <br>
     * Note that this endpoint is not necessarily handled by the homeserver, but by another webserver, to be used for discovering
     * the homeserver URL.
     * <br>
     * Return: {@link ServerDiscoveryResponse}.
     * <p>Status code 200: Server discovery information.</p>
     * <p>Status code 404: No server discovery information available.</p>
     *
     * @return {@link ServerDiscoveryResponse}.
     */
    @GET
    @Path("/")
    CompletionStage<ServerDiscoveryResponse> serverDiscovery();
}
