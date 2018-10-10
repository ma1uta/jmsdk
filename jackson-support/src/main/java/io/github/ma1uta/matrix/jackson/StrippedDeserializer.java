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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.StrippedState;

import java.io.IOException;

/**
 * The deserializer of the StrippedState.
 */
public class StrippedDeserializer extends JsonDeserializer<StrippedState> {

    @Override
    public StrippedState deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode jsonType = node.get("type");
        if (jsonType == null || !jsonType.isTextual()) {
            throw new JsonParseException(parser, "Missing required field: type");
        }
        String type = jsonType.asText();

        StrippedState strippedState = new StrippedState();
        strippedState.setType(type);

        EventContentDeserializer contentDeserializer = new EventContentDeserializer();
        strippedState.setContent(contentDeserializer.deserialize(node.get("content"), type, codec));

        JsonNode stateKey = node.get("state_key");
        if (stateKey != null && stateKey.isTextual()) {
            strippedState.setStateKey(stateKey.asText());
        }

        JsonNode sender = node.get("sender");
        if (sender != null && sender.isTextual()) {
            strippedState.setSender(sender.asText());
        }

        return strippedState;
    }
}
