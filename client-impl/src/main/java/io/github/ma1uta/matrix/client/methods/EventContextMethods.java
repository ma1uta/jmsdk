/*
 * Copyright sablintolya@gmail.com
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

package io.github.ma1uta.matrix.client.methods;

import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.EventContextApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.eventcontext.EventContextResponse;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Event context methods.
 */
public class EventContextMethods extends AbstractMethods {

    public EventContextMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
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
    public CompletableFuture<EventContextResponse> context(String roomId, String eventId, Integer limit) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");

        RequestParams params = defaults().clone()
            .path("roomId", roomId)
            .path("eventId", eventId)
            .query("limit", limit);
        return factory().get(EventContextApi.class, "context", params, EventContextResponse.class);
    }
}
