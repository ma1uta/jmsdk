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

import io.github.ma1uta.matrix.event.content.RoomAliasesContent;
import io.github.ma1uta.matrix.event.content.RoomAvatarContent;
import io.github.ma1uta.matrix.event.content.RoomCanonicalAliasContent;
import io.github.ma1uta.matrix.event.content.RoomCreateContent;
import io.github.ma1uta.matrix.event.content.RoomEncryptionContent;
import io.github.ma1uta.matrix.event.content.RoomGuestAccessContent;
import io.github.ma1uta.matrix.event.content.RoomHistoryVisibilityContent;
import io.github.ma1uta.matrix.event.content.RoomJoinRulesContent;
import io.github.ma1uta.matrix.event.content.RoomMemberContent;
import io.github.ma1uta.matrix.event.nested.PreviousRoom;
import org.mapstruct.Mapping;

import javax.json.JsonObject;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable", "LineLength"})
public interface StateEventContentMapper extends RoomEventContentMapper {

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

    RoomMemberContent roomMemberContent(JsonObject jsonObject);
}
