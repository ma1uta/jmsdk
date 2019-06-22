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
import io.github.ma1uta.matrix.event.nested.Answer;
import io.github.ma1uta.matrix.event.nested.Candidate;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"algorithm\")))", target = "algorithm")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"room_id\")))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"sender_key\")))", target = "senderKey")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"session_id\")))", target = "sessionId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"session_key\")))", target = "sessionKey")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"sender_claimed_ed25519_key\")))", target = "senderClaimedEd25519Key")
    @Mapping(expression = "java(toStringArray(jsonObject.getJsonArray(\"forwarding_curve25519_key_chain\")))", target = "forwardingCurve25519KeyChain")
    ForwardedRoomKeyContent forwardedRoomKeyContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"event_id\")))", target = "eventId")
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

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"transaction_id\")))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"method\")))", target = "method")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"key_agreement_protocol\")))", target = "keyAgreementProtocol")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"hash\")))", target = "hash")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"message_authentication_code\")))", target = "messageAuthenticationCode")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"short_authentication_string\")))", target = "shortAuthenticationString")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"commitment\")))", target = "commitment")
    KeyVerificationAcceptContent keyVerificationAcceptContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"transaction_id\")))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"reason\")))", target = "reason")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"code\")))", target = "code")
    KeyVerificationCancelContent keyVerificationCancelContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"transaction_id\")))", target = "transactionId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"key\")))", target = "key")
    KeyVerificationKeyContent keyVerificationKeyContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"transaction_id\")))", target = "transactionId")
    @Mapping(expression = "java(toStringMap(jsonObject.getJsonObject(\"mac\")))", target = "mac")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"keys\")))", target = "keys")
    KeyVerificationMacContent keyVerificationMacContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"call_id\")))", target = "callId")
    @Mapping(expression = "java(answer(jsonObject.getJsonObject(\"answer\")))", target = "answer")
    @Mapping(expression = "java(toLong(jsonObject.getJsonNumber(\"version\")))", target = "version")
    CallAnswerContent callAnswerContent(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"type\")))", target = "type")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"sdp\")))", target = "sdp")
    Answer answer(JsonObject jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"call_id\")))", target = "callId")
    @Mapping(expression = "java(candidates(jsonObject.getJsonArray(\"candidates\")))", target = "candidates")
    @Mapping(expression = "java(toLong(jsonObject.getJsonNumber(\"version\")))", target = "version")
    CallCandidatesContent callCandidatesContent(JsonObject jsonObject);

    List<Candidate> candidates(JsonArray jsonObject);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"sdpMid\")))", target = "sdpMid")
    @Mapping(expression = "java(toLong(jsonObject.getJsonNumber(\"sdpMLineIndex\")))", target = "sdpMLineIndex")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"candidate\")))", target = "candidate")
    Candidate candidate(JsonObject jsonObject);
}
