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

import io.github.ma1uta.matrix.event.CallAnswer;
import io.github.ma1uta.matrix.event.CallCandidates;
import io.github.ma1uta.matrix.event.CallHangup;
import io.github.ma1uta.matrix.event.CallInvite;
import io.github.ma1uta.matrix.event.Direct;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.ForwardedRoomKey;
import io.github.ma1uta.matrix.event.FullyRead;
import io.github.ma1uta.matrix.event.IgnoredUserList;
import io.github.ma1uta.matrix.event.Presence;
import io.github.ma1uta.matrix.event.PushRules;
import io.github.ma1uta.matrix.event.RawEvent;
import io.github.ma1uta.matrix.event.Receipt;
import io.github.ma1uta.matrix.event.RoomAliases;
import io.github.ma1uta.matrix.event.RoomAvatar;
import io.github.ma1uta.matrix.event.RoomCanonicalAlias;
import io.github.ma1uta.matrix.event.RoomCreate;
import io.github.ma1uta.matrix.event.RoomEncrypted;
import io.github.ma1uta.matrix.event.RoomEncryption;
import io.github.ma1uta.matrix.event.RoomGuestAccess;
import io.github.ma1uta.matrix.event.RoomHistoryVisibility;
import io.github.ma1uta.matrix.event.RoomJoinRules;
import io.github.ma1uta.matrix.event.RoomKey;
import io.github.ma1uta.matrix.event.RoomKeyRequest;
import io.github.ma1uta.matrix.event.RoomMember;
import io.github.ma1uta.matrix.event.RoomMessage;
import io.github.ma1uta.matrix.event.RoomMessageFeedback;
import io.github.ma1uta.matrix.event.RoomName;
import io.github.ma1uta.matrix.event.RoomPinned;
import io.github.ma1uta.matrix.event.RoomPowerLevels;
import io.github.ma1uta.matrix.event.RoomRedaction;
import io.github.ma1uta.matrix.event.RoomServerAcl;
import io.github.ma1uta.matrix.event.RoomThirdPartyInvite;
import io.github.ma1uta.matrix.event.RoomTopic;
import io.github.ma1uta.matrix.event.Sticker;
import io.github.ma1uta.matrix.event.Tag;
import io.github.ma1uta.matrix.event.Typing;

import java.lang.reflect.Type;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;

/**
 * The Deserializer to the Event.
 */
public class EventDeserializer implements JsonbDeserializer<Event> {

    private Jsonb jsonb = JsonbBuilder.create();

    protected Jsonb jsonb() {
        return jsonb;
    }

    @Override
    public Event deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        JsonObject object = parser.getObject();
        String event = object.toString();
        if (object.get("type") == null || object.isNull("type")) {
            return parse(event, ctx, null);
        }
        String type = object.getString("type");

        switch (type) {
            case CallAnswer.TYPE:
                return jsonb().fromJson(event, CallAnswer.class);
            case CallCandidates.TYPE:
                return jsonb().fromJson(event, CallCandidates.class);
            case CallHangup.TYPE:
                return jsonb().fromJson(event, CallHangup.class);
            case CallInvite.TYPE:
                return jsonb().fromJson(event, CallInvite.class);
            case Direct.TYPE:
                return jsonb().fromJson(event, Direct.class);
            case ForwardedRoomKey.TYPE:
                return jsonb().fromJson(event, ForwardedRoomKey.class);
            case FullyRead.TYPE:
                return jsonb().fromJson(event, FullyRead.class);
            case IgnoredUserList.TYPE:
                return jsonb().fromJson(event, IgnoredUserList.class);
            case Presence.TYPE:
                return jsonb().fromJson(event, Presence.class);
            case Receipt.TYPE:
                return jsonb().fromJson(event, Receipt.class);
            case RoomAliases.TYPE:
                return jsonb().fromJson(event, RoomAliases.class);
            case RoomAvatar.TYPE:
                return jsonb().fromJson(event, RoomAvatar.class);
            case RoomCanonicalAlias.TYPE:
                return jsonb().fromJson(event, RoomCanonicalAlias.class);
            case RoomCreate.TYPE:
                return jsonb().fromJson(event, RoomCreate.class);
            case RoomGuestAccess.TYPE:
                return jsonb().fromJson(event, RoomGuestAccess.class);
            case RoomEncryption.TYPE:
                return jsonb().fromJson(event, RoomEncryption.class);
            case RoomEncrypted.TYPE:
                return jsonb().fromJson(event, RoomEncrypted.class);
            case RoomHistoryVisibility.TYPE:
                return jsonb().fromJson(event, RoomHistoryVisibility.class);
            case RoomJoinRules.TYPE:
                return jsonb().fromJson(event, RoomJoinRules.class);
            case RoomKey.TYPE:
                return jsonb().fromJson(event, RoomKey.class);
            case RoomKeyRequest.TYPE:
                return jsonb().fromJson(event, RoomKeyRequest.class);
            case RoomMember.TYPE:
                return jsonb().fromJson(event, RoomMember.class);
            case RoomMessage.TYPE:
                return jsonb().fromJson(event, RoomMessage.class);
            case RoomMessageFeedback.TYPE:
                return jsonb().fromJson(event, RoomMessageFeedback.class);
            case RoomName.TYPE:
                return jsonb().fromJson(event, RoomName.class);
            case RoomPinned.TYPE:
                return jsonb().fromJson(event, RoomPinned.class);
            case RoomPowerLevels.TYPE:
                return jsonb().fromJson(event, RoomPowerLevels.class);
            case RoomRedaction.TYPE:
                return jsonb().fromJson(event, RoomRedaction.class);
            case RoomThirdPartyInvite.TYPE:
                return jsonb().fromJson(event, RoomThirdPartyInvite.class);
            case RoomTopic.TYPE:
                return jsonb().fromJson(event, RoomTopic.class);
            case Sticker.TYPE:
                return jsonb().fromJson(event, Sticker.class);
            case Tag.TYPE:
                return jsonb().fromJson(event, Tag.class);
            case Typing.TYPE:
                return jsonb().fromJson(event, Typing.class);
            case RoomServerAcl.TYPE:
                return jsonb().fromJson(event, RoomServerAcl.class);
            case PushRules.TYPE:
                return jsonb().fromJson(event, PushRules.class);
            default:
                return parse(event, ctx, type);
        }
    }

    protected Event parse(String event, DeserializationContext ctx, String type) {
        return new RawEvent(jsonb().fromJson(event, Map.class), type);
    }
}
