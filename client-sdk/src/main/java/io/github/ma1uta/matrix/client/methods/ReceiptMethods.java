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
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.ReceiptApi;

import java.util.Objects;

/**
 * Receipt method.
 */
public class ReceiptMethods {

    private final MatrixClient matrixClient;

    public ReceiptMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Send receipt to specified event.
     *
     * @param roomId  room id.
     * @param eventId event id.
     */
    public void sendReceipt(String roomId, String eventId) {
        Objects.requireNonNull(roomId, "RoomId cannot be empty.");
        Objects.requireNonNull(eventId, "EventId cannot be empty.");
        RequestParams params = new RequestParams().pathParam("roomId", roomId).pathParam("eventId", eventId)
            .pathParam("receiptType", ReceiptApi.Receipt.READ);
        getMatrixClient().getRequestMethods().post(ReceiptApi.class, "receipt", params, "", EmptyResponse.class);
    }
}
