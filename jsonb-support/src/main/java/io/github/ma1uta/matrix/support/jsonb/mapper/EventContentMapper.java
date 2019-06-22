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

package io.github.ma1uta.matrix.support.jsonb.mapper;

import io.github.ma1uta.matrix.event.content.CallAnswerContent;
import io.github.ma1uta.matrix.event.content.CallCandidatesContent;
import io.github.ma1uta.matrix.event.content.DirectContent;
import io.github.ma1uta.matrix.event.content.ForwardedRoomKeyContent;
import io.github.ma1uta.matrix.event.content.FullyReadContent;
import io.github.ma1uta.matrix.event.content.IgnoredUserListContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationAcceptContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationCancelContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationKeyContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationMacContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationRequestContent;
import io.github.ma1uta.matrix.event.content.KeyVerificationStartContent;
import io.github.ma1uta.matrix.event.content.PresenceContent;
import io.github.ma1uta.matrix.event.content.PushRulesContent;
import io.github.ma1uta.matrix.event.content.ReceiptContent;
import io.github.ma1uta.matrix.event.content.RoomKeyContent;
import io.github.ma1uta.matrix.event.content.RoomKeyRequestContent;
import io.github.ma1uta.matrix.event.content.TagContent;
import io.github.ma1uta.matrix.event.content.TypingContent;
import io.github.ma1uta.matrix.event.nested.Answer;
import io.github.ma1uta.matrix.event.nested.Candidate;
import io.github.ma1uta.matrix.event.nested.PushCondition;
import io.github.ma1uta.matrix.event.nested.PushRule;
import io.github.ma1uta.matrix.event.nested.ReceiptInfo;
import io.github.ma1uta.matrix.event.nested.ReceiptTs;
import io.github.ma1uta.matrix.event.nested.RequestedKeyInfo;
import io.github.ma1uta.matrix.event.nested.Ruleset;
import io.github.ma1uta.matrix.event.nested.TagInfo;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable", "LineLength"})
public interface EventContentMapper extends CommonMapper {

    Object EMPTY = new Object();

    default DirectContent directContent(JsonObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }

        DirectContent directContent = new DirectContent();

        for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
            if (entry.getValue() == null || entry.getValue().getValueType() == JsonValue.ValueType.NULL) {
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
        if (jsonObject == null || jsonObject.getValueType() == JsonValue.ValueType.NULL) {
            return null;
        }

        IgnoredUserListContent ignoredUserListContent = new IgnoredUserListContent();

        Map<String, Object> ignoredUsers = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("ignored_users").entrySet()) {
            ignoredUsers.put(entry.getKey(), EMPTY);
        }
        ignoredUserListContent.setIgnoredUsers(ignoredUsers);

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

    @Mapping(expression = "java(pushRules(jsonObject.getJsonArray(\"content\")))", target = "content")
    @Mapping(expression = "java(pushRules(jsonObject.getJsonArray(\"override\")))", target = "override")
    @Mapping(expression = "java(pushRules(jsonObject.getJsonArray(\"room\")))", target = "room")
    @Mapping(expression = "java(pushRules(jsonObject.getJsonArray(\"sender\")))", target = "sender")
    @Mapping(expression = "java(pushRules(jsonObject.getJsonArray(\"underride\")))", target = "underride")
    Ruleset ruleset(JsonObject jsonObject);

    List<PushRule> pushRules(JsonArray jsonArray);

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
        if (isNull(jsonObject, "read")) {
            return null;
        }

        ReceiptInfo receiptInfo = new ReceiptInfo();

        Map<String, ReceiptTs> read = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("read").entrySet()) {
            read.put(entry.getKey(), receiptTs(entry.getValue().asJsonObject()));
        }
        receiptInfo.setRead(read);

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
        if (isNull(jsonObject, "tags")) {
            return null;
        }

        TagContent tagContent = new TagContent();

        Map<String, TagInfo> tags = new HashMap<>();
        for (Map.Entry<String, JsonValue> entry : jsonObject.getJsonObject("tags").entrySet()) {
            tags.put(entry.getKey(), tagInfo(entry.getValue().asJsonObject()));
        }
        tagContent.setTags(tags);

        return tagContent;
    }

    @Mapping(expression = "java(toLong(jsonObject, \"order\"))", target = "order")
    TagInfo tagInfo(JsonObject jsonObject);

    @Mapping(expression = "java(toStringArray(jsonObject, \"user_ids\"))", target = "userIds")
    TypingContent typingContent(JsonObject jsonObject);

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
}
