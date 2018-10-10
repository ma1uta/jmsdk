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

import static io.github.ma1uta.matrix.Event.MessageType.AUDIO;
import static io.github.ma1uta.matrix.Event.MessageType.EMOTE;
import static io.github.ma1uta.matrix.Event.MessageType.FILE;
import static io.github.ma1uta.matrix.Event.MessageType.IMAGE;
import static io.github.ma1uta.matrix.Event.MessageType.LOCATION;
import static io.github.ma1uta.matrix.Event.MessageType.NOTICE;
import static io.github.ma1uta.matrix.Event.MessageType.TEXT;
import static io.github.ma1uta.matrix.Event.MessageType.VIDEO;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.events.RoomMessage;
import io.github.ma1uta.matrix.events.messages.Audio;
import io.github.ma1uta.matrix.events.messages.Emote;
import io.github.ma1uta.matrix.events.messages.File;
import io.github.ma1uta.matrix.events.messages.Image;
import io.github.ma1uta.matrix.events.messages.Location;
import io.github.ma1uta.matrix.events.messages.Notice;
import io.github.ma1uta.matrix.events.messages.RawMessage;
import io.github.ma1uta.matrix.events.messages.Text;
import io.github.ma1uta.matrix.events.messages.Video;

/**
 * The room message deserializer.
 */
public class RoomMessageDeserializer {

    /**
     * Deserialize the room message.
     *
     * @param node  the json node with the message.
     * @param codec the json codec.
     * @return deserialized value or null.
     * @throws JsonProcessingException when cannot deserialize the room message.
     */
    public RoomMessage deserialize(JsonNode node, ObjectCodec codec) throws JsonProcessingException {
        if (node == null) {
            return null;
        }

        JsonNode typeNode = node.get("msgtype");
        if (typeNode == null || !typeNode.isTextual()) {
            return new RawMessage(node, null);
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
                return new RawMessage(node, msgtype);
        }
    }
}
