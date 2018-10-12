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

import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.message.Notice;
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
                new EncryptedMessageDeserializer(),
                new EventDeserializer(),
                new StrippedStateDeserializer(),
                new RoomMessageDeserializer()
            ).withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL)
        );

    }

    @Test
    public void msgtype() throws Exception {
        assertThrows(RuntimeException.class, () -> mapper.fromJson("{\"body\":\"exception\"}", RoomMessageContent.class));
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"body\":\"test\",\"msgtype\":\"m.text\"};\"test\"",
        "{\"msgtype\":\"m.text\"};"
    }, delimiter = ';')
    public void text(String event, String body) throws Exception {
        RoomMessageContent message = mapper.fromJson(event, RoomMessageContent.class);

        Assertions.assertTrue(message instanceof Text);

        Text text = (Text) message;
        Assertions.assertEquals(body, text.getBody());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"body\":\"test\",\"msgtype\":\"m.notice\"};\"test\"",
        "{\"msgtype\":\"m.notice\"};"
    }, delimiter = ';')
    public void notice(String event, String body) throws Exception {
        RoomMessageContent message = mapper.fromJson(event, RoomMessageContent.class);

        Assertions.assertTrue(message instanceof Notice);

        Notice notice = (Notice) message;
        Assertions.assertEquals(body, notice.getBody());
    }
}
