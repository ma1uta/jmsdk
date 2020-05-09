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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.report.ReportRequest;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Users may encounter content which they find inappropriate and should be able to report it to the server administrators or room
 * moderators for review. This module defines a way for users to report content.
 * <br>
 * Content is reported based upon a negative score, where -100 is "most offensive" and 0 is "inoffensive".
 */
@Path("/_matrix/client/r0")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ReportApi {

    /**
     * Reports an event as inappropriate to the server, which may then notify the appropriate people.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The event has been reported successfully.</p>
     *
     * @param roomId        Required. The room in which the event being reported is located.
     * @param eventId       Required. The event to report.
     * @param reportRequest JSON body request.
     * @return {@link EmptyResponse}.
     */
    @POST
    @Path("/rooms/{roomId}/report/{eventId}")
    CompletionStage<EmptyResponse> report(
        @PathParam("roomId") String roomId,
        @PathParam("eventId") String eventId,
        ReportRequest reportRequest
    );
}
