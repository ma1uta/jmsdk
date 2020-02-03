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

package io.github.ma1uta.matrix.support.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.MegolmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.OlmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.RawEncryptedContent;

import java.io.IOException;

/**
 * RoomEncryptedContent deserializer.
 */
public class RoomEncryptedContentDeserializer extends JsonDeserializer<RoomEncryptedContent> {

    @Override
    public RoomEncryptedContent deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode algorithmNode = node.get("algorithm");
        if (algorithmNode == null || !algorithmNode.isTextual()) {
            return parse(node, ctxt, codec, null);
        }
        String algorithm = algorithmNode.asText();

        switch (algorithm) {
            case MegolmEncryptedContent.ALGORITHM:
                return codec.treeToValue(node, MegolmEncryptedContent.class);
            case OlmEncryptedContent.ALGORITHM:
                return codec.treeToValue(node, OlmEncryptedContent.class);
            default:
                return parse(node, ctxt, codec, algorithm);
        }
    }

    protected RoomEncryptedContent parse(JsonNode jsonNode, DeserializationContext ctxt, ObjectCodec codec, String algorithm) {
        return new RawEncryptedContent(jsonNode, algorithm);
    }
}
