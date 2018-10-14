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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.RoomName;
import io.github.ma1uta.matrix.event.RoomTopic;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.content.RoomNameContent;
import io.github.ma1uta.matrix.event.content.RoomTopicContent;
import io.github.ma1uta.matrix.event.message.Notice;
import io.github.ma1uta.matrix.event.message.RawMessageContent;
import io.github.ma1uta.matrix.event.message.Text;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

public class RoomMessageContentDeserializerTest {

    private Jsonb mapper;

    @BeforeEach
    public void before() {
        mapper = JsonbBuilder.create(
            new JsonbConfig().withDeserializers(
                new RoomEncryptedMessageContentDeserializer(),
                new EventDeserializer(),
                new RoomMessageDeserializer()
            ).withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL)
        );
    }

    @Test
    public void msgtype() {
        RoomMessageContent content = mapper.fromJson("{\"body\":\"exception\"}", RoomMessageContent.class);
        assertTrue(content instanceof RawMessageContent);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"body\":\"test\",\"msgtype\":\"m.text\"};test",
        "{\"msgtype\":\"m.text\"};"
    }, delimiter = ';')
    public void text(String eventArg, String bodyArg) throws Exception {
        RoomMessageContent message = mapper.fromJson(eventArg, RoomMessageContent.class);

        Assertions.assertTrue(message instanceof Text);

        Text text = (Text) message;
        Assertions.assertEquals(bodyArg, text.getBody());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"body\":\"test\",\"msgtype\":\"m.notice\"};test",
        "{\"msgtype\":\"m.notice\"};"
    }, delimiter = ';')
    public void notice(String eventArg, String bodyArg) {
        RoomMessageContent message = mapper.fromJson(eventArg, RoomMessageContent.class);

        Assertions.assertTrue(message instanceof Notice);

        Notice notice = (Notice) message;
        Assertions.assertEquals(bodyArg, notice.getBody());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"type\":\"m.room.name\",\"content\":{\"name\":\"test\"}};test",
    }, delimiter = ';')
    public void roomName(String eventArg, String nameArg) {
        Event event = mapper.fromJson(eventArg, Event.class);

        assertTrue(event instanceof RoomName);
        RoomName roomName = (RoomName) event;

        assertEquals("m.room.name", roomName.getType());
        RoomNameContent content = roomName.getContent();
        assertNotNull(content);
        assertEquals(nameArg, content.getName());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"type\":\"m.room.topic\",\"content\":{\"topic\":\"Topic name\"},\"prev_content\":{\"topic\":\"Old topic\"}};Topic name;Old topic;RoomTopic",
    }, delimiter = ';')
    public void roomTopic(String eventArg, String newContent, String oldContent, String eventClass) {
        Event eventObject = mapper.fromJson(eventArg, Event.class);

        assertEquals(eventClass, eventObject.getClass().getSimpleName());
        assertTrue(eventObject instanceof RoomTopic);

        RoomTopic stateEvent = (RoomTopic) eventObject;
        RoomTopicContent content = stateEvent.getContent();
        RoomTopicContent prevContent = stateEvent.getPrevContent();

        assertNotNull(content);
        assertNotNull(prevContent);

        assertEquals(content.getClass(), prevContent.getClass());
        assertEquals(newContent, content.getTopic());
        assertEquals(oldContent, prevContent.getTopic());
    }
}
