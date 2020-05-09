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
import io.github.ma1uta.matrix.client.model.sendtodevice.SendToDeviceRequest;
import io.github.ma1uta.matrix.client.rest.blocked.SendToDeviceApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;

/**
 * Send to device method.
 */
public class SendToDeviceMethods {

    private final SendToDeviceApi sendToDeviceApi;

    public SendToDeviceMethods(RestClientBuilder restClientBuilder) {
        this.sendToDeviceApi = restClientBuilder.build(SendToDeviceApi.class);
    }

    /**
     * This endpoint is used to send send-to-device events to a set of client devices.
     *
     * @param eventType The type of event to send.
     * @param request   The sending data.
     * @return The empty response.
     */
    public EmptyResponse sendToDevice(String eventType, SendToDeviceRequest request) {
        Objects.requireNonNull(eventType, "RoomId cannot be empty.");

        return sendToDeviceApi.send(eventType, Long.toString(System.currentTimeMillis()), request);
    }
}
