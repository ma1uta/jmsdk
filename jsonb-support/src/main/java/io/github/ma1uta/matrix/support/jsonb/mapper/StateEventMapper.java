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

import io.github.ma1uta.matrix.event.RoomAliases;
import io.github.ma1uta.matrix.event.RoomAvatar;
import io.github.ma1uta.matrix.event.RoomCanonicalAlias;
import io.github.ma1uta.matrix.event.RoomCreate;
import io.github.ma1uta.matrix.event.RoomEncryption;
import io.github.ma1uta.matrix.event.RoomGuestAccess;
import io.github.ma1uta.matrix.event.RoomHistoryVisibility;
import io.github.ma1uta.matrix.event.RoomJoinRules;
import io.github.ma1uta.matrix.event.StateEvent;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.json.JsonObject;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable"})
public interface StateEventMapper extends RoomEventMapper {

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
}
