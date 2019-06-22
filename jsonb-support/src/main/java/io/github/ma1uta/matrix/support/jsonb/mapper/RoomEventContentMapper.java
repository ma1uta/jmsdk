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
import io.github.ma1uta.matrix.event.content.CallHangupContent;
import io.github.ma1uta.matrix.event.content.CallInviteContent;
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.content.RoomMessageFeedbackContent;
import io.github.ma1uta.matrix.event.content.RoomRedactionContent;
import io.github.ma1uta.matrix.event.content.StickerContent;
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
import io.github.ma1uta.matrix.event.nested.JWK;
import io.github.ma1uta.matrix.event.nested.LocationInfo;
import io.github.ma1uta.matrix.event.nested.Offer;
import io.github.ma1uta.matrix.event.nested.Relates;
import io.github.ma1uta.matrix.event.nested.Reply;
import io.github.ma1uta.matrix.event.nested.ThumbnailInfo;
import io.github.ma1uta.matrix.event.nested.VideoInfo;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

@SuppressWarnings( {"javadocType", "javadocMethod", "javadocVariable", "LineLength"})
public interface RoomEventContentMapper extends SimpleEventContentMapper {

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
}
