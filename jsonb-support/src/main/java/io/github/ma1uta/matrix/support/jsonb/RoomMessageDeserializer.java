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

package io.github.ma1uta.matrix.support.jsonb;

import static io.github.ma1uta.matrix.Event.MessageType.AUDIO;
import static io.github.ma1uta.matrix.Event.MessageType.EMOTE;
import static io.github.ma1uta.matrix.Event.MessageType.FILE;
import static io.github.ma1uta.matrix.Event.MessageType.IMAGE;
import static io.github.ma1uta.matrix.Event.MessageType.LOCATION;
import static io.github.ma1uta.matrix.Event.MessageType.NOTICE;
import static io.github.ma1uta.matrix.Event.MessageType.TEXT;
import static io.github.ma1uta.matrix.Event.MessageType.VIDEO;

import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.message.Audio;
import io.github.ma1uta.matrix.event.message.Emote;
import io.github.ma1uta.matrix.event.message.File;
import io.github.ma1uta.matrix.event.message.FormattedBody;
import io.github.ma1uta.matrix.event.message.Image;
import io.github.ma1uta.matrix.event.message.Location;
import io.github.ma1uta.matrix.event.message.Notice;
import io.github.ma1uta.matrix.event.message.RawMessageContent;
import io.github.ma1uta.matrix.event.message.Text;
import io.github.ma1uta.matrix.event.message.Video;
import io.github.ma1uta.matrix.event.nested.AudioInfo;
import io.github.ma1uta.matrix.event.nested.EncryptedFile;
import io.github.ma1uta.matrix.event.nested.FileInfo;
import io.github.ma1uta.matrix.event.nested.ImageInfo;
import io.github.ma1uta.matrix.event.nested.LocationInfo;
import io.github.ma1uta.matrix.event.nested.Relates;
import io.github.ma1uta.matrix.event.nested.VideoInfo;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;

/**
 * JSON-B deserializer of the Room Messages.
 */
public class RoomMessageDeserializer implements JsonbDeserializer<RoomMessageContent> {

    private final JsonParserFactory factory = Json.createParserFactory(Collections.emptyMap());

    protected JsonParserFactory factory() {
        return factory;
    }

    @Override
    public RoomMessageContent deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
        String msgtype = null;
        Map<String, JsonValue> props = new HashMap<>();

        while (parser.hasNext()) {
            JsonParser.Event event = parser.next();
            if (JsonParser.Event.END_OBJECT == event) {
                break;
            }

            if (JsonParser.Event.KEY_NAME == event) {
                String name = parser.getString();

                parser.next();
                switch (name) {
                    case "msgtype":
                        msgtype = parser.getString();
                        break;
                    default:
                        props.put(name, parser.getValue());
                }
            }
        }

        if (msgtype == null) {
            throw new RuntimeException("Missing required property msgtype.");
        }

        switch (msgtype) {
            case AUDIO:
                return audio(props, ctx);
            case EMOTE:
                return emote(props, ctx);
            case FILE:
                return file(props, ctx);
            case IMAGE:
                return image(props, ctx);
            case LOCATION:
                return location(props, ctx);
            case NOTICE:
                return notice(props, ctx);
            case TEXT:
                return text(props, ctx);
            case VIDEO:
                return video(props, ctx);
            default:
                return raw(props, msgtype, ctx);
        }
    }

    protected Audio audio(Map<String, JsonValue> props, DeserializationContext ctx) {
        Audio audio = body(new Audio(), props, ctx);
        audio.setUrl(prop(props, ctx, "url"));
        audio.setInfo(info(props, ctx, "info", AudioInfo.class));
        audio.setFile(info(props, ctx, "file", EncryptedFile.class));
        return audio;
    }

    protected Emote emote(Map<String, JsonValue> props, DeserializationContext ctx) {
        return formattedBody(body(new Emote(), props, ctx), props, ctx);
    }

    protected File file(Map<String, JsonValue> props, DeserializationContext ctx) {
        File file = body(new File(), props, ctx);
        file.setFilename(prop(props, ctx, "filename"));
        file.setUrl(prop(props, ctx, "url"));
        file.setInfo(info(props, ctx, "info", FileInfo.class));
        file.setFile(info(props, ctx, "file", EncryptedFile.class));
        return file;
    }

    protected Image image(Map<String, JsonValue> props, DeserializationContext ctx) {
        Image image = body(new Image(), props, ctx);
        image.setUrl(prop(props, ctx, "url"));
        image.setInfo(info(props, ctx, "info", ImageInfo.class));
        image.setFile(info(props, ctx, "file", EncryptedFile.class));
        return image;
    }

    protected Location location(Map<String, JsonValue> props, DeserializationContext ctx) {
        Location location = body(new Location(), props, ctx);
        location.setGeoUri(prop(props, ctx, "geo_uri"));
        location.setInfo(info(props, ctx, "info", LocationInfo.class));
        return location;
    }

    protected Notice notice(Map<String, JsonValue> props, DeserializationContext ctx) {
        return formattedBody(body(new Notice(), props, ctx), props, ctx);
    }

    protected Text text(Map<String, JsonValue> props, DeserializationContext ctx) {
        return formattedBody(body(new Text(), props, ctx), props, ctx);
    }

    protected Video video(Map<String, JsonValue> props, DeserializationContext ctx) {
        Video video = body(new Video(), props, ctx);
        video.setUrl(prop(props, ctx, "url"));
        video.setInfo(info(props, ctx, "info", VideoInfo.class));
        video.setFile(info(props, ctx, "file", EncryptedFile.class));
        return video;
    }

    protected RawMessageContent raw(Map<String, JsonValue> props, String type, DeserializationContext ctx) {
        return new RawMessageContent(props, type);
    }

    protected <T extends RoomMessageContent> T body(T roomMessage, Map<String, JsonValue> props, DeserializationContext ctx) {
        roomMessage.setBody(prop(props, ctx, "body"));
        roomMessage.setRelatesTo(info(props, ctx, "m.relates_to", Relates.class));
        return roomMessage;
    }

    protected <T extends FormattedBody> T formattedBody(T roomMessage, Map<String, JsonValue> props, DeserializationContext ctx) {
        roomMessage.setFormat(prop(props, ctx, "format"));
        roomMessage.setFormattedBody(prop(props, ctx, "formatted_body"));
        return roomMessage;
    }

    protected <T> T info(Map<String, JsonValue> props, DeserializationContext ctx, String propName, Class<T> propClass) {
        JsonValue value = props.get(propName);
        if (value == null || JsonValue.NULL.equals(value)) {
            return null;
        } else {
            try (JsonParser parser = factory().createParser(value.asJsonObject())) {
                return ctx.deserialize(propClass, parser);
            }
        }
    }

    protected String prop(Map<String, JsonValue> props, DeserializationContext ctx, String propName) {
        JsonValue value = props.get(propName);
        return value == null || JsonValue.NULL.equals(value) ? null : value.toString();
    }
}
