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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.api.ReceiptApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.receipt.ReadMarkersRequest;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Receipt method.
 */
public class ReceiptMethods extends AbstractMethods {

    public ReceiptMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Send receipt to specified event.
     *
     * @param roomId  The room id.
     * @param eventId The event id.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> sendReceipt(String roomId, String eventId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId).path("eventId", eventId)
            .path("receiptType", ReceiptApi.Receipt.READ);
        return factory().post(ReceiptApi.class, "receipt", params, "", EmptyResponse.class);
    }

    /**
     * Sets the position of the read marker for a given room, and optionally the read receipt's location.
     *
     * @param roomId  The room id.
     * @param request The optional read markers.
     * @return The empty response.
     */
    public CompletableFuture<EmptyResponse> readMarkers(String roomId, ReadMarkersRequest request) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        RequestParams params = defaults().clone().path("roomId", roomId);
        return factory().post(ReceiptApi.class, "readMarkers", params, request, EmptyResponse.class);
    }
}
