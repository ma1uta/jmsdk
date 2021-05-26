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

package io.github.ma1uta.matrix.support.jsonb.mapper;

import io.github.ma1uta.matrix.common.Signed;
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
import io.github.ma1uta.matrix.event.RoomEvent;
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
import io.github.ma1uta.matrix.event.StateEvent;
import io.github.ma1uta.matrix.event.Sticker;
import io.github.ma1uta.matrix.event.Tag;
import io.github.ma1uta.matrix.event.Typing;
import io.github.ma1uta.matrix.event.Unsigned;
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
import io.github.ma1uta.matrix.event.encrypted.MegolmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.OlmEncryptedContent;
import io.github.ma1uta.matrix.event.encrypted.RawEncryptedContent;
import io.github.ma1uta.matrix.event.message.Audio;
import io.github.ma1uta.matrix.event.message.Emote;
import io.github.ma1uta.matrix.event.message.File;
import io.github.ma1uta.matrix.event.message.FormattedBody;
import io.github.ma1uta.matrix.event.message.Image;
import io.github.ma1uta.matrix.event.message.Location;
import io.github.ma1uta.matrix.event.message.Notice;
import io.github.ma1uta.matrix.event.message.RawMessageContent;
import io.github.ma1uta.matrix.event.message.ServerNotice;
import io.github.ma1uta.matrix.event.message.Text;
import io.github.ma1uta.matrix.event.message.Video;
import io.github.ma1uta.matrix.event.nested.Answer;
import io.github.ma1uta.matrix.event.nested.AudioInfo;
import io.github.ma1uta.matrix.event.nested.Candidate;
import io.github.ma1uta.matrix.event.nested.CiphertextInfo;
import io.github.ma1uta.matrix.event.nested.EncryptedFile;
import io.github.ma1uta.matrix.event.nested.FileInfo;
import io.github.ma1uta.matrix.event.nested.ImageInfo;
import io.github.ma1uta.matrix.event.nested.Invite;
import io.github.ma1uta.matrix.event.nested.JWK;
import io.github.ma1uta.matrix.event.nested.LocationInfo;
import io.github.ma1uta.matrix.event.nested.NotificationPowerLevel;
import io.github.ma1uta.matrix.event.nested.Offer;
import io.github.ma1uta.matrix.event.nested.PreviousRoom;
import io.github.ma1uta.matrix.event.nested.PublicKeys;
import io.github.ma1uta.matrix.event.nested.PushCondition;
import io.github.ma1uta.matrix.event.nested.PushRule;
import io.github.ma1uta.matrix.event.nested.ReceiptInfo;
import io.github.ma1uta.matrix.event.nested.ReceiptTs;
import io.github.ma1uta.matrix.event.nested.Relates;
import io.github.ma1uta.matrix.event.nested.Reply;
import io.github.ma1uta.matrix.event.nested.RequestedKeyInfo;
import io.github.ma1uta.matrix.event.nested.Ruleset;
import io.github.ma1uta.matrix.event.nested.TagInfo;
import io.github.ma1uta.matrix.event.nested.ThumbnailInfo;
import io.github.ma1uta.matrix.event.nested.VideoInfo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable", "InterfaceIsType"})
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, imports = JsonValue.class)
public interface EventMapper {

    Logger LOGGER = LoggerFactory.getLogger(EventMapper.class);

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    Util UTIL = new Util();

    Jsonb JSONB = JsonbBuilder.create();

