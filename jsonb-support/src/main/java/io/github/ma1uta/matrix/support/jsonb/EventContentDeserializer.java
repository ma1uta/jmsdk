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

import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.event.content.CallAnswerContent;
import io.github.ma1uta.matrix.event.content.CallCandidatesContent;
import io.github.ma1uta.matrix.event.content.CallHangupContent;
import io.github.ma1uta.matrix.event.content.CallInviteContent;
import io.github.ma1uta.matrix.event.content.DirectContent;
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

import javax.json.bind.serializer.DeserializationContext;
import javax.json.stream.JsonParser;

/**
 * The deserializer of the event content.
 */
public class EventContentDeserializer {

    /**
     * Deserialize the event content.
     *
     * @param parser JSON parser.
     * @param ctx    Deserialization context.
     * @param type   Event content type.
     * @return the deserialized event content.
     */
    public EventContent deserialize(JsonParser parser, DeserializationContext ctx, String type) {
        switch (type) {
            case CALL_ANSWER:
                return ctx.deserialize(CallAnswerContent.class, parser);
            case CALL_CANDIDATES:
                return ctx.deserialize(CallCandidatesContent.class, parser);
            case CALL_HANGUP:
                return ctx.deserialize(CallHangupContent.class, parser);
            case CALL_INVITE:
                return ctx.deserialize(CallInviteContent.class, parser);
            case DIRECT:
                return ctx.deserialize(DirectContent.class, parser);
            case FORWARDED_ROOM_KEY:
                return ctx.deserialize(ForwardedRoomKeyContent.class, parser);
            case FULLY_READ:
                return ctx.deserialize(FullyReadContent.class, parser);
            case IGNORED_USER_LIST:
                return ctx.deserialize(IgnoredUserListContent.class, parser);
            case PRESENCE:
                return ctx.deserialize(PresenceContent.class, parser);
            case RECEIPT:
                return ctx.deserialize(ReceiptContent.class, parser);
            case ROOM_ALIASES:
                return ctx.deserialize(RoomAliasesContent.class, parser);
            case ROOM_AVATAR:
                return ctx.deserialize(RoomAvatarContent.class, parser);
            case ROOM_CANONICAL_ALIAS:
                return ctx.deserialize(RoomCanonicalAliasContent.class, parser);
            case ROOM_CREATE:
                return ctx.deserialize(RoomCreateContent.class, parser);
            case ROOM_GUEST_ACCESS:
                return ctx.deserialize(RoomGuestAccessContent.class, parser);
            case ROOM_ENCRIPTION:
                return ctx.deserialize(RoomEncryptionContent.class, parser);
            case ROOM_ENCRYPTED:
                return ctx.deserialize(RoomEncryptedContent.class, parser);
            case ROOM_HISTORY_VISIBILITY:
                return ctx.deserialize(RoomHistoryVisibilityContent.class, parser);
            case ROOM_JOIN_RULES:
                return ctx.deserialize(RoomJoinRulesContent.class, parser);
            case ROOM_KEY:
                return ctx.deserialize(RoomKeyContent.class, parser);
            case ROOM_KEY_REQUEST:
                return ctx.deserialize(RoomKeyRequestContent.class, parser);
            case ROOM_MEMBER:
                return ctx.deserialize(RoomMemberContent.class, parser);
            case ROOM_MESSAGE:
                return ctx.deserialize(RoomMessageContent.class, parser);
            case ROOM_MESSAGE_FEEDBACK:
                return ctx.deserialize(RoomMessageFeedbackContent.class, parser);
            case ROOM_NAME:
                return ctx.deserialize(RoomNameContent.class, parser);
            case ROOM_PINNED_EVENTS:
                return ctx.deserialize(RoomPinnedContent.class, parser);
            case ROOM_POWER_LEVELS:
                return ctx.deserialize(RoomPowerLevelsContent.class, parser);
            case ROOM_REDACTION:
                return ctx.deserialize(RoomRedactionContent.class, parser);
            case ROOM_THIRD_PARTY_INVITE:
                return ctx.deserialize(RoomThirdPartyInviteContent.class, parser);
            case ROOM_TOPIC:
                return ctx.deserialize(RoomTopicContent.class, parser);
            case STICKER:
                return ctx.deserialize(StickerContent.class, parser);
            case TAG:
                return ctx.deserialize(TagContent.class, parser);
            case TYPING:
                return ctx.deserialize(TypingContent.class, parser);
            case ROOM_SERVER_ACL:
                return ctx.deserialize(RoomServerAclContent.class, parser);
            default:
                return parse(parser, ctx, type);
        }
    }

    protected EventContent parse(JsonParser parser, DeserializationContext ctx, String type) {
        return new RawEventContent(parser.getObject(), type);
    }
}
