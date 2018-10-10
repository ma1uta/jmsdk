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

import static java.util.stream.Collectors.toMap;

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.events.RoomEncrypted;
import io.github.ma1uta.matrix.events.encrypted.MegolmEncrypted;
import io.github.ma1uta.matrix.events.encrypted.OlmEncrypted;
import io.github.ma1uta.matrix.events.encrypted.RawEncrypted;
import io.github.ma1uta.matrix.events.nested.CiphertextInfo;

import java.lang.reflect.Type;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * The Jsonb deserializer of the room encrypted messages.
 */
public class EncryptedMessageDeserializer implements JsonbDeserializer<RoomEncrypted> {

    @Override
    public RoomEncrypted deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

        JsonObject object = parser.getObject();
        String algorithm = object.getString("algorithm");

        switch (algorithm) {
            case Event.Encryption.OLM:
                return olmEncrypted(object, ctx);
            case Event.Encryption.MEGOLM:
                return megolmEncrypted(object, ctx);
            default:
                return parse(object, algorithm, ctx);
        }
    }

    protected RoomEncrypted megolmEncrypted(JsonObject object, DeserializationContext ctx) {
        MegolmEncrypted megolmEncrypted = commonProperties(new MegolmEncrypted(), object);
        megolmEncrypted.setCiphertext(object.getString("ciphertext"));
        return megolmEncrypted;
    }

    protected RoomEncrypted olmEncrypted(JsonObject object, DeserializationContext ctx) {
        OlmEncrypted olmEncrypted = commonProperties(new OlmEncrypted(), object);
        if (!object.isNull("ciphertext")) {
            olmEncrypted.setCiphertext(
                object
                    .getJsonObject("ciphertext")
                    .entrySet()
                    .stream()
                    .collect(
                        toMap(
                            Map.Entry::getKey,
                            entry -> {
                                JsonObject ciphertextInfo = entry.getValue().asJsonObject();
                                CiphertextInfo info = new CiphertextInfo();
                                info.setBody(ciphertextInfo.getString("body"));
                                if (!ciphertextInfo.isNull("type")) {
                                    info.setType(ciphertextInfo.getJsonNumber("type").longValue());
                                }
                                return info;
                            }
                        )
                    )
            );
        }
        return olmEncrypted;
    }

    protected <T extends RoomEncrypted> T commonProperties(T roomEncrypted, JsonObject object) {
        roomEncrypted.setSenderKey(object.getString("sender_key"));
        roomEncrypted.setDeviceId(object.getString("device_id"));
        roomEncrypted.setSessionId(object.getString("session_id"));
        return roomEncrypted;
    }

    protected RoomEncrypted parse(JsonObject jsonObject, String algorithm, DeserializationContext ctx) {
        return new RawEncrypted(jsonObject, algorithm);
    }
}
