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

import static io.github.ma1uta.matrix.event.Event.EventType.CALL_ANSWER;
import static io.github.ma1uta.matrix.event.Event.EventType.CALL_CANDIDATES;
import static io.github.ma1uta.matrix.event.Event.EventType.CALL_HANGUP;
import static io.github.ma1uta.matrix.event.Event.EventType.CALL_INVITE;
import static io.github.ma1uta.matrix.event.Event.EventType.DIRECT;
import static io.github.ma1uta.matrix.event.Event.EventType.FORWARDED_ROOM_KEY;
import static io.github.ma1uta.matrix.event.Event.EventType.FULLY_READ;
import static io.github.ma1uta.matrix.event.Event.EventType.IGNORED_USER_LIST;
import static io.github.ma1uta.matrix.event.Event.EventType.PRESENCE;
import static io.github.ma1uta.matrix.event.Event.EventType.RECEIPT;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_ALIASES;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_AVATAR;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_CANONICAL_ALIAS;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_CREATE;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_ENCRIPTION;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_ENCRYPTED;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_GUEST_ACCESS;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_HISTORY_VISIBILITY;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_JOIN_RULES;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_KEY;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_KEY_REQUEST;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_MEMBER;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_MESSAGE;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_MESSAGE_FEEDBACK;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_NAME;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_PINNED_EVENTS;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_POWER_LEVELS;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_REDACTION;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_SERVER_ACL;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_THIRD_PARTY_INVITE;
import static io.github.ma1uta.matrix.event.Event.EventType.ROOM_TOPIC;
import static io.github.ma1uta.matrix.event.Event.EventType.STICKER;
import static io.github.ma1uta.matrix.event.Event.EventType.TAG;
import static io.github.ma1uta.matrix.event.Event.EventType.TYPING;

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
import io.github.ma1uta.matrix.event.content.RawEventContent;

import java.lang.reflect.Type;
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
        if (object.get("type") == null || object.isNull("type")) {
            return parse(object, ctx, null);
        }
        String type = object.getString("type");
        String event = object.toString();

        switch (type) {
            case CALL_ANSWER:
                return jsonb().fromJson(event, CallAnswer.class);
            case CALL_CANDIDATES:
                return jsonb().fromJson(event, CallCandidates.class);
            case CALL_HANGUP:
                return jsonb().fromJson(event, CallHangup.class);
            case CALL_INVITE:
                return jsonb().fromJson(event, CallInvite.class);
            case DIRECT:
                return jsonb().fromJson(event, Direct.class);
            case FORWARDED_ROOM_KEY:
                return jsonb().fromJson(event, ForwardedRoomKey.class);
            case FULLY_READ:
                return jsonb().fromJson(event, FullyRead.class);
            case IGNORED_USER_LIST:
                return jsonb().fromJson(event, IgnoredUserList.class);
            case PRESENCE:
                return jsonb().fromJson(event, Presence.class);
            case RECEIPT:
                return jsonb().fromJson(event, Receipt.class);
            case ROOM_ALIASES:
                return jsonb().fromJson(event, RoomAliases.class);
            case ROOM_AVATAR:
                return jsonb().fromJson(event, RoomAvatar.class);
            case ROOM_CANONICAL_ALIAS:
                return jsonb().fromJson(event, RoomCanonicalAlias.class);
            case ROOM_CREATE:
                return jsonb().fromJson(event, RoomCreate.class);
            case ROOM_GUEST_ACCESS:
                return jsonb().fromJson(event, RoomGuestAccess.class);
            case ROOM_ENCRIPTION:
                return jsonb().fromJson(event, RoomEncryption.class);
            case ROOM_ENCRYPTED:
                return jsonb().fromJson(event, RoomEncrypted.class);
            case ROOM_HISTORY_VISIBILITY:
                return jsonb().fromJson(event, RoomHistoryVisibility.class);
            case ROOM_JOIN_RULES:
                return jsonb().fromJson(event, RoomJoinRules.class);
            case ROOM_KEY:
                return jsonb().fromJson(event, RoomKey.class);
            case ROOM_KEY_REQUEST:
                return jsonb().fromJson(event, RoomKeyRequest.class);
            case ROOM_MEMBER:
                return jsonb().fromJson(event, RoomMember.class);
            case ROOM_MESSAGE:
                return jsonb().fromJson(event, RoomMessage.class);
            case ROOM_MESSAGE_FEEDBACK:
                return jsonb().fromJson(event, RoomMessageFeedback.class);
            case ROOM_NAME:
                return jsonb().fromJson(event, RoomName.class);
            case ROOM_PINNED_EVENTS:
                return jsonb().fromJson(event, RoomPinned.class);
            case ROOM_POWER_LEVELS:
                return jsonb().fromJson(event, RoomPowerLevels.class);
            case ROOM_REDACTION:
                return jsonb().fromJson(event, RoomRedaction.class);
            case ROOM_THIRD_PARTY_INVITE:
                return jsonb().fromJson(event, RoomThirdPartyInvite.class);
            case ROOM_TOPIC:
                return jsonb().fromJson(event, RoomTopic.class);
            case STICKER:
                return jsonb().fromJson(event, Sticker.class);
            case TAG:
                return jsonb().fromJson(event, Tag.class);
            case TYPING:
                return jsonb().fromJson(event, Typing.class);
            case ROOM_SERVER_ACL:
                return jsonb().fromJson(event, RoomServerAcl.class);
            default:
                return parse(object, ctx, type);
        }
    }

    protected Event parse(JsonObject object, DeserializationContext ctx, String type) {
        RawEvent event = new RawEvent();
        event.setType(type);
        event.setProperties(object);
        event.setContent(new RawEventContent(object.get("content")));
        return event;
    }
}
