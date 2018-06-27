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
import io.github.ma1uta.matrix.client.api.SendToDeviceApi;
import io.github.ma1uta.matrix.client.model.sendtodevice.SendToDeviceRequest;

/**
 * Send to device method.
 */
public class SendToDeviceMethods {

    private final MatrixClient matrixClient;

    public SendToDeviceMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * This endpoint is used to send send-to-device events to a set of client devices.
     *
     * @param eventType The type of event to send.
     * @param request   sending data.
     */
    public void sendToDevice(String eventType, SendToDeviceRequest request) {
        RequestParams params = new RequestParams().pathParam("eventType", eventType)
            .pathParam("txnId", Long.toString(System.currentTimeMillis()));
        getMatrixClient().getRequestMethods().put(SendToDeviceApi.class, "send", params, request, EmptyResponse.class);
    }
}
