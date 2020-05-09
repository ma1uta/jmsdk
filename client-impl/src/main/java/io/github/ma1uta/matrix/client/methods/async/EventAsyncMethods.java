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

import io.github.ma1uta.matrix.Page;
import io.github.ma1uta.matrix.client.model.event.JoinedMembersResponse;
import io.github.ma1uta.matrix.client.model.event.MembersResponse;
import io.github.ma1uta.matrix.client.model.event.RedactRequest;
import io.github.ma1uta.matrix.client.model.event.SendEventResponse;
import io.github.ma1uta.matrix.client.rest.async.EventApi;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.RoomMessage;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.event.message.FormattedBody;
import io.github.ma1uta.matrix.event.message.Notice;
import io.github.ma1uta.matrix.event.message.Text;
import io.github.ma1uta.matrix.impl.Deserializer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;

/**
 * EventAsyncMethods api.
 */
public class EventAsyncMethods {

    private final EventApi eventApi;

    private final Deserializer deserializer;

    public EventAsyncMethods(RestClientBuilder restClientBuilder) {
        this.eventApi = restClientBuilder.build(EventApi.class);
        Iterator<Deserializer> iterator = ServiceLoader.load(Deserializer.class).iterator();
        if (iterator.hasNext()) {
            this.deserializer = iterator.next();
        } else {
            throw new IllegalStateException("Missing Event deserializer. Check that jsonb-support or jackson-support modules enabled.");
        }
    }

    /**
     * Get a single event based on roomId/eventId. You must have permission to retrieve this event e.g. by being a member in the
     * room for this event.
     *
     * @param roomId  The ID of the room the event is in.
     * @param eventId The event ID to get.
     * @return The full event.
     */
    public CompletableFuture<Event> event(String roomId, String eventId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");

        return eventApi.roomEvent(roomId, eventId).toCompletableFuture();
    }

