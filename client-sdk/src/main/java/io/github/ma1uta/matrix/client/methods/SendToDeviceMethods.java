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
import io.github.ma1uta.matrix.client.api.SendToDeviceApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.sendtodevice.SendToDeviceRequest;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * Send to device method.
 */
public class SendToDeviceMethods extends AbstractMethods {

    public SendToDeviceMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * This endpoint is used to send send-to-device events to a set of client devices.
     *
     * @param eventType The type of event to send.
     * @param request   sending data.
     * @return empty response.
     */
    public CompletableFuture<EmptyResponse> sendToDevice(String eventType, SendToDeviceRequest request) {
        Objects.requireNonNull(eventType, "RoomId cannot be empty.");
        RequestParams params = defaults().clone().path("eventType", eventType).path("txnId", Long.toString(System.currentTimeMillis()));
        return factory().put(SendToDeviceApi.class, "send", params, request, EmptyResponse.class);
    }
}
