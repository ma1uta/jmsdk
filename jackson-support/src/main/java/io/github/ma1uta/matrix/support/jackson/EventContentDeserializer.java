/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.event.CallAnswer;
import io.github.ma1uta.matrix.event.CallCandidates;
import io.github.ma1uta.matrix.event.CallHangup;
import io.github.ma1uta.matrix.event.CallInvite;
import io.github.ma1uta.matrix.event.Direct;
import io.github.ma1uta.matrix.event.Dummy;
import io.github.ma1uta.matrix.event.ForwardedRoomKey;
import io.github.ma1uta.matrix.event.FullyRead;
import io.github.ma1uta.matrix.event.IgnoredUserList;
import io.github.ma1uta.matrix.event.KeyVerificationAccept;
import io.github.ma1uta.matrix.event.KeyVerificationCancel;
import io.github.ma1uta.matrix.event.KeyVerificationKey;
import io.github.ma1uta.matrix.event.KeyVerificationMac;
import io.github.ma1uta.matrix.event.KeyVerificationRequest;
import io.github.ma1uta.matrix.event.KeyVerificationStart;
import io.github.ma1uta.matrix.event.PolicyRuleRoom;
import io.github.ma1uta.matrix.event.PolicyRuleServer;
import io.github.ma1uta.matrix.event.PolicyRuleUser;
import io.github.ma1uta.matrix.event.Presence;
import io.github.ma1uta.matrix.event.PushRules;
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
import io.github.ma1uta.matrix.event.RoomTombstone;
import io.github.ma1uta.matrix.event.RoomTopic;
import io.github.ma1uta.matrix.event.Sticker;
import io.github.ma1uta.matrix.event.Tag;
import io.github.ma1uta.matrix.event.Typing;
import io.github.ma1uta.matrix.event.content.CallAnswerContent;
import io.github.ma1uta.matrix.event.content.CallCandidatesContent;
import io.github.ma1uta.matrix.event.content.CallHangupContent;
import io.github.ma1uta.matrix.event.content.CallInviteContent;
import io.github.ma1uta.matrix.event.content.DirectContent;
import io.github.ma1uta.matrix.event.content.DummyContent;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.event.content.ForwardedRoomKeyContent;
import io.github.ma1uta.matrix.event.content.FullyReadContent;
import io.github.ma1uta.matrix.event.content.IgnoredUserListContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationAcceptContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationCancelContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationKeyContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationMacContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationRequestContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationStartContent;
import io.github.ma1uta.matrix.event.content.PolicyRuleRoomContent;
import io.github.ma1uta.matrix.event.content.PolicyRuleServerContent;
import io.github.ma1uta.matrix.event.content.PolicyRuleUserContent;
import io.github.ma1uta.matrix.event.content.PresenceContent;
import io.github.ma1uta.matrix.event.content.PushRulesContent;
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
import io.github.ma1uta.matrix.event.content.TombstoneContent;
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
            case CallAnswer.TYPE:
                return mapper.readValue(content, CallAnswerContent.class);
            case CallCandidates.TYPE:
                return mapper.readValue(content, CallCandidatesContent.class);
            case CallHangup.TYPE:
                return mapper.readValue(content, CallHangupContent.class);
            case CallInvite.TYPE:
                return mapper.readValue(content, CallInviteContent.class);
            case Direct.TYPE:
                return mapper.readValue(content, DirectContent.class);
            case Dummy.TYPE:
                return mapper.readValue(content, DummyContent.class);
            case ForwardedRoomKey.TYPE:
                return mapper.readValue(content, ForwardedRoomKeyContent.class);
            case FullyRead.TYPE:
                return mapper.readValue(content, FullyReadContent.class);
            case IgnoredUserList.TYPE:
                return mapper.readValue(content, IgnoredUserListContent.class);
            case KeyVerificationAccept.TYPE:
                return mapper.readValue(content, KeyVerificationAcceptContent.class);
            case KeyVerificationCancel.TYPE:
                return mapper.readValue(content, KeyVerificationCancelContent.class);
            case KeyVerificationKey.TYPE:
                return mapper.readValue(content, KeyVerificationKeyContent.class);
            case KeyVerificationMac.TYPE:
                return mapper.readValue(content, KeyVerificationMacContent.class);
            case KeyVerificationRequest.TYPE:
                return mapper.readValue(content, KeyVerificationRequestContent.class);
            case KeyVerificationStart.TYPE:
                return mapper.readValue(content, KeyVerificationStartContent.class);
            case Presence.TYPE:
                return mapper.readValue(content, PresenceContent.class);
            case PushRules.TYPE:
                return mapper.readValue(content, PushRulesContent.class);
            case Receipt.TYPE:
                return mapper.readValue(content, ReceiptContent.class);
            case RoomAliases.TYPE:
                return mapper.readValue(content, RoomAliasesContent.class);
            case RoomAvatar.TYPE:
                return mapper.readValue(content, RoomAvatarContent.class);
            case RoomCanonicalAlias.TYPE:
                return mapper.readValue(content, RoomCanonicalAliasContent.class);
            case RoomCreate.TYPE:
                return mapper.readValue(content, RoomCreateContent.class);
            case RoomGuestAccess.TYPE:
                return mapper.readValue(content, RoomGuestAccessContent.class);
            case RoomEncryption.TYPE:
                return mapper.readValue(content, RoomEncryptionContent.class);
            case RoomEncrypted.TYPE:
                return mapper.readValue(content, RoomEncryptedContent.class);
            case RoomHistoryVisibility.TYPE:
                return mapper.readValue(content, RoomHistoryVisibilityContent.class);
            case RoomJoinRules.TYPE:
                return mapper.readValue(content, RoomJoinRulesContent.class);
            case RoomKey.TYPE:
                return mapper.readValue(content, RoomKeyContent.class);
            case RoomKeyRequest.TYPE:
                return mapper.readValue(content, RoomKeyRequestContent.class);
            case RoomMember.TYPE:
                return mapper.readValue(content, RoomMemberContent.class);
            case RoomMessage.TYPE:
                return mapper.readValue(content, RoomMessageContent.class);
            case RoomMessageFeedback.TYPE:
                return mapper.readValue(content, RoomMessageFeedbackContent.class);
            case RoomName.TYPE:
                return mapper.readValue(content, RoomNameContent.class);
            case RoomPinned.TYPE:
                return mapper.readValue(content, RoomPinnedContent.class);
            case RoomPowerLevels.TYPE:
                return mapper.readValue(content, RoomPowerLevelsContent.class);
            case RoomRedaction.TYPE:
                return mapper.readValue(content, RoomRedactionContent.class);
            case RoomThirdPartyInvite.TYPE:
                return mapper.readValue(content, RoomThirdPartyInviteContent.class);
            case RoomTopic.TYPE:
                return mapper.readValue(content, RoomTopicContent.class);
            case Sticker.TYPE:
                return mapper.readValue(content, StickerContent.class);
            case Tag.TYPE:
                return mapper.readValue(content, TagContent.class);
            case Typing.TYPE:
                return mapper.readValue(content, TypingContent.class);
            case RoomTombstone.TYPE:
                return mapper.readValue(content, TombstoneContent.class);
            case RoomServerAcl.TYPE:
                return mapper.readValue(content, RoomServerAclContent.class);
            case PolicyRuleUser.TYPE:
                return mapper.readValue(content, PolicyRuleUserContent.class);
            case PolicyRuleRoom.TYPE:
                return mapper.readValue(content, PolicyRuleRoomContent.class);
            case PolicyRuleServer.TYPE:
                return mapper.readValue(content, PolicyRuleServerContent.class);
            default:
                return parse(content, mapper);
        }
    }

    protected EventContent parse(byte[] content, ObjectMapper mapper) throws IOException {
        return new RawEventContent(mapper.readValue(content, Map.class));
    }
}
