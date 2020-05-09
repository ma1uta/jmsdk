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
import io.github.ma1uta.matrix.client.model.receipt.ReadMarkersRequest;

import java.util.concurrent.CompletionStage;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This module adds in support for receipts. These receipts are a form of acknowledgement of an event. This module defines a single
 * acknowledgement: m.read which indicates that the user has read up to a given event.
 * <br>
 * Sending a receipt for each event can result in sending large amounts of traffic to a homeserver. To prevent this from becoming
 * a problem, receipts are implemented using "up to" markers. This marker indicates that the acknowledgement applies to all events
 * "up to and including" the event specified. For example, marking an event as "read" would indicate that the user had read all
 * events up to the referenced event.
 */
@Path("/_matrix/client/r0/rooms")
@Produces(MediaType.APPLICATION_JSON)
public interface ReceiptApi {

    /**
     * This API updates the marker for the given receipt type to the event ID specified.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The receipt was sent.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param roomId      Required. The room in which to send the event.
     * @param receiptType Required. The type of receipt to send. One of: ["m.read"]
     * @param eventId     Required. The event ID to acknowledge up to.
     * @return {@link EmptyResponse}.
     */
    @POST
    @Path("/{roomId}/receipt/{receiptType}/{eventId}")
    CompletionStage<EmptyResponse> receipt(
        @PathParam("roomId") String roomId,
        @PathParam("receiptType") String receiptType,
        @PathParam("eventId") String eventId
    );

    /**
     * Sets the position of the read marker for a given room, and optionally the read receipt's location.
     * <br>
     * <b>Rate-limited</b>: Yes.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The read marker, and read receipt if provided, have been updated.</p>
     * <p>Status code 429: This request was rate-limited.</p>
     *
     * @param roomId  Required. The room ID to set the read marker in for the user.
     * @param request JSON body request.
     * @return {@link EmptyResponse}.
     */
    @POST
    @Path("/{roomId}/read_markers")
    CompletionStage<EmptyResponse> readMarkers(
        @PathParam("roomId") String roomId,
        ReadMarkersRequest request
    );
}
