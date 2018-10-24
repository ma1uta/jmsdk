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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.event.content.CallAnswerContent;
import io.github.ma1uta.matrix.event.content.CallCandidatesContent;
import io.github.ma1uta.matrix.event.content.CallHangupContent;
import io.github.ma1uta.matrix.event.content.CallInviteContent;
import io.github.ma1uta.matrix.event.content.DirectContent;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.event.content.ForwardedRoomKeyContent;
import io.github.ma1uta.matrix.event.content.FullyReadContent;
import io.github.ma1uta.matrix.event.content.IgnoredUserListContent;
import io.github.ma1uta.matrix.event.content.PresenceContent;
import io.github.ma1uta.matrix.event.content.RawEventContent;
import io.github.ma1uta.matrix.event.content.ReceiptContent;
import io.github.ma1uta.matrix.event.content.RoomAliasesContent;
import io.github.ma1uta.matrix.event.content.RoomAvatarContent;
import io.github.ma1uta.matrix.event.content.RoomCanonicalAliasContent;
import io.github.ma1uta.matrix.event.content.RoomCreateContent;
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.content.RoomEncryptionContent;
import io.github.ma1uta.matrix.event.content.RoomGuestAccessContent;
import io.github.ma1uta.matrix.event.content.RoomHistoryVisibilityContent;
import io.github.ma1uta.matrix.event.content.RoomJoinRulesContent;
import io.github.ma1uta.matrix.event.content.RoomKeyContent;
import io.github.ma1uta.matrix.event.content.RoomKeyRequestContent;
import io.github.ma1uta.matrix.event.content.RoomMemberContent;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.content.RoomMessageFeedbackContent;
import io.github.ma1uta.matrix.event.content.RoomNameContent;
import io.github.ma1uta.matrix.event.content.RoomPinnedContent;
import io.github.ma1uta.matrix.event.content.RoomPowerLevelsContent;
import io.github.ma1uta.matrix.event.content.RoomRedactionContent;
import io.github.ma1uta.matrix.event.content.RoomServerAclContent;
import io.github.ma1uta.matrix.event.content.RoomThirdPartyInviteContent;
import io.github.ma1uta.matrix.event.content.RoomTopicContent;
import io.github.ma1uta.matrix.event.content.StickerContent;
import io.github.ma1uta.matrix.event.content.TagContent;
import io.github.ma1uta.matrix.event.content.TypingContent;

import java.util.Map;

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
                return codec.treeToValue(node, CallAnswerContent.class);
            case CALL_CANDIDATES:
                return codec.treeToValue(node, CallCandidatesContent.class);
            case CALL_HANGUP:
                return codec.treeToValue(node, CallHangupContent.class);
            case CALL_INVITE:
                return codec.treeToValue(node, CallInviteContent.class);
            case DIRECT:
                return codec.treeToValue(node, DirectContent.class);
            case FORWARDED_ROOM_KEY:
                return codec.treeToValue(node, ForwardedRoomKeyContent.class);
            case FULLY_READ:
                return codec.treeToValue(node, FullyReadContent.class);
            case IGNORED_USER_LIST:
                return codec.treeToValue(node, IgnoredUserListContent.class);
            case PRESENCE:
                return codec.treeToValue(node, PresenceContent.class);
            case RECEIPT:
                return codec.treeToValue(node, ReceiptContent.class);
            case ROOM_ALIASES:
                return codec.treeToValue(node, RoomAliasesContent.class);
            case ROOM_AVATAR:
                return codec.treeToValue(node, RoomAvatarContent.class);
            case ROOM_CANONICAL_ALIAS:
                return codec.treeToValue(node, RoomCanonicalAliasContent.class);
            case ROOM_CREATE:
                return codec.treeToValue(node, RoomCreateContent.class);
            case ROOM_GUEST_ACCESS:
                return codec.treeToValue(node, RoomGuestAccessContent.class);
            case ROOM_ENCRIPTION:
                return codec.treeToValue(node, RoomEncryptionContent.class);
            case ROOM_ENCRYPTED:
                return codec.treeToValue(node, RoomEncryptedContent.class);
            case ROOM_HISTORY_VISIBILITY:
                return codec.treeToValue(node, RoomHistoryVisibilityContent.class);
            case ROOM_JOIN_RULES:
                return codec.treeToValue(node, RoomJoinRulesContent.class);
            case ROOM_KEY:
                return codec.treeToValue(node, RoomKeyContent.class);
            case ROOM_KEY_REQUEST:
                return codec.treeToValue(node, RoomKeyRequestContent.class);
            case ROOM_MEMBER:
                return codec.treeToValue(node, RoomMemberContent.class);
            case ROOM_MESSAGE:
                return codec.treeToValue(node, RoomMessageContent.class);
            case ROOM_MESSAGE_FEEDBACK:
                return codec.treeToValue(node, RoomMessageFeedbackContent.class);
            case ROOM_NAME:
                return codec.treeToValue(node, RoomNameContent.class);
            case ROOM_PINNED_EVENTS:
                return codec.treeToValue(node, RoomPinnedContent.class);
            case ROOM_POWER_LEVELS:
                return codec.treeToValue(node, RoomPowerLevelsContent.class);
            case ROOM_REDACTION:
                return codec.treeToValue(node, RoomRedactionContent.class);
            case ROOM_THIRD_PARTY_INVITE:
                return codec.treeToValue(node, RoomThirdPartyInviteContent.class);
            case ROOM_TOPIC:
                return codec.treeToValue(node, RoomTopicContent.class);
            case STICKER:
                return codec.treeToValue(node, StickerContent.class);
            case TAG:
                return codec.treeToValue(node, TagContent.class);
            case TYPING:
                return codec.treeToValue(node, TypingContent.class);
            case ROOM_SERVER_ACL:
                return codec.treeToValue(node, RoomServerAclContent.class);
            default:
                return parse(node, codec);
        }
    }

    protected EventContent parse(JsonNode jsonNode, ObjectCodec codec) throws JsonProcessingException {
        return new RawEventContent(codec.treeToValue(jsonNode, Map.class));
    }
}
