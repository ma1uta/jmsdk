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

import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.Map;

/**
 * The deserializer of the event content.
 */
public class EventContentDeserializer {

    /**
     * Deserialize the event content.
     *
     * @param content the content to deserialize.
     * @param type    the type of the event.
     * @param mapper  the object mapper.
     * @return the deserialized event content or null
     * @throws IOException when deserialization was failed.
     */
    public EventContent deserialize(byte[] content, String type, ObjectMapper mapper) throws IOException {
        switch (type) {
            case CALL_ANSWER:
                return mapper.readValue(content, CallAnswerContent.class);
            case CALL_CANDIDATES:
                return mapper.readValue(content, CallCandidatesContent.class);
            case CALL_HANGUP:
                return mapper.readValue(content, CallHangupContent.class);
            case CALL_INVITE:
                return mapper.readValue(content, CallInviteContent.class);
            case DIRECT:
                return mapper.readValue(content, DirectContent.class);
            case FORWARDED_ROOM_KEY:
                return mapper.readValue(content, ForwardedRoomKeyContent.class);
            case FULLY_READ:
                return mapper.readValue(content, FullyReadContent.class);
            case IGNORED_USER_LIST:
                return mapper.readValue(content, IgnoredUserListContent.class);
            case PRESENCE:
                return mapper.readValue(content, PresenceContent.class);
            case RECEIPT:
                return mapper.readValue(content, ReceiptContent.class);
            case ROOM_ALIASES:
                return mapper.readValue(content, RoomAliasesContent.class);
            case ROOM_AVATAR:
                return mapper.readValue(content, RoomAvatarContent.class);
            case ROOM_CANONICAL_ALIAS:
                return mapper.readValue(content, RoomCanonicalAliasContent.class);
            case ROOM_CREATE:
                return mapper.readValue(content, RoomCreateContent.class);
            case ROOM_GUEST_ACCESS:
                return mapper.readValue(content, RoomGuestAccessContent.class);
            case ROOM_ENCRIPTION:
                return mapper.readValue(content, RoomEncryptionContent.class);
            case ROOM_ENCRYPTED:
                return mapper.readValue(content, RoomEncryptedContent.class);
            case ROOM_HISTORY_VISIBILITY:
                return mapper.readValue(content, RoomHistoryVisibilityContent.class);
            case ROOM_JOIN_RULES:
                return mapper.readValue(content, RoomJoinRulesContent.class);
            case ROOM_KEY:
                return mapper.readValue(content, RoomKeyContent.class);
            case ROOM_KEY_REQUEST:
                return mapper.readValue(content, RoomKeyRequestContent.class);
            case ROOM_MEMBER:
                return mapper.readValue(content, RoomMemberContent.class);
            case ROOM_MESSAGE:
                return mapper.readValue(content, RoomMessageContent.class);
            case ROOM_MESSAGE_FEEDBACK:
                return mapper.readValue(content, RoomMessageFeedbackContent.class);
            case ROOM_NAME:
                return mapper.readValue(content, RoomNameContent.class);
            case ROOM_PINNED_EVENTS:
                return mapper.readValue(content, RoomPinnedContent.class);
            case ROOM_POWER_LEVELS:
                return mapper.readValue(content, RoomPowerLevelsContent.class);
            case ROOM_REDACTION:
                return mapper.readValue(content, RoomRedactionContent.class);
            case ROOM_THIRD_PARTY_INVITE:
                return mapper.readValue(content, RoomThirdPartyInviteContent.class);
            case ROOM_TOPIC:
                return mapper.readValue(content, RoomTopicContent.class);
            case STICKER:
                return mapper.readValue(content, StickerContent.class);
            case TAG:
                return mapper.readValue(content, TagContent.class);
            case TYPING:
                return mapper.readValue(content, TypingContent.class);
            case ROOM_SERVER_ACL:
                return mapper.readValue(content, RoomServerAclContent.class);
            default:
                return parse(content, mapper);
        }
    }

    protected EventContent parse(byte[] content, ObjectMapper mapper) throws IOException {
        return new RawEventContent(mapper.readValue(content, Map.class));
    }
}
