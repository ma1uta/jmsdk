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

package io.github.ma1uta.matrix.jackson;

import static io.github.ma1uta.matrix.event.Event.MessageType.AUDIO;
import static io.github.ma1uta.matrix.event.Event.MessageType.EMOTE;
import static io.github.ma1uta.matrix.event.Event.MessageType.FILE;
import static io.github.ma1uta.matrix.event.Event.MessageType.IMAGE;
import static io.github.ma1uta.matrix.event.Event.MessageType.LOCATION;
import static io.github.ma1uta.matrix.event.Event.MessageType.NOTICE;
import static io.github.ma1uta.matrix.event.Event.MessageType.TEXT;
import static io.github.ma1uta.matrix.event.Event.MessageType.VIDEO;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.message.Audio;
import io.github.ma1uta.matrix.event.message.Emote;
import io.github.ma1uta.matrix.event.message.File;
import io.github.ma1uta.matrix.event.message.Image;
import io.github.ma1uta.matrix.event.message.Location;
import io.github.ma1uta.matrix.event.message.Notice;
import io.github.ma1uta.matrix.event.message.RawMessageContent;
import io.github.ma1uta.matrix.event.message.Text;
import io.github.ma1uta.matrix.event.message.Video;

import java.io.IOException;

/**
 * The room message deserializer.
 */
public class RoomMessageContentDeserializer extends JsonDeserializer<RoomMessageContent> {

    @Override
    public RoomMessageContent deserialize(JsonParser parser, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        if (node == null) {
            return null;
        }

        JsonNode typeNode = node.get("msgtype");
        if (typeNode == null || !typeNode.isTextual()) {
            return parse(node, ctxt, codec, null);
        }
        String msgtype = typeNode.asText();
        switch (msgtype) {
            case AUDIO:
                return codec.treeToValue(node, Audio.class);
            case EMOTE:
                return codec.treeToValue(node, Emote.class);
            case FILE:
                return codec.treeToValue(node, File.class);
            case IMAGE:
                return codec.treeToValue(node, Image.class);
            case LOCATION:
                return codec.treeToValue(node, Location.class);
            case NOTICE:
                return codec.treeToValue(node, Notice.class);
            case TEXT:
                return codec.treeToValue(node, Text.class);
            case VIDEO:
                return codec.treeToValue(node, Video.class);
            default:
                return parse(node, ctxt, codec, msgtype);
        }
    }

    protected RoomMessageContent parse(JsonNode jsonNode, DeserializationContext ctxt, ObjectCodec codec, String msgtype) {
        return new RawMessageContent(jsonNode, msgtype);
    }
}
