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
import io.github.ma1uta.matrix.event.message.ServerNotice;
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
            case Audio.MSGTYPE:
                return codec.treeToValue(node, Audio.class);
            case Emote.MSGTYPE:
                return codec.treeToValue(node, Emote.class);
            case File.MSGTYPE:
                return codec.treeToValue(node, File.class);
            case Image.MSGTYPE:
                return codec.treeToValue(node, Image.class);
            case Location.MSGTYPE:
                return codec.treeToValue(node, Location.class);
            case Notice.MSGTYPE:
                return codec.treeToValue(node, Notice.class);
            case Text.MSGTYPE:
                return codec.treeToValue(node, Text.class);
            case Video.MSGTYPE:
                return codec.treeToValue(node, Video.class);
            case ServerNotice.MSGTYPE:
                return codec.treeToValue(node, ServerNotice.class);
            default:
                return parse(node, ctxt, codec, msgtype);
        }
    }

    protected RoomMessageContent parse(JsonNode jsonNode, DeserializationContext ctxt, ObjectCodec codec, String msgtype) {
        return new RawMessageContent(jsonNode, msgtype);
    }
}
