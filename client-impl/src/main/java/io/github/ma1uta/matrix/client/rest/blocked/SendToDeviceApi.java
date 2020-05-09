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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.sendtodevice.SendToDeviceRequest;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * This module provides a means by which clients can exchange signalling messages without them being stored permanently as part of
 * a shared communication history. A message is delivered exactly once to each client device.
 * <br>
 * The primary motivation for this API is exchanging data that is meaningless or undesirable to persist in the room DAG - for example,
 * one-time authentication tokens or key data. It is not intended for conversational data, which should be sent using the normal
 * /rooms/&lt;room_id&gt;/send API for consistency throughout Matrix.
 */
@Path("/_matrix/client/r0/sendToDevice")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SendToDeviceApi {

    /**
     * This endpoint is used to send send-to-device events to a set of client devices.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * <p>Status code 200: The message was successfully sent.</p>
     *
     * @param eventType           Required. The type of event to send.
     * @param txnId               Required. The transaction ID for this event. Clients should generate an ID unique across requests with the
     *                            same access token; it will be used by the server to ensure idempotency of requests.
     * @param sendToDeviceRequest Request body.
     * @return {@link EmptyResponse}.
     */
    @PUT
    @Path("/{eventType}/{txnId}")
    EmptyResponse send(
        @PathParam("eventType") String eventType,
        @PathParam("txnId") String txnId,
        SendToDeviceRequest sendToDeviceRequest
    );
}
