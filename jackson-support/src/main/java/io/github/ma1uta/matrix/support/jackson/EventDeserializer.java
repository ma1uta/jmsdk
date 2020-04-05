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
import io.github.ma1uta.matrix.event.Dummy;
import io.github.ma1uta.matrix.event.Event;
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
import io.github.ma1uta.matrix.event.RoomTombstone;
import io.github.ma1uta.matrix.event.RoomTopic;
import io.github.ma1uta.matrix.event.Sticker;
import io.github.ma1uta.matrix.event.Tag;
import io.github.ma1uta.matrix.event.Typing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Event deserializer.
 */
public class EventDeserializer extends JsonDeserializer<Event> {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventDeserializer.class);

    @Override
    public Event deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        JsonNode type = node.get("type");
        if (type == null || !type.isTextual()) {
            return parse(node, codec, ctxt, null);
        }

        try {
            switch (type.asText()) {
                case CallAnswer.TYPE:
                    return codec.treeToValue(node, CallAnswer.class);
                case CallCandidates.TYPE:
                    return codec.treeToValue(node, CallCandidates.class);
                case CallHangup.TYPE:
                    return codec.treeToValue(node, CallHangup.class);
                case CallInvite.TYPE:
                    return codec.treeToValue(node, CallInvite.class);
                case Direct.TYPE:
                    return codec.treeToValue(node, Direct.class);
                case ForwardedRoomKey.TYPE:
                    return codec.treeToValue(node, ForwardedRoomKey.class);
                case FullyRead.TYPE:
                    return codec.treeToValue(node, FullyRead.class);
                case IgnoredUserList.TYPE:
                    return codec.treeToValue(node, IgnoredUserList.class);
                case Presence.TYPE:
                    return codec.treeToValue(node, Presence.class);
                case Receipt.TYPE:
                    return codec.treeToValue(node, Receipt.class);
                case RoomAliases.TYPE:
                    return codec.treeToValue(node, RoomAliases.class);
                case RoomAvatar.TYPE:
                    return codec.treeToValue(node, RoomAvatar.class);
                case RoomCanonicalAlias.TYPE:
                    return codec.treeToValue(node, RoomCanonicalAlias.class);
                case RoomCreate.TYPE:
                    return codec.treeToValue(node, RoomCreate.class);
                case RoomGuestAccess.TYPE:
                    return codec.treeToValue(node, RoomGuestAccess.class);
                case RoomEncryption.TYPE:
                    return codec.treeToValue(node, RoomEncryption.class);
                case RoomEncrypted.TYPE:
                    return codec.treeToValue(node, RoomEncrypted.class);
                case RoomHistoryVisibility.TYPE:
                    return codec.treeToValue(node, RoomHistoryVisibility.class);
                case RoomJoinRules.TYPE:
                    return codec.treeToValue(node, RoomJoinRules.class);
                case RoomKey.TYPE:
                    return codec.treeToValue(node, RoomKey.class);
                case RoomKeyRequest.TYPE:
                    return codec.treeToValue(node, RoomKeyRequest.class);
                case RoomMember.TYPE:
                    return codec.treeToValue(node, RoomMember.class);
                case RoomMessage.TYPE:
                    return codec.treeToValue(node, RoomMessage.class);
                case RoomMessageFeedback.TYPE:
                    return codec.treeToValue(node, RoomMessageFeedback.class);
                case RoomName.TYPE:
                    return codec.treeToValue(node, RoomName.class);
                case RoomPinned.TYPE:
                    return codec.treeToValue(node, RoomPinned.class);
                case RoomPowerLevels.TYPE:
                    return codec.treeToValue(node, RoomPowerLevels.class);
                case RoomRedaction.TYPE:
                    return codec.treeToValue(node, RoomRedaction.class);
                case RoomThirdPartyInvite.TYPE:
                    return codec.treeToValue(node, RoomThirdPartyInvite.class);
                case RoomTopic.TYPE:
                    return codec.treeToValue(node, RoomTopic.class);
                case Sticker.TYPE:
                    return codec.treeToValue(node, Sticker.class);
                case Tag.TYPE:
                    return codec.treeToValue(node, Tag.class);
                case Typing.TYPE:
                    return codec.treeToValue(node, Typing.class);
                case RoomServerAcl.TYPE:
                    return codec.treeToValue(node, RoomServerAcl.class);
                case PushRules.TYPE:
                    return codec.treeToValue(node, PushRules.class);
                case RoomTombstone.TYPE:
                    return codec.treeToValue(node, RoomTombstone.class);
                case Dummy.TYPE:
                    return codec.treeToValue(node, Dummy.class);
                case KeyVerificationAccept.TYPE:
                    return codec.treeToValue(node, KeyVerificationAccept.class);
                case KeyVerificationCancel.TYPE:
                    return codec.treeToValue(node, KeyVerificationCancel.class);
                case KeyVerificationKey.TYPE:
                    return codec.treeToValue(node, KeyVerificationKey.class);
                case KeyVerificationMac.TYPE:
                    return codec.treeToValue(node, KeyVerificationMac.class);
                case KeyVerificationRequest.TYPE:
                    return codec.treeToValue(node, KeyVerificationRequest.class);
                case KeyVerificationStart.TYPE:
                    return codec.treeToValue(node, KeyVerificationStart.class);
                case PolicyRuleRoom.TYPE:
                    return codec.treeToValue(node, PolicyRuleRoom.class);
                case PolicyRuleUser.TYPE:
                    return codec.treeToValue(node, PolicyRuleUser.class);
                case PolicyRuleServer.TYPE:
                    return codec.treeToValue(node, PolicyRuleServer.class);
                default:
                    return parse(node, codec, ctxt, type.asText());
            }
        } catch (IOException e) {
            LOGGER.error("Unable to parse event", e);
            return parse(node, codec, ctxt, type.asText());
        }
    }

    protected Event parse(JsonNode jsonNode, ObjectCodec codec, DeserializationContext ctxt, String type) throws JsonProcessingException {
        return new RawEvent(codec.treeToValue(jsonNode, Map.class), type);
    }
}
