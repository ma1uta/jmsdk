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
import io.github.ma1uta.matrix.StrippedState;
import io.github.ma1uta.matrix.Unsigned;

import java.util.ArrayList;
import java.util.Collections;
import javax.json.Json;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 * Deserializer of the {@link Unsigned} object.
 */
public class UnsignedDeserializer {

    private final JsonParserFactory factory = Json.createParserFactory(Collections.emptyMap());

    private final EventContentDeserializer contentDeserializer = new EventContentDeserializer();

    protected JsonParserFactory factory() {
        return factory;
    }

    protected EventContentDeserializer contentDeserializer() {
        return contentDeserializer;
    }

    /**
     * Deserialize the {@link Unsigned} object.
     *
     * @param parser JSON parser.
     * @param ctx    Deserialization context.
     * @param type   Semantic type of the event.
     * @return the {@link Unsigned} object.
     */
    public Unsigned deserialize(JsonParser parser, DeserializationContext ctx, String type) {
        Unsigned unsigned = new Unsigned();

        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (JsonParser.Event.END_OBJECT == event) {
                break;
            }

            if (JsonParser.Event.KEY_NAME == event) {
                String name = parser.getString();

                parser.next();
                switch (name) {
                    case "age":
                        unsigned.setAge(parser.getLong());
                        break;
                    case "redacted_because":
                        unsigned.setRedactedBecause(ctx.deserialize(Event.class, parser));
                        break;
                    case "transaction_id":
                        unsigned.setTransactionId(parser.getString());
                        break;
                    case "prev_content":
                        unsigned.setPrevContent(contentDeserializer().deserialize(parser, ctx, type));
                        break;
                    case "invite_room_state":
                        try (JsonParser inviteParser = factory().createParser(parser.getArray())) {
                            unsigned.setInviteRoomState(ctx.deserialize(new ArrayList<StrippedState>() {
                            }.getClass(), inviteParser));
                        }
                        break;
                    default:
                        parse(unsigned, parser, ctx, type);
                }
            }
        }

        return unsigned;
    }

    protected void parse(Unsigned unsigned, JsonParser parser, DeserializationContext ctx, String type) {
        // nothing.
    }
}
