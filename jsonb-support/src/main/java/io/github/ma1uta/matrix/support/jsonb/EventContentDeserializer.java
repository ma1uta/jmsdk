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
import io.github.ma1uta.matrix.events.RoomEncrypted;
import io.github.ma1uta.matrix.events.RoomEncryption;
import io.github.ma1uta.matrix.events.RoomGuestAccess;
import io.github.ma1uta.matrix.events.RoomHistoryVisibility;
import io.github.ma1uta.matrix.events.RoomJoinRules;
import io.github.ma1uta.matrix.events.RoomKey;
import io.github.ma1uta.matrix.events.RoomKeyRequest;
import io.github.ma1uta.matrix.events.RoomMember;
import io.github.ma1uta.matrix.events.RoomMessage;
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
                return ctx.deserialize(CallAnswer.class, parser);
            case CALL_CANDIDATES:
                return ctx.deserialize(CallCandidates.class, parser);
            case CALL_HANGUP:
                return ctx.deserialize(CallHangup.class, parser);
            case CALL_INVITE:
                return ctx.deserialize(CallInvite.class, parser);
            case DIRECT:
                return ctx.deserialize(Direct.class, parser);
            case FORWARDED_ROOM_KEY:
                return ctx.deserialize(ForwardedRoomKey.class, parser);
            case FULLY_READ:
                return ctx.deserialize(FullyRead.class, parser);
            case IGNORED_USER_LIST:
                return ctx.deserialize(IgnoredUserList.class, parser);
            case PRESENCE:
                return ctx.deserialize(Presence.class, parser);
            case RECEIPT:
                return ctx.deserialize(Receipt.class, parser);
            case ROOM_ALIASES:
                return ctx.deserialize(RoomAliases.class, parser);
            case ROOM_AVATAR:
                return ctx.deserialize(RoomAvatar.class, parser);
            case ROOM_CANONICAL_ALIAS:
                return ctx.deserialize(RoomCanonicalAlias.class, parser);
            case ROOM_CREATE:
                return ctx.deserialize(RoomCreate.class, parser);
            case ROOM_GUEST_ACCESS:
                return ctx.deserialize(RoomGuestAccess.class, parser);
            case ROOM_ENCRIPTION:
                return ctx.deserialize(RoomEncryption.class, parser);
            case ROOM_ENCRYPTED:
                return ctx.deserialize(RoomEncrypted.class, parser);
            case ROOM_HISTORY_VISIBILITY:
                return ctx.deserialize(RoomHistoryVisibility.class, parser);
            case ROOM_JOIN_RULES:
                return ctx.deserialize(RoomJoinRules.class, parser);
            case ROOM_KEY:
                return ctx.deserialize(RoomKey.class, parser);
            case ROOM_KEY_REQUEST:
                return ctx.deserialize(RoomKeyRequest.class, parser);
            case ROOM_MEMBER:
                return ctx.deserialize(RoomMember.class, parser);
            case ROOM_MESSAGE:
                return ctx.deserialize(RoomMessage.class, parser);
            case ROOM_MESSAGE_FEEDBACK:
                return ctx.deserialize(RoomMessageFeedback.class, parser);
            case ROOM_NAME:
                return ctx.deserialize(RoomName.class, parser);
            case ROOM_PINNED_EVENTS:
                return ctx.deserialize(RoomPinned.class, parser);
            case ROOM_POWER_LEVELS:
                return ctx.deserialize(RoomPowerLevels.class, parser);
            case ROOM_REDACTION:
                return ctx.deserialize(RoomRedaction.class, parser);
            case ROOM_THIRD_PARTY_INVITE:
                return ctx.deserialize(RoomThirdPartyInvite.class, parser);
            case ROOM_TOPIC:
                return ctx.deserialize(RoomTopic.class, parser);
            case STICKER:
                return ctx.deserialize(Sticker.class, parser);
            case TAG:
                return ctx.deserialize(Tag.class, parser);
            case TYPING:
                return ctx.deserialize(Typing.class, parser);
            case ROOM_SERVER_ACL:
                return ctx.deserialize(RoomServerAcl.class, parser);
            default:
                return parse(parser, ctx, type);
        }
    }

    protected EventContent parse(JsonParser parser, DeserializationContext ctx, String type) {
        return new RawEventContent(parser.getObject(), type);
    }
}
