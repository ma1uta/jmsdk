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
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.MegolmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.OlmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.RawEncryptedContent;
import io.github.ma1uta.matrix.event.nested.CiphertextInfo;

import java.lang.reflect.Type;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * The Jsonb deserializer of the room encrypted messages.
 */
public class EncryptedMessageDeserializer implements JsonbDeserializer<RoomEncryptedContent> {

    @Override
    public RoomEncryptedContent deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

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

    protected RoomEncryptedContent megolmEncrypted(JsonObject object, DeserializationContext ctx) {
        MegolmEncryptedContent megolmEncrypted = commonProperties(new MegolmEncryptedContent(), object);
        megolmEncrypted.setCiphertext(object.getString("ciphertext"));
        return megolmEncrypted;
    }

    protected RoomEncryptedContent olmEncrypted(JsonObject object, DeserializationContext ctx) {
        OlmEncryptedContent olmEncrypted = commonProperties(new OlmEncryptedContent(), object);
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

    protected <T extends RoomEncryptedContent> T commonProperties(T roomEncrypted, JsonObject object) {
        roomEncrypted.setSenderKey(object.getString("sender_key"));
        roomEncrypted.setDeviceId(object.getString("device_id"));
        roomEncrypted.setSessionId(object.getString("session_id"));
        return roomEncrypted;
    }

    protected RoomEncryptedContent parse(JsonObject jsonObject, String algorithm, DeserializationContext ctx) {
        return new RawEncryptedContent(jsonObject, algorithm);
    }
}
