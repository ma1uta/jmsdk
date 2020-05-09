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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.model.receipt.ReadMarkersRequest;
import io.github.ma1uta.matrix.client.rest.blocked.ReceiptApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;

/**
 * Receipt method.
 */
public class ReceiptMethods {

    private final ReceiptApi receiptApi;

    public ReceiptMethods(RestClientBuilder restClientBuilder) {
        this.receiptApi = restClientBuilder.build(ReceiptApi.class);
    }

    /**
     * Send receipt to specified event.
     *
     * @param roomId  The room id.
     * @param eventId The event id.
     * @return The empty response.
     */
    public EmptyResponse sendReceipt(String roomId, String eventId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");

        return receiptApi.receipt(roomId, io.github.ma1uta.matrix.client.api.ReceiptApi.Receipt.READ, eventId);
    }

    /**
     * Sets the position of the read marker for a given room, and optionally the read receipt's location.
     *
     * @param roomId  The room id.
     * @param request The optional read markers.
     * @return The empty response.
     */
    public EmptyResponse readMarkers(String roomId, ReadMarkersRequest request) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");

        return receiptApi.readMarkers(roomId, request);
    }
}
