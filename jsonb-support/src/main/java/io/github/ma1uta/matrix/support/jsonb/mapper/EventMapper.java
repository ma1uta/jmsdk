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

import io.github.ma1uta.matrix.event.CallAnswer;
import io.github.ma1uta.matrix.event.CallCandidates;
import io.github.ma1uta.matrix.event.Direct;
import io.github.ma1uta.matrix.event.Dummy;
import io.github.ma1uta.matrix.event.ForwardedRoomKey;
import io.github.ma1uta.matrix.event.FullyRead;
import io.github.ma1uta.matrix.event.IgnoredUserList;
import io.github.ma1uta.matrix.event.KeyVerificationAccept;
import io.github.ma1uta.matrix.event.KeyVerificationCancel;
import io.github.ma1uta.matrix.event.KeyVerificationKey;
import io.github.ma1uta.matrix.event.KeyVerificationMac;
import io.github.ma1uta.matrix.event.RoomEvent;
import io.github.ma1uta.matrix.event.StateEvent;
import io.github.ma1uta.matrix.event.content.DummyContent;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import javax.json.JsonObject;
import javax.json.JsonValue;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable"})
@Mapper(unmappedSourcePolicy = ReportingPolicy.IGNORE, imports = JsonValue.class)
public interface EventMapper extends EventContentMapper {

    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"event_id\")))", target = "eventId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"room_id\")))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"sender\")))", target = "sender")
    @Mapping(expression = "java(toLong(jsonObject.getJsonNumber(\"origin_server_ts\")))", target = "originServerTs")
    void roomEvent(JsonObject jsonObject, @MappingTarget RoomEvent roomEvent);

    @InheritConfiguration
    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"state_key\")))", target = "stateKey")
    void stateEvent(JsonObject jsonObject, @MappingTarget StateEvent stateEvent);

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

    @Mapping(expression = "java(toString(jsonObject.getJsonString(\"room_id\")))", target = "roomId")
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

    @InheritConfiguration
    @Mapping(expression = "java(callAnswerContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    CallAnswer callAnswer(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(callCandidatesContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    CallCandidates callCandidates(JsonObject jsonObject);
}