    /**
     * Looks up the contents of a state event in a room. If the user is joined to the room then the state is taken from the current
     * state of the room. If the user has left the room then the state is taken from the state of the room when they left.
     *
     * @param roomId    The room to look up the state in.
     * @param eventType The type of state to look up.
     * @param stateKey  The key of the state to look up.
     * @return The content of the state event.
     */
    public CompletableFuture<EventContent> eventContent(String roomId, String eventType, String stateKey) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventType, "EventType cannot be empty.");
        Objects.requireNonNull(stateKey, "StateKey cannot be empty.");

        return eventApi.roomEventWithTypeAndState(roomId, eventType, stateKey)
            .thenApply(bytes -> {
                try {
                    return deserializer.deserializeEventContent(bytes, eventType);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).toCompletableFuture();
    }

    /**
     * Get the state events for the current state of a room.
     *
     * @param roomId The room to look up the state for.
     * @return The current state of the room.
     */
    public CompletableFuture<List<Event>> roomState(String roomId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        return eventApi.roomState(roomId).toCompletableFuture();
    }

    /**
     * Get the list of members for this room.
     *
     * @param roomId The room to get the member events for.
     * @return A list of members of the room.
     */
    public CompletableFuture<MembersResponse> members(String roomId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        return eventApi.members(roomId).toCompletableFuture();
    }

    /**
     * This API returns a map of MXIDs to member info objects for members of the room.
     *
     * @param roomId The room to get the members of.
     * @return A map of MXID to room member objects.
     */
    public CompletableFuture<JoinedMembersResponse> joinedMembers(String roomId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        return eventApi.joinedMembers(roomId).toCompletableFuture();
    }

    /**
     * This API returns a list of message and state events for a room. It uses pagination query parameters to paginate history in the room.
     *
     * @param roomId The room to get events from.
     * @param from   The token to start returning events from.
     * @param to     The token to stop returning events at.
     * @param dir    The direction to return events from.
     * @param limit  The direction to return events from.
     * @param filter A JSON RoomEventFilter to filter returned events with.
     * @return A list of messages with a new token to request more.
     */
    public CompletableFuture<Page<Event>> messages(String roomId, String from, String to, String dir, Integer limit, String filter) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(from, "From cannot be empty.");
        Objects.requireNonNull(dir, "Dir cannot be empty.");

        return eventApi.messages(roomId, from, to, dir, limit, filter).toCompletableFuture();
    }

    /**
     * State events can be sent using this endpoint. These events will be overwritten if (room id), (event type) and (state key) all match.
     *
     * @param roomId       The room to set the state in.
     * @param eventType    The type of event to send.
     * @param stateKey     The state_key for the state to send. Defaults to the empty string.
     * @param eventContent The event content.
     * @return An ID for the sent event.
     */
    public CompletableFuture<SendEventResponse> sendStateEvent(String roomId, String eventType, String stateKey,
                                                               EventContent eventContent) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventType, "EventType cannot be empty.");
        Objects.requireNonNull(stateKey, "StateKey cannot be empty.");

        return eventApi.sendStateEvent(roomId, eventType, stateKey, eventContent).toCompletableFuture();
    }

    /**
     * State events can be sent using this endpoint. These events will be overwritten if (room id), (event type) and (state key) all match.
     *
     * @param roomId       The room to set the state in.
     * @param eventType    The type of event to send.
     * @param eventContent The event content.
     * @return An ID for the sent event.
     */
    public CompletableFuture<SendEventResponse> sendStateEvent(String roomId, String eventType, EventContent eventContent) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventType, "EventType cannot be empty.");

        return eventApi.sendStateEvent(roomId, eventType, eventContent).toCompletableFuture();
    }

    /**
     * This endpoint is used to send a message event to a room. Message events allow access to historical events and pagination,
     * making them suited for "once-off" activity in a room.
     *
     * @param roomId       The room to send the event to.
     * @param eventType    The type of event to send.
     * @param eventContent The event content.
     * @return An ID for the sent event.
     */
    public CompletableFuture<SendEventResponse> sendEvent(String roomId, String eventType, EventContent eventContent) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventType, "EventType cannot be empty.");

        return eventApi.sendEvent(roomId, eventType, Long.toString(System.currentTimeMillis()), eventContent).toCompletableFuture();
    }

    /**
     * Strips all information out of an event which isn't critical to the integrity of the server-side representation of the room.
     *
     * @param roomId  The room from which to redact the event.
     * @param eventId The ID of the event to redact.
     * @param reason  The reason for the event being redacted.
     * @return An ID for the redaction event.
     */
    public CompletableFuture<SendEventResponse> redact(String roomId, String eventId, String reason) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");

        RedactRequest request = new RedactRequest();
        request.setReason(reason);
        return eventApi.redact(roomId, eventId, Long.toString(System.currentTimeMillis()), request).toCompletableFuture();
    }

    /**
     * Send message.
     *
     * @param roomId The room id.
     * @param text   The message.
     * @return The ID of the sent event.
     */
    public CompletableFuture<SendEventResponse> sendMessage(String roomId, String text) {
        return sendFormattedMessage(roomId, text, null);
    }

    /**
     * Send notice.
     *
     * @param roomId The room id.
     * @param text   The message.
     * @return The ID of the sent event.
     */
    public CompletableFuture<SendEventResponse> sendNotice(String roomId, String text) {
        return sendFormattedNotice(roomId, text, null);
    }

    /**
     * Send formatted message.
     *
     * @param roomId        The room id.
     * @param text          The message.
     * @param formattedText The formatted message.
     * @return The ID of the sent event.
     */
    public CompletableFuture<SendEventResponse> sendFormattedMessage(String roomId, String text, String formattedText) {
        return sendFormatted(roomId, text, formattedText, new Text());
    }

    /**
     * Send notice.
     *
     * @param roomId        The room id.
     * @param text          The message.
     * @param formattedText The formatted message.
     * @return The ID of the sent event.
     */
    public CompletableFuture<SendEventResponse> sendFormattedNotice(String roomId, String text, String formattedText) {
        return sendFormatted(roomId, text, formattedText, new Notice());
    }

    /**
     * Send formatted body object.
     *
     * @param roomId        The room id.
     * @param text          The message.
     * @param formattedText The formatted message.
     * @param payload       Formatted object.
     * @return The ID of the sent event.
     */
    protected CompletableFuture<SendEventResponse> sendFormatted(String roomId, String text, String formattedText, FormattedBody payload) {
        payload.setBody(text);
        payload.setFormattedBody(formattedText);
        if (formattedText != null) {
            payload.setFormat(FormattedBody.Format.ORG_MATRIX_CUSTOM_HTML);
        }

        return sendEvent(roomId, RoomMessage.TYPE, payload);
    }
}
