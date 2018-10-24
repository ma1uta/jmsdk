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

package io.github.ma1uta.matrix.support.jackson;

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
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.io.IOException;
import java.util.Map;

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
            return parse(node, codec, ctxt, null);
        }

        switch (type.asText()) {
            case CALL_ANSWER:
                return codec.treeToValue(node, CallAnswer.class);
            case CALL_CANDIDATES:
                return codec.treeToValue(node, CallCandidates.class);
            case CALL_HANGUP:
                return codec.treeToValue(node, CallHangup.class);
            case CALL_INVITE:
                return codec.treeToValue(node, CallInvite.class);
            case DIRECT:
                return codec.treeToValue(node, Direct.class);
            case FORWARDED_ROOM_KEY:
                return codec.treeToValue(node, ForwardedRoomKey.class);
            case FULLY_READ:
                return codec.treeToValue(node, FullyRead.class);
            case IGNORED_USER_LIST:
                return codec.treeToValue(node, IgnoredUserList.class);
            case PRESENCE:
                return codec.treeToValue(node, Presence.class);
            case RECEIPT:
                return codec.treeToValue(node, Receipt.class);
            case ROOM_ALIASES:
                return codec.treeToValue(node, RoomAliases.class);
            case ROOM_AVATAR:
                return codec.treeToValue(node, RoomAvatar.class);
            case ROOM_CANONICAL_ALIAS:
                return codec.treeToValue(node, RoomCanonicalAlias.class);
            case ROOM_CREATE:
                return codec.treeToValue(node, RoomCreate.class);
            case ROOM_GUEST_ACCESS:
                return codec.treeToValue(node, RoomGuestAccess.class);
            case ROOM_ENCRIPTION:
                return codec.treeToValue(node, RoomEncryption.class);
            case ROOM_ENCRYPTED:
                return codec.treeToValue(node, RoomEncrypted.class);
            case ROOM_HISTORY_VISIBILITY:
                return codec.treeToValue(node, RoomHistoryVisibility.class);
            case ROOM_JOIN_RULES:
                return codec.treeToValue(node, RoomJoinRules.class);
            case ROOM_KEY:
                return codec.treeToValue(node, RoomKey.class);
            case ROOM_KEY_REQUEST:
                return codec.treeToValue(node, RoomKeyRequest.class);
            case ROOM_MEMBER:
                return codec.treeToValue(node, RoomMember.class);
            case ROOM_MESSAGE:
                return codec.treeToValue(node, RoomMessage.class);
            case ROOM_MESSAGE_FEEDBACK:
                return codec.treeToValue(node, RoomMessageFeedback.class);
            case ROOM_NAME:
                return codec.treeToValue(node, RoomName.class);
            case ROOM_PINNED_EVENTS:
                return codec.treeToValue(node, RoomPinned.class);
            case ROOM_POWER_LEVELS:
                return codec.treeToValue(node, RoomPowerLevels.class);
            case ROOM_REDACTION:
                return codec.treeToValue(node, RoomRedaction.class);
            case ROOM_THIRD_PARTY_INVITE:
                return codec.treeToValue(node, RoomThirdPartyInvite.class);
            case ROOM_TOPIC:
                return codec.treeToValue(node, RoomTopic.class);
            case STICKER:
                return codec.treeToValue(node, Sticker.class);
            case TAG:
                return codec.treeToValue(node, Tag.class);
            case TYPING:
                return codec.treeToValue(node, Typing.class);
            case ROOM_SERVER_ACL:
                return codec.treeToValue(node, RoomServerAcl.class);
            default:
                return parse(node, codec, ctxt, type.asText());
        }
    }

    protected Event parse(JsonNode jsonNode, ObjectCodec codec, DeserializationContext ctxt, String type) throws JsonProcessingException {
        return new RawEvent(codec.treeToValue(jsonNode, Map.class), type);
    }
}
