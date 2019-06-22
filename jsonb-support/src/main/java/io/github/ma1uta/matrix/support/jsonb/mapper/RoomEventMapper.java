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
import io.github.ma1uta.matrix.event.CallHangup;
import io.github.ma1uta.matrix.event.CallInvite;
import io.github.ma1uta.matrix.event.RoomEncrypted;
import io.github.ma1uta.matrix.event.RoomEvent;
import io.github.ma1uta.matrix.event.RoomMessageFeedback;
import io.github.ma1uta.matrix.event.RoomRedaction;
import io.github.ma1uta.matrix.event.Sticker;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import javax.json.JsonObject;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable"})
public interface RoomEventMapper extends SimpleEventMapper {

    @Mapping(expression = "java(toString(jsonObject, \"event_id\"))", target = "eventId")
    @Mapping(expression = "java(toString(jsonObject, \"room_id\"))", target = "roomId")
    @Mapping(expression = "java(toString(jsonObject, \"sender\"))", target = "sender")
    @Mapping(expression = "java(toLong(jsonObject, \"origin_server_ts\"))", target = "originServerTs")
    void roomEvent(JsonObject jsonObject, @MappingTarget RoomEvent roomEvent);

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

    @Mapping(expression = "java(roomMessageFeedbackContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomMessageFeedback roomMessageFeedback(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(roomRedactionContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    RoomRedaction roomRedaction(JsonObject jsonObject);

    @InheritConfiguration
    @Mapping(expression = "java(stickerContent(jsonObject.getJsonObject(\"content\")))", target = "content")
    Sticker sticker(JsonObject jsonObject);
}
