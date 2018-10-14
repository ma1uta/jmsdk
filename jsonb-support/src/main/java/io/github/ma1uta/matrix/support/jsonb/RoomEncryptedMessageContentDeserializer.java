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

import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.MegolmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.OlmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.RawEncryptedContent;

import java.lang.reflect.Type;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * The Jsonb deserializer of the room encrypted messages.
 */
public class RoomEncryptedMessageContentDeserializer implements JsonbDeserializer<RoomEncryptedContent> {

    private Jsonb jsonb = JsonbBuilder.create();

    protected Jsonb jsonb() {
        return jsonb;
    }

    @Override
    public RoomEncryptedContent deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonObject object = parser.getObject();
        if (object.get("algorithm") == null || object.isNull("algorithm")) {
            return parse(object, null, ctx);
        }

        String algorithm = object.getString("algorithm");
        String objectSource = object.toString();

        switch (algorithm) {
            case Event.Encryption.OLM:
                return jsonb().fromJson(objectSource, OlmEncryptedContent.class);
            case Event.Encryption.MEGOLM:
                return jsonb().fromJson(objectSource, MegolmEncryptedContent.class);
            default:
                return parse(object, algorithm, ctx);
        }
    }

    protected RoomEncryptedContent parse(JsonObject jsonObject, String algorithm, DeserializationContext ctx) {
        return new RawEncryptedContent(jsonObject, algorithm);
    }
}
