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

package io.github.ma1uta.matrix.client.methods.blocked;

import io.github.ma1uta.matrix.client.model.eventcontext.EventContextResponse;
import io.github.ma1uta.matrix.client.rest.blocked.EventContextApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;

/**
 * Event context methods.
 */
public class EventContextMethods {

    private final EventContextApi eventContextApi;

    public EventContextMethods(RestClientBuilder restClientBuilder) {
        this.eventContextApi = restClientBuilder.build(EventContextApi.class);
    }

    /**
     * This API returns a number of events that happened just before and after the specified event. This allows clients to get the
     * context surrounding an event.
     *
     * @param roomId  The room to get events from.
     * @param eventId The event to get context around.
     * @param limit   The maximum number of events to return. Default: 10.
     * @return The events and state surrounding the requested event.
     */
    public EventContextResponse context(String roomId, String eventId, Integer limit) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");

        return eventContextApi.context(roomId, eventId, limit);
    }
}
