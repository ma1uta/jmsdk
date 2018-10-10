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

package io.github.ma1uta.matrix.support.jsonb;

import io.github.ma1uta.matrix.Event;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 * The Deserializer to the Event.
 */
public class EventDeserializer implements JsonbDeserializer<Event> {

    private final JsonParserFactory factory = Json.createParserFactory(Collections.emptyMap());

    private final EventContentDeserializer contentDeserializer = new EventContentDeserializer();

    private final UnsignedDeserializer unsignedDeserializer = new UnsignedDeserializer();

    protected JsonParserFactory factory() {
        return factory;
    }

    protected EventContentDeserializer contentDeserializer() {
        return contentDeserializer;
    }

    protected UnsignedDeserializer unsignedDeserializer() {
        return unsignedDeserializer;
    }

    @Override
    public Event deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        Event event = new Event();
        Map<String, JsonValue> props = new HashMap<>();

        while (parser.hasNext()) {
            JsonParser.Event parserEvent = parser.next();
            if (JsonParser.Event.END_OBJECT == parserEvent) {
                break;
            }

            if (JsonParser.Event.KEY_NAME == parserEvent) {
                String name = parser.getString();
                parser.next();
                JsonValue value = parser.getValue();

                switch (name) {
                    case "type":
                        event.setType(value.toString());
                        break;
                    case "event_id":
                        event.setEventId(value.toString());
                        break;
                    case "room_id":
                        event.setRoomId(value.toString());
                        break;
                    case "sender":
                        event.setSender(value.toString());
                        break;
                    case "origin_server_ts":
                        event.setOriginServerTs(Long.parseLong(value.toString()));
                        break;
                    case "state_key":
                        event.setStateKey(value.toString());
                        break;
                    default:
                        props.put(name, value);
                }
            }
        }

        if (event.getType() == null) {
            throw new RuntimeException("Wrong event, missing the type.");
        }

        props.forEach((name, value) -> {
            try (JsonParser contentParser = factory().createParser(value.asJsonObject())) {
                switch (name) {
                    case "unsigned":
                        event.setUnsigned(unsignedDeserializer().deserialize(contentParser, ctx, event.getType()));
                        break;
                    case "content":
                        event.setContent(contentDeserializer().deserialize(contentParser, ctx, event.getType()));
                        break;
                    case "prev_content":
                        event.setPrevContent(contentDeserializer().deserialize(contentParser, ctx, event.getType()));
                        break;
                    default:
                        parse(event, name, value, ctx);
                }
            }
        });

        return event;
    }

    protected void parse(Event event, String name, JsonValue value, DeserializationContext ctx) {
    }
}
