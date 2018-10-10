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

package io.github.ma1uta.matrix.jackson;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.Unsigned;

import java.io.IOException;

/**
 * Event deserializer.
 */
public class EventDeserializer extends JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        Event event = new Event();

        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        String type = asString(node.get("type"));
        if (isBlank(type)) {
            throw new JsonParseException(parser, "Missing required field: type");
        }

        event.setType(type);

        EventContentDeserializer contentDeserializer = new EventContentDeserializer();
        event.setContent(contentDeserializer.deserialize(node.get("content"), type, codec));

        String eventId = asString(node.get("event_id"));
        if (isBlank(eventId)) {
            throw new JsonParseException(parser, "Missing required field: event_id");
        }
        event.setEventId(eventId);

        String roomId = asString(node.get("room_id"));
        if (isBlank(roomId)) {
            throw new JsonParseException(parser, "Missing required field: room_id");
        }
        event.setRoomId(roomId);

        String sender = asString(node.get("sender"));
        if (isBlank(sender)) {
            throw new JsonParseException(parser, "Missing required field: sender");
        }
        event.setSender(sender);

        Long originServerTs = asLong(node.get("origin_server_ts"));
        if (originServerTs == null) {
            throw new JsonParseException(parser, "Missing required field: origin_server_ts");
        }
        event.setOriginServerTs(originServerTs);

        event.setUnsigned(codec.treeToValue(node.get("unsigned"), Unsigned.class));

        event.setPrevContent(contentDeserializer.deserialize(node.get("prev_content"), type, codec));

        event.setStateKey(asString(node.get("state_key")));

        return event;
    }

    protected String asString(JsonNode jsonElement) {
        return jsonElement != null && jsonElement.isTextual() ? jsonElement.asText() : null;
    }

    protected Long asLong(JsonNode jsonElement) {
        return jsonElement != null && jsonElement.isNumber() ? jsonElement.asLong() : null;
    }

    protected boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
