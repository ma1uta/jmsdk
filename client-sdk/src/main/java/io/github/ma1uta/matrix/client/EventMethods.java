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

package io.github.ma1uta.matrix.client;

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.client.api.EventApi;
import io.github.ma1uta.matrix.client.model.event.SendEventResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * EventMethods api.
 */
public class EventMethods {

    private final MatrixClient matrixClient;

    EventMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    public MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Send notice.
     *
     * @param roomId room id.
     * @param text   message.
     * @return sent event id.
     */
    public SendEventResponse sendNotice(String roomId, String text) {
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("roomId", roomId);
        pathParams.put("eventType", Event.EventType.ROOM_MESSAGE);
        pathParams.put("txnId", Long.toString(getMatrixClient().getTxn().getAndIncrement()));

        Map<String, String> payload = new HashMap<>();
        payload.put("msgtype", Event.MessageType.NOTICE);
        payload.put("body", text);
        return getMatrixClient().getRequestMethods().put(EventApi.class, "sendEvent", pathParams, null, payload, SendEventResponse.class);
    }

    /**
     * Send notice.
     *
     * @param roomId        room id.
     * @param text          message.
     * @param formattedText formatted message.
     * @return sent event id.
     */
    public SendEventResponse sendFormattedNotice(String roomId, String text, String formattedText) {
        Map<String, String> pathParams = new HashMap<>();
        pathParams.put("roomId", roomId);
        pathParams.put("eventType", Event.EventType.ROOM_MESSAGE);
        pathParams.put("txnId", Long.toString(getMatrixClient().getTxn().getAndIncrement()));

        Map<String, String> payload = new HashMap<>();
        payload.put("msgtype", Event.MessageType.NOTICE);
        payload.put("body", text);
        payload.put("formatted_body", formattedText);
        payload.put("format", "org.matrix.custom.html");
        return getMatrixClient().getRequestMethods().put(EventApi.class, "sendEvent", pathParams, null, payload, SendEventResponse.class);
    }
}
