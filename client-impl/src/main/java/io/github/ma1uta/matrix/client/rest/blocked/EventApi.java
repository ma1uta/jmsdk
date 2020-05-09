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

import io.github.ma1uta.matrix.Page;
import io.github.ma1uta.matrix.client.model.event.JoinedMembersResponse;
import io.github.ma1uta.matrix.client.model.event.MembersResponse;
import io.github.ma1uta.matrix.client.model.event.RedactRequest;
import io.github.ma1uta.matrix.client.model.event.SendEventResponse;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.content.EventContent;

import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * There are several APIs provided to GET events for a room.
 */
@Path("/_matrix/client/r0/rooms")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface EventApi {

    /**
     * Get a single event based on roomId/eventId. You must have permission to retrieve this event e.g. by being a member in the
     * room for this event.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link Event}.
     * <p>Status code 200: The full event.</p>
     * <p>Status code 404: The event not found, or you do not have permission to read this event.</p>
     *
     * @param roomId  Required. The ID of the room the event is in.
     * @param eventId Required. The event ID to get.
     * @return Event.
     */
    @GET
    @Path("/{roomId}/event/{eventId}")
    Event roomEvent(
        @PathParam("roomId") String roomId,
        @PathParam("eventId") String eventId
    );

    /**
     * Looks up the contents of a state event in a room. If the user is joined to the room then the state is taken from the current
     * state of the room. If the user has left the room then the state is taken from the state of the room when they left.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link EventContent}.
     * <p>Status code 200: The content of the state event.</p>
     * <p>Status code 403: You aren't a member of the room and weren't previously a member of the room.</p>
     * <p>Status code 404: The room has no state with the given type or key.</p>
     *
     * @param roomId    Required. The room to look up the state in.
     * @param eventType Required. The type of state to look up.
     * @param stateKey  Required. The key of the state to look up.
     * @return event content.
     */
    @GET
    @Path("/{roomId}/state/{eventType}/{stateKey}")
    byte[] roomEventWithTypeAndState(
        @PathParam("roomId") String roomId,
        @PathParam("eventType") String eventType,
        @PathParam("stateKey") String stateKey
    );

    /**
     * Get the state events for the current state of a room.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link List} of the {@link Event}s.
     * <p>Status code 200: The current state of the room.</p>
     * <p>Status code 403: You aren't a member of the room and weren't previously a member of the room.</p>
     *
     * @param roomId Required. The room to look up the state for.
     * @return event list.
     */
    @GET
    @Path("/{roomId}/state")
    List<Event> roomState(
        @PathParam("roomId") String roomId
    );

    /**
     * Get the list of members for this room.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link MembersResponse}.
     * <p>Status code 200: A list of members of the room. If you are joined to the room then this will be the current
     * members of the room. If you have left the room then this will be the members of the room when you left.</p>
     * <p>Status code 403: You aren't a member of the room and weren't previously a member of the room.</p>
     *
     * @param roomId Required. The room to get the member events for.
     * @return room members.
     */
    @GET
    @Path("/{roomId}/members")
    MembersResponse members(
        @PathParam("roomId") String roomId
    );

    /**
     * This API returns a map of MXIDs to member info objects for members of the room. The current user must be in the room for
     * it to work, unless it is an Application Service in which case any of the AS's users must be in the room. This API
     * is primarily for Application Services and should be faster to respond than /members as it can be implemented more
     * efficiently on the server.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link JoinedMembersResponse}.
     * <p>Status code 200: A map of MXID to room member objects.</p>
     * <p>Status code 403: You aren't a member of the room.</p>
     *
     * @param roomId Required. The room to get the members of.
     * @return joined members info.
     */
    @GET
    @Path("/{roomId}/joined_members")
    JoinedMembersResponse joinedMembers(
        @PathParam("roomId") String roomId
    );

    /**
     * This API returns a list of message and state events for a room. It uses pagination query parameters to paginate history in the room.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link Page} of the {@link Event}s.
     * <p>Status code 200: A list of messages with a new token to request more.</p>
     * <p>Status code 403: You aren't a member of the room.</p>
     *
     * @param roomId Required. The room to get events from.
     * @param from   Required. The token to start returning events from. This token can be obtained from a prev_batch token
     *               returned for each room by the sync API, or from a start or end token returned by a previous request to
     *               this endpoint.
     * @param to     The token to stop returning events at. This token can be obtained from a prev_batch token returned for
     *               each room by the sync endpoint, or from a start or end token returned by a previous request to this endpoint.
     * @param dir    Required. The direction to return events from. One of: ["b", "f"]
     * @param limit  The maximum number of events to return. Default: 10.
     * @param filter A JSON RoomEventFilter to filter returned events with.
     * @return messages and states.
     */
    @GET
    @Path("/{roomId}/messages")
    Page<Event> messages(
        @PathParam("roomId") String roomId,
        @QueryParam("from") String from,
        @QueryParam("to") String to,
        @QueryParam("dir") String dir,
        @QueryParam("limit") Integer limit,
        @QueryParam("filter") String filter
    );

    /**
     * State events can be sent using this endpoint. These events will be overwritten if (room id), (event type) and (state key) all match.
     * <br>
     * Requests to this endpoint cannot use transaction IDs like other PUT paths because they cannot be differentiated from the state_key.
     * Furthermore, POST is unsupported on state paths.
     * <br>
     * The body of the request should be the content object of the event; the fields in this object will vary depending on the
     * type of event. See Room Events for the m. event specification.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link SendEventResponse}.
     * <p>Status code 200: An ID for the sent event.</p>
     * <p>Status code 403: The sender doesn't have permission to send the event into the room.</p>
     *
     * @param roomId    Required. The room to set the state in.
     * @param eventType Required. The type of event to send.
     * @param stateKey  Required. The state_key for the state to send. Defaults to the empty string.
     * @param event     event.
     * @return send event info.
     */
    @PUT
    @Path("/{roomId}/state/{eventType}/{stateKey}")
    SendEventResponse sendStateEvent(
        @PathParam("roomId") String roomId,
        @PathParam("eventType") String eventType,
        @PathParam("stateKey") String stateKey,
        EventContent event
    );

    /**
     * State events can be sent using this endpoint. This endpoint is equivalent to calling /rooms/{roomId}/state/{eventType}/{stateKey}
     * with an empty stateKey. Previous state events with matching (roomId) and (eventType), and empty (stateKey), will be overwritten.
     * <br>
     * Requests to this endpoint cannot use transaction IDs like other PUT paths because they cannot be differentiated from the state_key.
     * Furthermore, POST is unsupported on state paths.
     * <br>
     * The body of the request should be the content object of the event; the fields in this object will vary depending on the type
     * of event. See Room Events for the m. event specification.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link SendEventResponse}.
     * <p>Status code 200: An ID for the sent event.</p>
     * <p>Status code 403: The sender doesn't have permission to send the event into the room.</p>
     *
     * @param roomId    Required. The room to set the state in.
     * @param eventType Required. The type of event to send.
     * @param event     event.
     * @return send event info.
     */
    @PUT
    @Path("/{roomId}/state/{eventType}")
    SendEventResponse sendStateEvent(
        @PathParam("roomId") String roomId,
        @PathParam("eventType") String eventType,
        EventContent event
    );

    /**
     * This endpoint is used to send a message event to a room. Message events allow access to historical events and pagination,
     * making them suited for "once-off" activity in a room.
     * <br>
     * The body of the request should be the content object of the event; the fields in this object will vary depending on the
     * type of event. See Room Events for the m. event specification.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link SendEventResponse}.
     * <p>Status code 200: An ID for the sent event.</p>
     *
     * @param roomId    Required. The room to send the event to.
     * @param eventType Required. The type of event to send.
     * @param txnId     Required. The transaction ID for this event. Clients should generate an ID unique across requests with the
     *                  same access token; it will be used by the server to ensure idempotency of requests.
     * @param event     event.
     * @return send event info.
     */
    @PUT
    @Path("/{roomId}/send/{eventType}/{txnId}")
    SendEventResponse sendEvent(
        @PathParam("roomId") String roomId,
        @PathParam("eventType") String eventType,
        @PathParam("txnId") String txnId,
        EventContent event
    );

    /**
     * Strips all information out of an event which isn't critical to the integrity of the server-side representation of the room.
     * <br>
     * This cannot be undone.
     * <br>
     * Users may redact their own events, and any user with a power level greater than or equal to the redact power level of the
     * room may redact events there.
     * <br>
     * <b>Requires auth</b>: Yes.
     * <br>
     * Return: {@link SendEventResponse}.
     * <p>Status code 200: An ID for the redaction event.</p>
     *
     * @param roomId  Required. The room from which to redact the event.
     * @param eventId Required. The ID of the event to redact.
     * @param txnId   Required. The transaction ID for this event. Clients should generate a unique ID; it will be used by the
     *                server to ensure idempotency of requests.
     * @param event   The reason for the event being redacted.
     * @return send event info.
     */
    @PUT
    @Path("/{roomId}/redact/{eventId}/{txnId}")
    SendEventResponse redact(
        @PathParam("roomId") String roomId,
        @PathParam("eventId") String eventId,
        @PathParam("txnId") String txnId,
        RedactRequest event
    );
}