    default Event deserialize(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        if (jsonObject.get("type") == null || jsonObject.isNull("type")) {
            return UTIL.parse(jsonObject);
        }

        String type = jsonObject.getString("type");

        try {
            switch (type) {
                case Direct.TYPE:
                    return direct(jsonObject);
                case Dummy.TYPE:
                    return dummy(jsonObject);
                case ForwardedRoomKey.TYPE:
                    return forwardedRoomKey(jsonObject);
                case FullyRead.TYPE:
                    return fullyRead(jsonObject);
                case IgnoredUserList.TYPE:
                    return ignoredUserList(jsonObject);
                case KeyVerificationAccept.TYPE:
                    return keyVerificationAccept(jsonObject);
                case KeyVerificationCancel.TYPE:
                    return keyVerificationCancel(jsonObject);
                case KeyVerificationKey.TYPE:
                    return keyVerificationKey(jsonObject);
                case KeyVerificationMac.TYPE:
                    return keyVerificationMac(jsonObject);
                case KeyVerificationRequest.TYPE:
                    return keyVerificationRequest(jsonObject);
                case KeyVerificationStart.TYPE:
                    return keyVerificationStart(jsonObject);
                case Presence.TYPE:
                    return presence(jsonObject);
                case PushRules.TYPE:
                    return pushRules(jsonObject);
                case Receipt.TYPE:
                    return receipt(jsonObject);
                case RoomKey.TYPE:
                    return roomKey(jsonObject);
                case RoomKeyRequest.TYPE:
                    return roomKeyRequest(jsonObject);
                case Tag.TYPE:
                    return tag(jsonObject);
                case Typing.TYPE:
                    return typing(jsonObject);

                case CallAnswer.TYPE:
                    return callAnswer(jsonObject);
                case CallCandidates.TYPE:
                    return callCandidates(jsonObject);
                case CallHangup.TYPE:
                    return callHangup(jsonObject);
                case CallInvite.TYPE:
                    return callInvite(jsonObject);
                case RoomEncrypted.TYPE:
                    return roomEncrypted(jsonObject);
                case RoomMessage.TYPE:
                    return roomMessage(jsonObject);
                case RoomMessageFeedback.TYPE:
                    return roomMessageFeedback(jsonObject);
                case RoomRedaction.TYPE:
                    return roomRedaction(jsonObject);
                case Sticker.TYPE:
                    return sticker(jsonObject);

                case RoomAliases.TYPE:
                    return roomAliases(jsonObject);
                case RoomAvatar.TYPE:
                    return roomAvatar(jsonObject);
                case RoomCanonicalAlias.TYPE:
                    return roomCanonicalAlias(jsonObject);
                case RoomCreate.TYPE:
                    return roomCreate(jsonObject);
                case RoomEncryption.TYPE:
                    return roomEncryption(jsonObject);
                case RoomGuestAccess.TYPE:
                    return roomGuestAccess(jsonObject);
                case RoomHistoryVisibility.TYPE:
                    return roomHistoryVisibility(jsonObject);
                case RoomJoinRules.TYPE:
                    return roomJoinRules(jsonObject);
                case RoomMember.TYPE:
                    return roomMember(jsonObject);
                case RoomName.TYPE:
                    return roomName(jsonObject);
                case RoomPinned.TYPE:
                    return roomPinned(jsonObject);
                case RoomPowerLevels.TYPE:
                    return roomPowerLevels(jsonObject);
                case RoomServerAcl.TYPE:
                    return roomServerAcl(jsonObject);
                case RoomThirdPartyInvite.TYPE:
                    return roomThirdPartyInvite(jsonObject);
                case RoomTombstone.TYPE:
                    return roomTombstone(jsonObject);
                case RoomTopic.TYPE:
                    return roomTopic(jsonObject);
                case PolicyRuleUser.TYPE:
                    return policyRuleUser(jsonObject);
                case PolicyRuleRoom.TYPE:
                    return policyRuleRoom(jsonObject);
                case PolicyRuleServer.TYPE:
                    return policyRuleServer(jsonObject);

                default:
                    return UTIL.parse(jsonObject);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to parse event", e);
            return UTIL.parse(jsonObject);
        }
    }

    @SuppressWarnings("unchecked")
    default <T extends EventContent> T deserializeEventContent(JsonObject jsonObject, String type) {
        switch (type) {
            case Direct.TYPE:
                return (T) directContent(jsonObject);
            case Dummy.TYPE:
                return (T) new DummyContent();
            case ForwardedRoomKey.TYPE:
                return (T) forwardedRoomKeyContent(jsonObject);
            case FullyRead.TYPE:
                return (T) fullyReadContent(jsonObject);
            case IgnoredUserList.TYPE:
                return (T) ignoredUserListContent(jsonObject);
            case KeyVerificationAccept.TYPE:
                return (T) keyVerificationAcceptContent(jsonObject);
            case KeyVerificationCancel.TYPE:
                return (T) keyVerificationCancelContent(jsonObject);
            case KeyVerificationKey.TYPE:
                return (T) keyVerificationKeyContent(jsonObject);
            case KeyVerificationMac.TYPE:
                return (T) keyVerificationMacContent(jsonObject);
            case KeyVerificationRequest.TYPE:
                return (T) keyVerificationRequestContent(jsonObject);
            case KeyVerificationStart.TYPE:
                return (T) keyVerificationStartContent(jsonObject);
            case Presence.TYPE:
                return (T) presenceContent(jsonObject);
            case PushRules.TYPE:
                return (T) pushRulesContent(jsonObject);
            case Receipt.TYPE:
                return (T) receiptContent(jsonObject);
            case RoomKey.TYPE:
                return (T) roomKeyContent(jsonObject);
            case RoomKeyRequest.TYPE:
                return (T) roomKeyRequestContent(jsonObject);
            case Tag.TYPE:
                return (T) tagContent(jsonObject);
            case Typing.TYPE:
                return (T) typingContent(jsonObject);

            case CallAnswer.TYPE:
                return (T) callAnswerContent(jsonObject);
            case CallCandidates.TYPE:
                return (T) callCandidatesContent(jsonObject);
            case CallHangup.TYPE:
                return (T) callHangupContent(jsonObject);
            case CallInvite.TYPE:
                return (T) callInviteContent(jsonObject);
            case RoomEncrypted.TYPE:
                return (T) roomEncryptedContent(jsonObject);
            case RoomMessage.TYPE:
                return (T) roomMessageContent(jsonObject);
            case RoomMessageFeedback.TYPE:
                return (T) roomMessageFeedbackContent(jsonObject);
            case RoomRedaction.TYPE:
                return (T) roomRedactionContent(jsonObject);
            case Sticker.TYPE:
                return (T) stickerContent(jsonObject);

            case RoomAliases.TYPE:
                return (T) roomAliasesContent(jsonObject);
            case RoomAvatar.TYPE:
                return (T) roomAvatarContent(jsonObject);
            case RoomCanonicalAlias.TYPE:
                return (T) roomCanonicalAliasContent(jsonObject);
            case RoomCreate.TYPE:
                return (T) roomCreateContent(jsonObject);
            case RoomEncryption.TYPE:
                return (T) roomEncryptionContent(jsonObject);
            case RoomGuestAccess.TYPE:
                return (T) roomGuestAccessContent(jsonObject);
            case RoomHistoryVisibility.TYPE:
                return (T) roomHistoryVisibilityContent(jsonObject);
            case RoomJoinRules.TYPE:
                return (T) roomJoinRulesContent(jsonObject);
            case RoomMember.TYPE:
                return (T) roomMemberContent(jsonObject);
            case RoomName.TYPE:
                return (T) roomNameContent(jsonObject);
            case RoomPinned.TYPE:
                return (T) roomPinnedContent(jsonObject);
            case RoomPowerLevels.TYPE:
                return (T) roomPowerLevelsContent(jsonObject);
            case RoomServerAcl.TYPE:
                return (T) roomServerAclContent(jsonObject);
            case RoomThirdPartyInvite.TYPE:
                return (T) roomThirdPartyInviteContent(jsonObject);
            case RoomTombstone.TYPE:
                return (T) tombstoneContent(jsonObject);
            case RoomTopic.TYPE:
                return (T) roomTopicContent(jsonObject);

            default:
                return (T) new RawEventContent(toRawMap(jsonObject));
        }
    }

    class Util {
        public Event parse(JsonObject jsonObject) {
            Map<String, Object> props = new HashMap<>();
            for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
                JsonValue value = entry.getValue();
                Object object = value == null ? null : parse(value);
                props.put(entry.getKey(), object);
            }
            return new RawEvent(props, (String) props.get("type"));
        }

        public Object parse(JsonValue jsonValue) {
            if (jsonValue == null) {
                return null;
            }
            switch (jsonValue.getValueType()) {
                case NULL:
                    return null;
                case STRING:
                    return ((JsonString) jsonValue).getString();
                case NUMBER:
                    return ((JsonNumber) jsonValue).longValue();
                case ARRAY:
                    return jsonValue.asJsonArray().stream().map(this::parse).collect(Collectors.toList());
                case TRUE:
                    return Boolean.TRUE;
                case FALSE:
                    return Boolean.FALSE;
                case OBJECT:
                    return parse(jsonValue.asJsonObject());
                default:
                    return jsonValue.toString();
            }
        }
    }


