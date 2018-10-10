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

import static io.github.ma1uta.matrix.Event.EventType.CALL_ANSWER;
import static io.github.ma1uta.matrix.Event.EventType.CALL_CANDIDATES;
import static io.github.ma1uta.matrix.Event.EventType.CALL_HANGUP;
import static io.github.ma1uta.matrix.Event.EventType.CALL_INVITE;
import static io.github.ma1uta.matrix.Event.EventType.DIRECT;
import static io.github.ma1uta.matrix.Event.EventType.FORWARDED_ROOM_KEY;
import static io.github.ma1uta.matrix.Event.EventType.FULLY_READ;
import static io.github.ma1uta.matrix.Event.EventType.IGNORED_USER_LIST;
import static io.github.ma1uta.matrix.Event.EventType.PRESENCE;
import static io.github.ma1uta.matrix.Event.EventType.RECEIPT;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_ALIASES;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_AVATAR;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_CANONICAL_ALIAS;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_CREATE;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_ENCRIPTION;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_ENCRYPTED;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_GUEST_ACCESS;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_HISTORY_VISIBILITY;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_JOIN_RULES;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_KEY;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_KEY_REQUEST;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_MEMBER;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_MESSAGE;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_MESSAGE_FEEDBACK;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_NAME;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_PINNED_EVENTS;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_POWER_LEVELS;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_REDACTION;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_SERVER_ACL;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_THIRD_PARTY_INVITE;
import static io.github.ma1uta.matrix.Event.EventType.ROOM_TOPIC;
import static io.github.ma1uta.matrix.Event.EventType.STICKER;
import static io.github.ma1uta.matrix.Event.EventType.TAG;
import static io.github.ma1uta.matrix.Event.EventType.TYPING;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.EventContent;
import io.github.ma1uta.matrix.events.CallAnswer;
import io.github.ma1uta.matrix.events.CallCandidates;
import io.github.ma1uta.matrix.events.CallHangup;
import io.github.ma1uta.matrix.events.CallInvite;
import io.github.ma1uta.matrix.events.Direct;
import io.github.ma1uta.matrix.events.ForwardedRoomKey;
import io.github.ma1uta.matrix.events.FullyRead;
import io.github.ma1uta.matrix.events.IgnoredUserList;
import io.github.ma1uta.matrix.events.Presence;
import io.github.ma1uta.matrix.events.RawEventContent;
import io.github.ma1uta.matrix.events.Receipt;
import io.github.ma1uta.matrix.events.RoomAliases;
import io.github.ma1uta.matrix.events.RoomAvatar;
import io.github.ma1uta.matrix.events.RoomCanonicalAlias;
import io.github.ma1uta.matrix.events.RoomCreate;
import io.github.ma1uta.matrix.events.RoomEncryption;
import io.github.ma1uta.matrix.events.RoomGuestAccess;
import io.github.ma1uta.matrix.events.RoomHistoryVisibility;
import io.github.ma1uta.matrix.events.RoomJoinRules;
import io.github.ma1uta.matrix.events.RoomKey;
import io.github.ma1uta.matrix.events.RoomKeyRequest;
import io.github.ma1uta.matrix.events.RoomMember;
import io.github.ma1uta.matrix.events.RoomMessageFeedback;
import io.github.ma1uta.matrix.events.RoomName;
import io.github.ma1uta.matrix.events.RoomPinned;
import io.github.ma1uta.matrix.events.RoomPowerLevels;
import io.github.ma1uta.matrix.events.RoomRedaction;
import io.github.ma1uta.matrix.events.RoomServerAcl;
import io.github.ma1uta.matrix.events.RoomThirdPartyInvite;
import io.github.ma1uta.matrix.events.RoomTopic;
import io.github.ma1uta.matrix.events.Sticker;
import io.github.ma1uta.matrix.events.Tag;
import io.github.ma1uta.matrix.events.Typing;

/**
 * The deserializer of the event content.
 */
public class EventContentDeserializer {

    /**
     * Deserialize the event content.
     *
     * @param node  the json node with the event content.
     * @param type  the type of the event.
     * @param codec the json codec.
     * @return the deserialized event content or null
     * @throws JsonProcessingException when missing the `msgtype` of the message.
     */
    public EventContent deserialize(JsonNode node, String type, ObjectCodec codec) throws JsonProcessingException {
        if (node == null || node.isNull()) {
            return null;
        }

        switch (type) {
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
                return new EncryptedMessageDeserializer().deserialize(node, codec);
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
                return new RoomMessageDeserializer().deserialize(node, codec);
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
                return new RawEventContent(node);
        }
    }
}
