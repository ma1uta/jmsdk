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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.content.ReceiptContent;
import io.github.ma1uta.matrix.event.content.RoomEncryptedContent;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.nested.ReceiptInfo;
import io.github.ma1uta.matrix.event.nested.ReceiptTs;
import io.github.ma1uta.matrix.support.jackson.workaround.ReceiptTsDeserialized4898;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ReceiptContentTest {
    private ObjectMapper mapper;

    @BeforeEach
    public void before() {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Event.class, new EventDeserializer());
        module.addDeserializer(RoomMessageContent.class, new RoomMessageContentDeserializer());
        module.addDeserializer(RoomEncryptedContent.class, new RoomEncryptedContentDeserializer());
        module.addDeserializer(ReceiptTs.class, new ReceiptTsDeserialized4898());
        mapper.registerModule(module);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    public void receiptTsAsObject() throws IOException {
        ReceiptContent receiptContent = mapper.readValue("{\"$1553207419136504TMSfD:matrix.org\": {\n" +
            "  \"m.read\": {\n" +
            "    \"@joupho:matrix.ketchupma.io\": {\n" +
            "      \"ts\": 1553208081353\n" +
            "    }\n" +
            "  }\n" +
            "}}", ReceiptContent.class);
        ReceiptInfo receiptInfo = receiptContent.get("$1553207419136504TMSfD:matrix.org");
        assertNotNull(receiptInfo);
        assertEquals(1553208081353L, receiptInfo.getRead().get("@joupho:matrix.ketchupma.io").getTs());
    }

    @Test
    public void receiptTsAsString() throws IOException {
        ReceiptContent receiptContent = mapper.readValue("{\"$155312243221YYtNR:cooperteam.net\": {\n" +
            "  \"m.read\": {\n" +
            "    \"@kitsune:matrix.org\": \"{\\\"ts\\\": 1553137938081}\"\n" +
            "  }\n" +
            "}}", ReceiptContent.class);
        ReceiptInfo receiptInfo = receiptContent.get("$155312243221YYtNR:cooperteam.net");
        assertNotNull(receiptInfo);
        assertEquals(1553137938081L, receiptInfo.getRead().get("@kitsune:matrix.org").getTs());
    }
}