    // Common methods.

    Object EMPTY = new Object();

    default boolean isNull(JsonValue jsonValue) {
        return jsonValue == null || jsonValue.getValueType() == JsonValue.ValueType.NULL;
    }

    default boolean isNull(JsonObject jsonObject, String property) {
        return isNull(jsonObject)
            || jsonObject.get(property) == null
            || jsonObject.isNull(property);
    }

    default String toString(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return jsonObject.getString(property);
    }

    default String toString(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }

        return ((JsonString) jsonValue).getString();
    }

    default Long toLong(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return toLong(jsonObject.getJsonNumber(property));
    }

    default Long toLong(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }

        return ((JsonNumber) jsonValue).longValue();
    }

    default Byte toByte(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return toByte(jsonObject.getJsonNumber(property));
    }

    default Byte toByte(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }

        return (byte) ((JsonNumber) jsonValue).intValue();
    }

    default Boolean toBoolean(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return jsonObject.getBoolean(property) ? Boolean.TRUE : Boolean.FALSE;
    }

    default JsonObject toObject(JsonValue jsonValue) {
        if (isNull(jsonValue)) {
            return null;
        }
        return jsonValue.asJsonObject();
    }

    default List<String> toStringArray(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return toStringArray(jsonObject.getJsonArray(property));
    }

    default List<String> toStringArray(JsonArray jsonArray) {
        if (isNull(jsonArray)) {
            return null;
        }

        return jsonArray.stream().filter(Objects::nonNull).map(this::toString).collect(Collectors.toList());
    }

    default Map<String, String> toStringMap(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        Map<String, String> map = new HashMap<>();

        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), toString(entry.getValue()));
        }

        return map;
    }

    default Map<String, String> toStringMap(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        return toStringMap(jsonObject.getJsonObject(property));
    }

    default Map<String, Byte> toStringByteMap(JsonObject jsonObject, String property) {
        if (isNull(jsonObject, property)) {
            return null;
        }

        Map<String, Byte> map = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject(property).entrySet()) {
            map.put(entry.getKey(), toByte(entry.getValue()));
        }
        return map;
    }

    default Map<String, Object> toRawMap(JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            JsonValue value = entry.getValue();
            map.put(entry.getKey(), value == null ? null : UTIL.parse(value));
        }
        return map;
    }


    // Events.

    @Mapping(expression = "java(directContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Direct direct(JsonObject jsonObject);

    default Dummy dummy(JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        Dummy dummy = new Dummy();
        dummy.setContent(new DummyContent());
        return dummy;
    }

    @Mapping(expression = "java(forwardedRoomKeyContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    ForwardedRoomKey forwardedRoomKey(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(fullyReadContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    FullyRead fullyRead(JsonObject jsonObject);

    @Mapping(expression = "java(ignoredUserListContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    IgnoredUserList ignoredUserList(JsonObject jsonObject);

    @Mapping(expression = "java(keyVerificationAcceptContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    KeyVerificationAccept keyVerificationAccept(JsonObject jsonObject);

    @Mapping(expression = "java(keyVerificationCancelContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    KeyVerificationCancel keyVerificationCancel(JsonObject jsonObject);

    @Mapping(expression = "java(keyVerificationKeyContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    KeyVerificationKey keyVerificationKey(JsonObject jsonObject);

    @Mapping(expression = "java(keyVerificationMacContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    KeyVerificationMac keyVerificationMac(JsonObject jsonObject);

    @Mapping(expression = "java(keyVerificationRequestContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    KeyVerificationRequest keyVerificationRequest(JsonObject jsonObject);

    @Mapping(expression = "java(keyVerificationStartContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    KeyVerificationStart keyVerificationStart(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"sender\"))", target = "sender")
    @Mapping(expression = "java(presenceContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Presence presence(JsonObject jsonObject);

    @Mapping(expression = "java(pushRulesContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    PushRules pushRules(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(receiptContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Receipt receipt(JsonObject jsonObject);

    @Mapping(expression = "java(roomKeyContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomKey roomKey(JsonObject jsonObject);

    @Mapping(expression = "java(roomKeyRequestContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomKeyRequest roomKeyRequest(JsonObject jsonObject);

    @Mapping(expression = "java(tagContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Tag tag(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(typingContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Typing typing(JsonObject jsonObject);


    // Room events.

    @Mapping(expression = "java(toString(jsonObject, \"event_id\"))", target = "eventId")
    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject, \"sender\"))", target = "sender")
    @Mapping(expression = "java(toLong(jsonObject, \"origin_server_ts\"))", target = "originServerTs")
    @Mapping(expression = "java(unsigned(jsonObject))", target = "unsigned")
    void roomEvent(JsonObject jsonObject, @MappingTarget RoomEvent roomEvent);

    default Unsigned unsigned(JsonObject jsonObject) {
        if (isNull(jsonObject, "unsigned")) {
            return null;
        }

        return unsigned(jsonObject.getJsonObject("unsigned"), jsonObject.getString("type"));
    }

    default Unsigned unsigned(JsonObject jsonObject, String type) {
        Unsigned<EventContent> unsigned = new Unsigned<>();

        unsigned.setAge(toLong(jsonObject, "age"));
        unsigned.setRedactedBecause(deserialize(jsonObject.getJsonObject("redacted_because")));
        unsigned.setTransactionId(toString(jsonObject, "transaction_id"));
        unsigned.setPrevContent(deserializeEventContent(jsonObject.getJsonObject("prev_content"), type));

        if (!isNull(jsonObject, "invite_room_state")) {
            unsigned.setInviteRoomState(
                jsonObject.getJsonArray("invite_room_state").stream()
                    .map(JsonValue::asJsonObject)
                    .map(this::deserialize)
                    .collect(Collectors.toList())
            );
        }

        return unsigned;

    }

    @InheritConfiguration
    @Mapping(expression = "java(callAnswerContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    CallAnswer callAnswer(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(callCandidatesContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    CallCandidates callCandidates(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(callHangupContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    CallHangup callHangup(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(callInviteContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    CallInvite callInvite(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(roomEncryptedContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomEncrypted roomEncrypted(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(roomMessageContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomMessage roomMessage(JsonObject jsonObject);

    @Mapping(expression = "java(roomMessageFeedbackContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomMessageFeedback roomMessageFeedback(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(roomRedactionContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomRedaction roomRedaction(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(stickerContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Sticker sticker(JsonObject jsonObject);


    // State events.

    @InheritConfiguration
    @Mapping(expression = "java(toString(jsonObject, \"state_key\"))", target = "stateKey")
    void stateEvent(JsonObject jsonObject, @MappingTarget StateEvent stateEvent);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomAliasesContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomAliasesContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomAliases roomAliases(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomAvatarContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomAvatarContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomAvatar roomAvatar(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomCanonicalAliasContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomCanonicalAliasContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomCanonicalAlias roomCanonicalAlias(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomCreateContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomCreateContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomCreate roomCreate(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomEncryptionContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomEncryptionContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomEncryption roomEncryption(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomGuestAccessContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomGuestAccessContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomGuestAccess roomGuestAccess(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomHistoryVisibilityContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomHistoryVisibilityContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomHistoryVisibility roomHistoryVisibility(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomJoinRulesContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomJoinRulesContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomJoinRules roomJoinRules(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomMemberContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomMemberContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomMember roomMember(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomNameContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomNameContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomName roomName(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomPinnedContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomPinnedContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomPinned roomPinned(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomPowerLevelsContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomPowerLevelsContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomPowerLevels roomPowerLevels(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomServerAclContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomServerAclContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomServerAcl roomServerAcl(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomThirdPartyInviteContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomThirdPartyInviteContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomThirdPartyInvite roomThirdPartyInvite(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(tombstoneContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(tombstoneContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomTombstone roomTombstone(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(roomTopicContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    @Mapping(expression = "java(roomTopicContent(jsonObject.getJsonObject(\"prev_content\")))", target = "prevContent")
    RoomTopic roomTopic(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(policyRuleUserContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    PolicyRuleUser policyRuleUser(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(policyRuleRoomContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    PolicyRuleRoom policyRuleRoom(JsonObject jsonObject);

    @InheritConfiguration(name = "stateEvent")
    @Mapping(expression = "java(policyRuleServerContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    PolicyRuleServer policyRuleServer(JsonObject jsonObject);

    // Event contents.

    default DirectContent directContent(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        DirectContent directContent = new DirectContent();

        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            if (isNull(entry.getValue())) {
                continue;
            }

            directContent.put(entry.getKey(), toStringArray(entry.getValue().asJsonArray()));
        }

        return directContent;
    }

    @Mapping(expression = "java(toString(jsonObject, \"algorithm\"))", target = "algorithm")
    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject, \"sender_key\"))", target = "senderKey")
    @Mapping(expression = "java(toString(jsonObject, \"session_id\"))", target = "sessionId")
    @Mapping(expression = "java(toString(jsonObject, \"session_key\"))", target = "sessionKey")
    @Mapping(expression = "java(toString(jsonObject, \"sender_claimed_ed25519_key\"))", target = "senderClaimedEd25519Key")
    @Mapping(expression = "java(toStringArray(jsonObject, \"forwarding_curve25519_key_chain\"))", target = "forwardingCurve25519KeyChain")
    ForwardedRoomKeyContent forwardedRoomKeyContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"event_id\"))", target = "eventId")
    FullyReadContent fullyReadContent(JsonObject jsonObject);

    default IgnoredUserListContent ignoredUserListContent(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        IgnoredUserListContent ignoredUserListContent = new IgnoredUserListContent();

        if (!isNull(jsonObject, "ignored_users")) {
            Map<String, Object> ignoredUsers = new HashMap<>();
            for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("ignored_users").entrySet()) {
                ignoredUsers.put(entry.getKey(), EMPTY);
            }
            ignoredUserListContent.setIgnoredUsers(ignoredUsers);
        }

        return ignoredUserListContent;
    }

    @Mapping(expression = "java(toString(jsonObject, \"transaction_id\"))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject, \"method\"))", target = "method")
    @Mapping(expression = "java(toString(jsonObject, \"key_agreement_protocol\"))", target = "keyAgreementProtocol")
    @Mapping(expression = "java(toString(jsonObject, \"hash\"))", target = "hash")
    @Mapping(expression = "java(toString(jsonObject, \"message_authentication_code\"))", target = "messageAuthenticationCode")
    @Mapping(expression = "java(toString(jsonObject, \"short_authentication_string\"))", target = "shortAuthenticationString")
    @Mapping(expression = "java(toString(jsonObject, \"commitment\"))", target = "commitment")
    KeyVerificationAcceptContent keyVerificationAcceptContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"transaction_id\"))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject, \"reason\"))", target = "reason")
    @Mapping(expression = "java(toString(jsonObject, \"code\"))", target = "code")
    KeyVerificationCancelContent keyVerificationCancelContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"transaction_id\"))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject, \"key\"))", target = "key")
    KeyVerificationKeyContent keyVerificationKeyContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"transaction_id\"))", target = "transactionId")
    @Mapping(expression = "java(toStringMap(jsonObject, \"mac\"))", target = "mac")
    @Mapping(expression = "java(toString(jsonObject, \"keys\"))", target = "keys")
    KeyVerificationMacContent keyVerificationMacContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"from_device\"))", target = "fromDevice")
    @Mapping(expression = "java(toString(jsonObject, \"transaction_id\"))", target = "transactionId")
    @Mapping(expression = "java(toStringArray(jsonObject, \"methods\"))", target = "methods")
    @Mapping(expression = "java(toLong(jsonObject, \"timestamp\"))", target = "timestamp")
    KeyVerificationRequestContent keyVerificationRequestContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"from_device\"))", target = "fromDevice")
    @Mapping(expression = "java(toString(jsonObject, \"transaction_id\"))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject, \"method\"))", target = "method")
    @Mapping(expression = "java(toString(jsonObject, \"next_method\"))", target = "nextMethod")
    @Mapping(expression = "java(toStringArray(jsonObject, \"key_agreement_protocol\"))", target = "keyAgreementProtocol")
    @Mapping(expression = "java(toStringArray(jsonObject, \"hashes\"))", target = "hashes")
    @Mapping(expression = "java(toStringArray(jsonObject, \"message_authentication_codes\"))", target = "messageAuthenticationCodes")
    @Mapping(expression = "java(toStringArray(jsonObject, \"short_authentication_string\"))", target = "shortAuthenticationString")
    KeyVerificationStartContent keyVerificationStartContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"avatar_url\"))", target = "avatarUrl")
    @Mapping(expression = "java(toString(jsonObject, \"displayname\"))", target = "displayName")
    @Mapping(expression = "java(toLong(jsonObject, \"last_active_ago\"))", target = "lastActiveAgo")
    @Mapping(expression = "java(toString(jsonObject, \"presence\"))", target = "presence")
    @Mapping(expression = "java(toBoolean(jsonObject, \"currently_active\"))", target = "currentlyActive")
    @Mapping(expression = "java(toString(jsonObject, \"status_msg\"))", target = "statusMsg")
    PresenceContent presenceContent(JsonObject jsonObject);

    @Mapping(expression = "java(ruleset(jsonObject.getJsonObject(\"global\")))", target = "global")
    PushRulesContent pushRulesContent(JsonObject jsonObject);

    @Mapping(expression = "java(pushRuleArray(jsonObject.getJsonArray(\"content\")))", target = "content")
    @Mapping(expression = "java(pushRuleArray(jsonObject.getJsonArray(\"override\")))", target = "override")
    @Mapping(expression = "java(pushRuleArray(jsonObject.getJsonArray(\"room\")))", target = "room")
    @Mapping(expression = "java(pushRuleArray(jsonObject.getJsonArray(\"sender\")))", target = "sender")
    @Mapping(expression = "java(pushRuleArray(jsonObject.getJsonArray(\"underride\")))", target = "underride")
    Ruleset ruleset(JsonObject jsonObject);

    List<PushRule> pushRuleArray(JsonArray jsonArray);

    @Mapping(expression = "java(actions(jsonObject))", target = "actions")
    @Mapping(expression = "java(toBoolean(jsonObject, \"default\"))", target = "defaultRule")
    @Mapping(expression = "java(toBoolean(jsonObject, \"enabled\"))", target = "enabled")
    @Mapping(expression = "java(toString(jsonObject, \"rule_id\"))", target = "ruleId")
    @Mapping(expression = "java(conditions(jsonObject.getJsonArray(\"conditions\")))", target = "conditions")
    @Mapping(expression = "java(toString(jsonObject, \"pattern\"))", target = "pattern")
    PushRule pushRule(JsonObject jsonObject);

    default List<Object> actions(JsonObject jsonObject) {
        if (isNull(jsonObject, "actions")) {
            return null;
        }

        return jsonObject.getJsonArray("actions").stream().map(action -> {
            switch (action.getValueType()) {
                case STRING:
                    return toString(action);
                case OBJECT:
                    return toStringMap(action.asJsonObject());
                default:
                    return action.toString();
            }
        }).collect(Collectors.toList());
    }

    List<PushCondition> conditions(JsonArray jsonArray);

    @Mapping(expression = "java(toString(jsonObject, \"kind\"))", target = "kind")
    @Mapping(expression = "java(toString(jsonObject, \"key\"))", target = "key")
    @Mapping(expression = "java(toString(jsonObject, \"pattern\"))", target = "pattern")
    @Mapping(expression = "java(toString(jsonObject, \"is\"))", target = "is")
    PushCondition pushCondition(JsonObject jsonObject);

    default ReceiptContent receiptContent(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        ReceiptContent receiptContent = new ReceiptContent();

        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            receiptContent.put(entry.getKey(), receiptInfo(entry.getValue().asJsonObject()));
        }

        return receiptContent;
    }

    default ReceiptInfo receiptInfo(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        ReceiptInfo receiptInfo = new ReceiptInfo();

        if (!isNull(jsonObject, "m.read")) {
            Map<String, ReceiptTs> read = new HashMap<>();
            for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("m.read").entrySet()) {
                JsonValue entryValue = entry.getValue();
                ReceiptTs receiptTs;
                if (entryValue.getValueType() == JsonValue.ValueType.STRING) {
                    // workaround for https://github.com/matrix-org/synapse/issues/4898
                    String serializedObject = entryValue.toString();
                    String preparedString = serializedObject.substring(1, serializedObject.length() - 1).replaceAll("\\\\", "");
                    receiptTs = JSONB.fromJson(preparedString, ReceiptTs.class);
                } else {
                    receiptTs = receiptTs(entryValue.asJsonObject());
                }
                read.put(entry.getKey(), receiptTs);
            }
            receiptInfo.setRead(read);
        }

        return receiptInfo;
    }

    @Mapping(expression = "java(toLong(jsonObject, \"ts\"))", target = "ts")
    ReceiptTs receiptTs(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"algorithm\"))", target = "algorithm")
    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject, \"session_id\"))", target = "sessionId")
    @Mapping(expression = "java(toString(jsonObject, \"session_key\"))", target = "sessionKey")
    RoomKeyContent roomKeyContent(JsonObject jsonObject);

    @Mapping(expression = "java(requestKeyInfo(jsonObject.getJsonObject(\"body\")))", target = "body")
    @Mapping(expression = "java(toString(jsonObject, \"action\"))", target = "action")
    @Mapping(expression = "java(toString(jsonObject, \"requesting_device_id\"))", target = "requestingDeviceId")
    @Mapping(expression = "java(toString(jsonObject, \"request_id\"))", target = "requestId")
    RoomKeyRequestContent roomKeyRequestContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"algorithm\"))", target = "algorithm")
    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject, \"sender_key\"))", target = "senderKey")
    @Mapping(expression = "java(toString(jsonObject, \"session_id\"))", target = "sessionId")
    RequestedKeyInfo requestKeyInfo(JsonObject jsonObject);

    default TagContent tagContent(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        TagContent tagContent = new TagContent();

        if (!isNull(jsonObject, "tags")) {
            Map<String, TagInfo> tags = new HashMap<>();
            for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("tags").entrySet()) {
                tags.put(entry.getKey(), tagInfo(entry.getValue().asJsonObject()));
            }
            tagContent.setTags(tags);
        }

        return tagContent;
    }

    @Mapping(expression = "java(toLong(jsonObject, \"order\"))", target = "order")
    TagInfo tagInfo(JsonObject jsonObject);

    @Mapping(expression = "java(toStringArray(jsonObject, \"user_ids\"))", target = "userIds")
    TypingContent typingContent(JsonObject jsonObject);


    // Room event contents.

    @Mapping(expression = "java(toString(jsonObject, \"call_id\"))", target = "callId")
    @Mapping(expression = "java(answer(jsonObject.getJsonObject(\"answer\")))", target = "answer")
    @Mapping(expression = "java(toLong(jsonObject, \"version\"))", target = "version")
    CallAnswerContent callAnswerContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"type\"))", target = "type")
    @Mapping(expression = "java(toString(jsonObject, \"sdp\"))", target = "sdp")
    Answer answer(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"call_id\"))", target = "callId")
    @Mapping(expression = "java(candidates(jsonObject.getJsonArray(\"candidates\")))", target = "candidates")
    @Mapping(expression = "java(toLong(jsonObject, \"version\"))", target = "version")
    CallCandidatesContent callCandidatesContent(JsonObject jsonObject);

    List<Candidate> candidates(JsonArray jsonArray);

    @Mapping(expression = "java(toString(jsonObject, \"sdpMid\"))", target = "sdpMid")
    @Mapping(expression = "java(toLong(jsonObject, \"sdpMLineIndex\"))", target = "sdpMLineIndex")
    @Mapping(expression = "java(toString(jsonObject, \"candidate\"))", target = "candidate")
    Candidate candidate(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"call_id\"))", target = "callId")
    @Mapping(expression = "java(toLong(jsonObject, \"version\"))", target = "version")
    @Mapping(expression = "java(toString(jsonObject, \"reason\"))", target = "reason")
    CallHangupContent callHangupContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"call_id\"))", target = "callId")
    @Mapping(expression = "java(offer(jsonObject.getJsonObject(\"offer\")))", target = "offer")
    @Mapping(expression = "java(toLong(jsonObject, \"version\"))", target = "version")
    @Mapping(expression = "java(toLong(jsonObject, \"lifetime\"))", target = "lifetime")
    CallInviteContent callInviteContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"type\"))", target = "type")
    @Mapping(expression = "java(toString(jsonObject, \"sdp\"))", target = "sdp")
    Offer offer(JsonObject jsonObject);

    default RoomEncryptedContent roomEncryptedContent(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        String algorithm = jsonObject.getString("algorithm");

        RoomEncryptedContent roomEncryptedContent;
        switch (algorithm) {
            case OlmEncryptedContent.ALGORITHM:
                roomEncryptedContent = olmEncryptedContent(jsonObject);
                break;
            case MegolmEncryptedContent.ALGORITHM:
                roomEncryptedContent = megolmEncryptedContent(jsonObject);
                break;
            default:
                roomEncryptedContent = rawEncryptedContent(jsonObject, algorithm);
        }
        roomEncryptedContent.setSenderKey(toString(jsonObject, "sender_key"));
        roomEncryptedContent.setDeviceId(toString(jsonObject, "device_id"));
        roomEncryptedContent.setSessionId(toString(jsonObject, "session_id"));

        return roomEncryptedContent;
    }

    default OlmEncryptedContent olmEncryptedContent(JsonObject jsonObject) {
        OlmEncryptedContent olmEncryptedContent = new OlmEncryptedContent();

        if (!isNull(jsonObject, "ciphertext")) {
            Map<String, CiphertextInfo> ciphertext = new HashMap<>();
            for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("ciphertext").entrySet()) {
                ciphertext.put(entry.getKey(), ciphertextInfo(entry.getValue().asJsonObject()));
            }
            olmEncryptedContent.setCiphertext(ciphertext);
        }

        return olmEncryptedContent;
    }

    @Mapping(expression = "java(toString(jsonObject, \"ciphertext\"))", target = "ciphertext")
    MegolmEncryptedContent megolmEncryptedContent(JsonObject jsonObject);

    default RawEncryptedContent rawEncryptedContent(JsonObject jsonObject, String algorithhm) {
        return new RawEncryptedContent(toRawMap(jsonObject), algorithhm);
    }

    @Mapping(expression = "java(toString(jsonObject, \"body\"))", target = "body")
    @Mapping(expression = "java(toLong(jsonObject, \"type\"))", target = "type")
    CiphertextInfo ciphertextInfo(JsonObject jsonObject);

    default RoomMessageContent roomMessageContent(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        RoomMessageContent roomMessageContent;

        if (isNull(jsonObject, "msgtype")) {
            return rawMessageContent(jsonObject, null);
        }

        String msgtype = jsonObject.getString("msgtype");
        switch (msgtype) {
            case Audio.MSGTYPE:
                roomMessageContent = audio(jsonObject);
                break;
            case Emote.MSGTYPE:
                roomMessageContent = emote(jsonObject);
                break;
            case File.MSGTYPE:
                roomMessageContent = file(jsonObject);
                break;
            case Image.MSGTYPE:
                roomMessageContent = image(jsonObject);
                break;
            case Location.MSGTYPE:
                roomMessageContent = location(jsonObject);
                break;
            case Notice.MSGTYPE:
                roomMessageContent = notice(jsonObject);
                break;
            case ServerNotice.MSGTYPE:
                roomMessageContent = serverNotice(jsonObject);
                break;
            case Text.MSGTYPE:
                roomMessageContent = text(jsonObject);
                break;
            case Video.MSGTYPE:
                roomMessageContent = video(jsonObject);
                break;
            default:
                roomMessageContent = rawMessageContent(jsonObject, msgtype);
        }

        roomMessageContent.setBody(toString(jsonObject, "body"));
        roomMessageContent.setRelatesTo(relates(jsonObject));

        return roomMessageContent;
    }

    default RawMessageContent rawMessageContent(JsonObject jsonObject, String msgtype) {
        return new RawMessageContent(toRawMap(jsonObject), msgtype);
    }

    @Mapping(expression = "java(toString(jsonObject, \"format\"))", target = "format")
    @Mapping(expression = "java(toString(jsonObject, \"formatted_body\"))", target = "formattedBody")
    void formattedBody(JsonObject jsonObject, @MappingTarget FormattedBody formattedBody);

    @Mapping(expression = "java(audioInfo(jsonObject.getJsonObject(\"duration\")))", target = "info")
    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    @Mapping(expression = "java(encryptedFile(jsonObject.getJsonObject(\"file\")))", target = "file")
    Audio audio(JsonObject jsonObject);

    @InheritConfiguration
    Emote emote(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"filename\"))", target = "filename")
    @Mapping(expression = "java(fileInfo(jsonObject.getJsonObject(\"info\")))", target = "info")
    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    @Mapping(expression = "java(encryptedFile(jsonObject.getJsonObject(\"file\")))", target = "file")
    File file(JsonObject jsonObject);

    @Mapping(expression = "java(imageInfo(jsonObject.getJsonObject(\"info\")))", target = "info")
    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    @Mapping(expression = "java(encryptedFile(jsonObject.getJsonObject(\"file\")))", target = "file")
    Image image(JsonObject jsonObject);

    @Mapping(expression = "java(locationInfo(jsonObject.getJsonObject(\"info\")))", target = "info")
    @Mapping(expression = "java(toString(jsonObject, \"geo_uri\"))", target = "geoUri")
    Location location(JsonObject jsonObject);

    @InheritConfiguration
    Notice notice(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"server_notice_type\"))", target = "serverNoticeType")
    @Mapping(expression = "java(toString(jsonObject, \"admin_contact\"))", target = "adminContact")
    @Mapping(expression = "java(toString(jsonObject, \"limit_type\"))", target = "limitType")
    ServerNotice serverNotice(JsonObject jsonObject);

    @InheritConfiguration
    Text text(JsonObject jsonObject);

    @Mapping(expression = "java(videoInfo(jsonObject.getJsonObject(\"info\")))", target = "info")
    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    @Mapping(expression = "java(encryptedFile(jsonObject.getJsonObject(\"file\")))", target = "file")
    Video video(JsonObject jsonObject);

    @Mapping(expression = "java(toLong(jsonObject, \"duration\"))", target = "duration")
    @Mapping(expression = "java(toString(jsonObject, \"mimetype\"))", target = "mimetype")
    @Mapping(expression = "java(toLong(jsonObject, \"size\"))", target = "size")
    AudioInfo audioInfo(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"thumbnail_url\"))", target = "thumbnailUrl")
    @Mapping(expression = "java(encryptedFile(jsonObject.getJsonObject(\"thumbnail_file\")))", target = "thumbnailFile")
    @Mapping(expression = "java(thumbnailInfo(jsonObject.getJsonObject(\"thumbnail_info\")))", target = "thumbnailInfo")
    LocationInfo locationInfo(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(toLong(jsonObject, \"h\"))", target = "height")
    @Mapping(expression = "java(toLong(jsonObject, \"w\"))", target = "width")
    @Mapping(expression = "java(toLong(jsonObject, \"duration\"))", target = "duration")
    VideoInfo videoInfo(JsonObject jsonObject);

    @Mapping(expression = "java(reply(jsonObject.getJsonObject(\"m.in_reply_to\")))", target = "inReplyTo")
    Relates relates(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"event_id\"))", target = "eventId")
    Reply reply(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"target_event_id\"))", target = "targetEventId")
    @Mapping(expression = "java(toString(jsonObject, \"type\"))", target = "type")
    RoomMessageFeedbackContent roomMessageFeedbackContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"reason\"))", target = "reason")
    RoomRedactionContent roomRedactionContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"body\"))", target = "body")
    @Mapping(expression = "java(imageInfo(jsonObject.getJsonObject(\"info\")))", target = "info")
    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    StickerContent stickerContent(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(toLong(jsonObject, \"h\"))", target = "height")
    @Mapping(expression = "java(toLong(jsonObject, \"w\"))", target = "width")
    ImageInfo imageInfo(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"mimetype\"))", target = "mimetype")
    @Mapping(expression = "java(toLong(jsonObject, \"size\"))", target = "size")
    @Mapping(expression = "java(toString(jsonObject, \"thumbnail_url\"))", target = "thumbnailUrl")
    @Mapping(expression = "java(encryptedFile(jsonObject.getJsonObject(\"thumbnail_file\")))", target = "thumbnailFile")
    @Mapping(expression = "java(thumbnailInfo(jsonObject.getJsonObject(\"thumbnail_info\")))", target = "thumbnailInfo")
    FileInfo fileInfo(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    @Mapping(expression = "java(jwk(jsonObject.getJsonObject(\"key\")))", target = "key")
    @Mapping(expression = "java(toString(jsonObject, \"iv\"))", target = "iv")
    @Mapping(expression = "java(toStringMap(jsonObject, \"hashes\"))", target = "hashes")
    @Mapping(expression = "java(toString(jsonObject, \"v\"))", target = "version")
    EncryptedFile encryptedFile(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"key\"))", target = "key")
    @Mapping(expression = "java(toStringArray(jsonObject, \"key_opts\"))", target = "keyOpts")
    @Mapping(expression = "java(toString(jsonObject, \"alg\"))", target = "alg")
    @Mapping(expression = "java(toString(jsonObject, \"k\"))", target = "encodedKey")
    @Mapping(expression = "java(toBoolean(jsonObject, \"ext\"))", target = "ext")
    JWK jwk(JsonObject jsonObject);

    @Mapping(expression = "java(toLong(jsonObject, \"h\"))", target = "height")
    @Mapping(expression = "java(toLong(jsonObject, \"w\"))", target = "width")
    @Mapping(expression = "java(toString(jsonObject, \"mimetype\"))", target = "mimetype")
    @Mapping(expression = "java(toLong(jsonObject, \"size\"))", target = "size")
    ThumbnailInfo thumbnailInfo(JsonObject jsonObject);


    // State content events.

    @Mapping(expression = "java(toStringArray(jsonObject, \"aliases\"))", target = "aliases")
    RoomAliasesContent roomAliasesContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"url\"))", target = "url")
    @Mapping(expression = "java(imageInfo(jsonObject.getJsonObject(\"info\")))", target = "info")
    RoomAvatarContent roomAvatarContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"alias\"))", target = "alias")
    RoomCanonicalAliasContent roomCanonicalAliasContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"creator\"))", target = "creator")
    @Mapping(expression = "java(toBoolean(jsonObject, \"m.federate\"))", target = "federate")
    @Mapping(expression = "java(toString(jsonObject, \"room_version\"))", target = "roomVersion")
    @Mapping(expression = "java(previousRoom(jsonObject.getJsonObject(\"predecessor\")))", target = "predecessor")
    RoomCreateContent roomCreateContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject, \"event_id\"))", target = "eventId")
    PreviousRoom previousRoom(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"algorithm\"))", target = "algorithm")
    @Mapping(expression = "java(toLong(jsonObject, \"rotation_period_ms\"))", target = "rotationPeriodMs")
    @Mapping(expression = "java(toLong(jsonObject, \"rotation_period_msgs\"))", target = "rotationPeriodMsgs")
    RoomEncryptionContent roomEncryptionContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"guest_access\"))", target = "guestAccess")
    RoomGuestAccessContent roomGuestAccessContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"history_visibility\"))", target = "historyVisibility")
    RoomHistoryVisibilityContent roomHistoryVisibilityContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"join_rule\"))", target = "joinRule")
    RoomJoinRulesContent roomJoinRulesContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"avatar_url\"))", target = "avatarUrl")
    @Mapping(expression = "java(toString(jsonObject, \"displayname\"))", target = "displayName")
    @Mapping(expression = "java(toString(jsonObject, \"membership\"))", target = "membership")
    @Mapping(expression = "java(toBoolean(jsonObject, \"is_direct\"))", target = "direct")
    @Mapping(expression = "java(invite(jsonObject.getJsonObject(\"third_party_invite\")))", target = "thirdPartyInvite")
    @Mapping(expression = "java(unsigned(jsonObject.getJsonObject(\"third_party_invite\"), \"m.room.member\"))", target = "unsigned")
    RoomMemberContent roomMemberContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"display_name\"))", target = "displayName")
    @Mapping(expression = "java(signed(jsonObject.getJsonObject(\"signed\")))", target = "signed")
    Invite invite(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"mxid\"))", target = "mxid")
    @Mapping(expression = "java(toString(jsonObject, \"token\"))", target = "token")
    @Mapping(expression = "java(signatures(jsonObject.getJsonObject(\"signatures\")))", target = "signatures")
    Signed signed(JsonObject jsonObject);

    default Map<String, Map<String, String>> signatures(JsonObject jsonObject) {
        if (isNull(jsonObject)) {
            return null;
        }

        Map<String, Map<String, String>> signatures = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            signatures.put(entry.getKey(), toStringMap(entry.getValue().asJsonObject()));
        }

        return signatures;
    }

    @Mapping(expression = "java(toString(jsonObject, \"name\"))", target = "name")
    RoomNameContent roomNameContent(JsonObject jsonObject);

    @Mapping(expression = "java(toStringArray(jsonObject, \"pinned\"))", target = "pinned")
    RoomPinnedContent roomPinnedContent(JsonObject jsonObject);

    @Mapping(expression = "java(toByte(jsonObject, \"ban\"))", target = "ban")
    @Mapping(expression = "java(toStringByteMap(jsonObject, \"events\"))", target = "events")
    @Mapping(expression = "java(toByte(jsonObject, \"events_default\"))", target = "eventsDefault")
    @Mapping(expression = "java(toByte(jsonObject, \"invite\"))", target = "invite")
    @Mapping(expression = "java(toByte(jsonObject, \"kick\"))", target = "kick")
    @Mapping(expression = "java(toByte(jsonObject, \"redact\"))", target = "redact")
    @Mapping(expression = "java(toByte(jsonObject, \"state_default\"))", target = "stateDefault")
    @Mapping(expression = "java(toStringByteMap(jsonObject, \"users\"))", target = "users")
    @Mapping(expression = "java(toByte(jsonObject, \"users_default\"))", target = "usersDefault")
    @Mapping(expression = "java(notificationPowerLevel(jsonObject.getJsonObject(\"notifications\")))", target = "notifications")
    RoomPowerLevelsContent roomPowerLevelsContent(JsonObject jsonObject);

    @Mapping(expression = "java(toByte(jsonObject, \"room\"))", target = "room")
    NotificationPowerLevel notificationPowerLevel(JsonObject jsonObject);

    @Mapping(expression = "java(toBoolean(jsonObject, \"allow_ip_literals\"))", target = "allowIpLiterals")
    @Mapping(expression = "java(toStringArray(jsonObject, \"allow\"))", target = "allow")
    @Mapping(expression = "java(toStringArray(jsonObject, \"deny\"))", target = "deny")
    RoomServerAclContent roomServerAclContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"display_name\"))", target = "displayName")
    @Mapping(expression = "java(toString(jsonObject, \"key_validity_url\"))", target = "keyValidityUrl")
    @Mapping(expression = "java(toString(jsonObject, \"public_key\"))", target = "publicKey")
    @Mapping(expression = "java(publicKeysArray(jsonObject.getJsonArray(\"public_keys\")))", target = "publicKeys")
    RoomThirdPartyInviteContent roomThirdPartyInviteContent(JsonObject jsonObject);

    List<PublicKeys> publicKeysArray(JsonArray jsonArray);

    @Mapping(expression = "java(toString(jsonObject, \"key_validity_url\"))", target = "keyValidityUrl")
    @Mapping(expression = "java(toString(jsonObject, \"public_key\"))", target = "publicKey")
    PublicKeys publicKeys(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"body\"))", target = "body")
    @Mapping(expression = "java(toString(jsonObject, \"replacement_room\"))", target = "replacementRoom")
    TombstoneContent tombstoneContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"topic\"))", target = "topic")
    RoomTopicContent roomTopicContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"entity\"))", target = "entity")
    @Mapping(expression = "java(toString(jsonObject, \"recommendation\"))", target = "recommendation")
    @Mapping(expression = "java(toString(jsonObject, \"reason\"))", target = "reason")
    PolicyRuleUserContent policyRuleUserContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"entity\"))", target = "entity")
    @Mapping(expression = "java(toString(jsonObject, \"recommendation\"))", target = "recommendation")
    @Mapping(expression = "java(toString(jsonObject, \"reason\"))", target = "reason")
    PolicyRuleRoomContent policyRuleRoomContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject, \"entity\"))", target = "entity")
    @Mapping(expression = "java(toString(jsonObject, \"recommendation\"))", target = "recommendation")
    @Mapping(expression = "java(toString(jsonObject, \"reason\"))", target = "reason")
    PolicyRuleServerContent policyRuleServerContent(JsonObject jsonObject);
}
