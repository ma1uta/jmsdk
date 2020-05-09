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

import io.github.ma1uta.matrix.client.model.eventcontext.EventContextResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * This API returns a number of events that happened just before and after the specified event. This allows clients to get the context
 * surrounding an event.
 */
@Path("/_matrix/client/r0/rooms")
@Produces(MediaType.APPLICATION_JSON)
public interface EventContextApi {

    /**
     * This API returns a number of events that happened just before and after the specified event. This allows clients to get the
     * context surrounding an event.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link EventContextResponse}.
     * <p>Status code 200: The events and state surrounding the requested event.</p>
     *
     * @param roomId  Required. The room to get events from.
     * @param eventId Required. The event to get context around.
     * @param limit   The maximum number of events to return. Default: 10.
     * @return {@link EventContextResponse}.
     */
    @GET
    @Path("/{roomId}/context/{eventId}")
    EventContextResponse context(
        @PathParam("roomId") String roomId,
        @PathParam("eventId") String eventId,
        @QueryParam("limit") Integer limit
    );
}
