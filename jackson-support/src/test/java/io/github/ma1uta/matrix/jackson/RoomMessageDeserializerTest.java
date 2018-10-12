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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.message.Notice;
import io.github.ma1uta.matrix.event.message.RawMessageContent;
import io.github.ma1uta.matrix.event.message.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class RoomMessageDeserializerTest {

    private ObjectMapper mapper;

    private RoomMessageDeserializer roomMessageDeserializer;

    @BeforeEach
    public void before() {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        roomMessageDeserializer = new RoomMessageDeserializer();
    }

    @Test
    public void msgtype() throws Exception {
        JsonParser parser = mapper.getFactory().createParser("{\"body\":\"exception\"}");
        ObjectCodec codec = parser.getCodec();
        RoomMessageContent message = roomMessageDeserializer.deserialize(codec.readTree(parser), codec);

        assertNotNull(message);
        assertTrue(message instanceof RawMessageContent);
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"body\":\"test\",\"msgtype\":\"m.text\"};test",
        "{\"msgtype\":\"m.text\"};"
    }, delimiter = ';')
    public void text(String event, String body) throws Exception {
        JsonParser parser = mapper.getFactory().createParser(event);
        ObjectCodec codec = parser.getCodec();
        RoomMessageContent message = roomMessageDeserializer.deserialize(codec.readTree(parser), codec);

        assertTrue(message instanceof Text);

        Text text = (Text) message;
        assertEquals(body, text.getBody());
    }

    @ParameterizedTest
    @CsvSource(value = {
        "{\"body\":\"test\",\"msgtype\":\"m.notice\"};test",
        "{\"msgtype\":\"m.notice\"};"
    }, delimiter = ';')
    public void notice(String event, String body) throws Exception {
        JsonParser parser = mapper.getFactory().createParser(event);
        ObjectCodec codec = parser.getCodec();
        RoomMessageContent message = roomMessageDeserializer.deserialize(codec.readTree(parser), codec);

        assertTrue(message instanceof Notice);

        Notice notice = (Notice) message;
        assertEquals(body, notice.getBody());
    }
}
