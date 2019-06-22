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

import static io.github.ma1uta.matrix.support.jsonb.mapper.EventMapper.INSTANCE;

import io.github.ma1uta.matrix.event.CallCandidates;
import io.github.ma1uta.matrix.event.Direct;
import io.github.ma1uta.matrix.event.Dummy;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.ForwardedRoomKey;
import io.github.ma1uta.matrix.event.FullyRead;
import io.github.ma1uta.matrix.event.IgnoredUserList;
import io.github.ma1uta.matrix.event.KeyVerificationAccept;
import io.github.ma1uta.matrix.event.KeyVerificationCancel;
import io.github.ma1uta.matrix.event.KeyVerificationKey;
import io.github.ma1uta.matrix.event.KeyVerificationMac;
import io.github.ma1uta.matrix.event.KeyVerificationRequest;
import io.github.ma1uta.matrix.event.KeyVerificationStart;
import io.github.ma1uta.matrix.event.Presence;
import io.github.ma1uta.matrix.event.RawEvent;
import io.github.ma1uta.matrix.event.RoomKey;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * The Deserializer to the Event.
 */
public class EventDeserializer implements JsonbDeserializer<Event> {

    @Override
    public Event deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonObject jsonObject = parser.getObject();

        if (jsonObject.get("type") == null || jsonObject.isNull("type")) {
            return parse(jsonObject);
        }

        String type = jsonObject.getString("type");

        switch (type) {
            case Direct.TYPE:
                return INSTANCE.direct(jsonObject);
            case Dummy.TYPE:
                return INSTANCE.dummy(jsonObject);
            case ForwardedRoomKey.TYPE:
                return INSTANCE.forwardedRoomKey(jsonObject);
            case FullyRead.TYPE:
                return INSTANCE.fullyRead(jsonObject);
            case IgnoredUserList.TYPE:
                return INSTANCE.ignoredUserList(jsonObject);
            case KeyVerificationAccept.TYPE:
                return INSTANCE.keyVerificationAccept(jsonObject);
            case KeyVerificationCancel.TYPE:
                return INSTANCE.keyVerificationCancel(jsonObject);
            case KeyVerificationKey.TYPE:
                return INSTANCE.keyVerificationKey(jsonObject);
            case KeyVerificationMac.TYPE:
                return INSTANCE.keyVerificationMac(jsonObject);
            case KeyVerificationRequest.TYPE:
                return INSTANCE.keyVerificationRequest(jsonObject);
            case KeyVerificationStart.TYPE:
                return INSTANCE.keyVerificationStart(jsonObject);
            case Presence.TYPE:
                return INSTANCE.presence(jsonObject);

            case RoomKey.TYPE:
                return INSTANCE.roomKey(jsonObject);
            case CallCandidates.TYPE:
                return INSTANCE.callCandidates(jsonObject);
            default:
                return parse(jsonObject);
        }
    }

    protected Event parse(JsonObject jsonObject) {
        Map<String, Object> props = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            JsonValue value = entry.getValue();
            Object object = value == null ? null : parse(value);
            props.put(entry.getKey(), object);
        }
        return new RawEvent(props, (String) props.get("type"));
    }

    protected Object parse(JsonValue jsonValue) {
        if (jsonValue == null) {
            return null;
        }
        switch (jsonValue.getValueType()) {
            case NULL:
                return null;
            case STRING:
                return ((JsonString) jsonValue).getString();
            case NUMBER:
                return ((JsonNumber) jsonValue).longValue();
            case ARRAY:
                return jsonValue.asJsonArray().stream().map(this::parse).collect(Collectors.toList());
            case TRUE:
                return Boolean.TRUE;
            case FALSE:
                return Boolean.FALSE;
            case OBJECT:
                return parse(jsonValue.asJsonObject());
            default:
                throw new IllegalArgumentException("Unknown json type: " + jsonValue.getValueType());
        }
    }
}
