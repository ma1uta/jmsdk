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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
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

import java.io.IOException;

/**
 * Event deserializer.
 */
public class EventDeserializer extends JsonDeserializer<Event> {

    @Override
    public Event deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode type = node.get("type");
        if (type == null || !type.isTextual()) {
            return parse(node, ctxt, null);
        }

        JsonParser eventParser = codec.getJsonFactory().createParser(node.toString());
        switch (type.asText()) {
            case CALL_ANSWER:
                return codec.readValue(eventParser, CallAnswer.class);
            case CALL_CANDIDATES:
                return codec.readValue(eventParser, CallCandidates.class);
            case CALL_HANGUP:
                return codec.readValue(eventParser, CallHangup.class);
            case CALL_INVITE:
                return codec.readValue(eventParser, CallInvite.class);
            case DIRECT:
                return codec.readValue(eventParser, Direct.class);
            case FORWARDED_ROOM_KEY:
                return codec.readValue(eventParser, ForwardedRoomKey.class);
            case FULLY_READ:
                return codec.readValue(eventParser, FullyRead.class);
            case IGNORED_USER_LIST:
                return codec.readValue(eventParser, IgnoredUserList.class);
            case PRESENCE:
                return codec.readValue(eventParser, Presence.class);
            case RECEIPT:
                return codec.readValue(eventParser, Receipt.class);
            case ROOM_ALIASES:
                return codec.readValue(eventParser, RoomAliases.class);
            case ROOM_AVATAR:
                return codec.readValue(eventParser, RoomAvatar.class);
            case ROOM_CANONICAL_ALIAS:
                return codec.readValue(eventParser, RoomCanonicalAlias.class);
            case ROOM_CREATE:
                return codec.readValue(eventParser, RoomCreate.class);
            case ROOM_GUEST_ACCESS:
                return codec.readValue(eventParser, RoomGuestAccess.class);
            case ROOM_ENCRIPTION:
                return codec.readValue(eventParser, RoomEncryption.class);
            case ROOM_ENCRYPTED:
                return codec.readValue(eventParser, RoomEncrypted.class);
            case ROOM_HISTORY_VISIBILITY:
                return codec.readValue(eventParser, RoomHistoryVisibility.class);
            case ROOM_JOIN_RULES:
                return codec.readValue(eventParser, RoomJoinRules.class);
            case ROOM_KEY:
                return codec.readValue(eventParser, RoomKey.class);
            case ROOM_KEY_REQUEST:
                return codec.readValue(eventParser, RoomKeyRequest.class);
            case ROOM_MEMBER:
                return codec.readValue(eventParser, RoomMember.class);
            case ROOM_MESSAGE:
                return codec.readValue(eventParser, RoomMessage.class);
            case ROOM_MESSAGE_FEEDBACK:
                return codec.readValue(eventParser, RoomMessageFeedback.class);
            case ROOM_NAME:
                return codec.readValue(eventParser, RoomName.class);
            case ROOM_PINNED_EVENTS:
                return codec.readValue(eventParser, RoomPinned.class);
            case ROOM_POWER_LEVELS:
                return codec.readValue(eventParser, RoomPowerLevels.class);
            case ROOM_REDACTION:
                return codec.readValue(eventParser, RoomRedaction.class);
            case ROOM_THIRD_PARTY_INVITE:
                return codec.readValue(eventParser, RoomThirdPartyInvite.class);
            case ROOM_TOPIC:
                return codec.readValue(eventParser, RoomTopic.class);
            case STICKER:
                return codec.readValue(eventParser, Sticker.class);
            case TAG:
                return codec.readValue(eventParser, Tag.class);
            case TYPING:
                return codec.readValue(eventParser, Typing.class);
            case ROOM_SERVER_ACL:
                return codec.readValue(eventParser, RoomServerAcl.class);
            default:
                return parse(node, ctxt, type.asText());
        }
    }

    protected Event parse(JsonNode jsonNode, DeserializationContext ctxt, String type) {
        RawEvent event = new RawEvent();
        event.setType(type);
        event.setProperties(jsonNode);
        event.setContent(new RawEventContent(jsonNode.get("content")));
        return event;
    }
}
