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

import io.github.ma1uta.matrix.StrippedState;

import java.lang.reflect.Type;
import java.util.Collections;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 * The {@link StrippedState} deserializer.
 */
public class StrippedStateDeserializer implements JsonbDeserializer<StrippedState> {

    private final JsonParserFactory factory = Json.createParserFactory(Collections.emptyMap());

    private final EventContentDeserializer contentDeserializer = new EventContentDeserializer();

    protected JsonParserFactory factory() {
        return factory;
    }

    protected EventContentDeserializer contentDeserializer() {
        return contentDeserializer;
    }

    /**
     * Deserialize the {@link StrippedState} object.
     *
     * @param parser JSON parser.
     * @param ctx    Deserialization context.
     * @param rtType Class of the event.
     * @return The {@link StrippedState} object.
     */
    public StrippedState deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        StrippedState state = new StrippedState();
        JsonObject content = null;

        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (JsonParser.Event.END_OBJECT == event) {
                break;
            }

            if (JsonParser.Event.KEY_NAME == event) {
                String name = parser.getString();

                switch (name) {
                    case "content":
                        content = parser.getObject();
                        break;
                    case "state_key":
                        state.setStateKey(parser.getString());
                        break;
                    case "sender":
                        state.setSender(parser.getString());
                        break;
                    case "type":
                        state.setType(parser.getString());
                        break;
                    default:
                        parse(state, parser, ctx);
                }
            }
        }

        if (content != null && !content.isEmpty() && state.getType() != null) {
            try (JsonParser contentParser = factory().createParser(content)) {
                state.setContent(contentDeserializer().deserialize(contentParser, ctx, state.getType()));
            }
        }

        return state;
    }

    protected void parse(StrippedState state, JsonParser parser, DeserializationContext ctx) {
        // nothing
    }
}
