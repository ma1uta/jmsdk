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

package io.github.ma1uta.matrix.support.jsonb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.ma1uta.matrix.event.Receipt;
import io.github.ma1uta.matrix.event.content.ReceiptContent;
import io.github.ma1uta.matrix.event.nested.ReceiptInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyOrderStrategy;

public class ReceiptContentTest {

    private Jsonb mapper;

    @BeforeEach
    public void before() {
        mapper = JsonbBuilder.create(
            new JsonbConfig().withDeserializers(
                new EventDeserializer(),
                new RoomMessageContentDeserializer()
            ).withPropertyOrderStrategy(PropertyOrderStrategy.LEXICOGRAPHICAL)
        );
    }

    @Test
    public void receiptTsAsObject() throws IOException {
        Receipt receipt = mapper.fromJson("{\n" +
            "    \"content\": {\n" +
            "        \"$1553207419136504TMSfD:matrix.org\": {\n" +
            "            \"m.read\": {\n" +
            "                \"@joupho:matrix.ketchupma.io\": {\n" +
            "                    \"ts\": 1553208081353\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"type\": \"m.receipt\",\n" +
            "    \"room_id\": \"123\"\n" +
            "}", Receipt.class);
        ReceiptContent receiptContent = receipt.getContent();
        ReceiptInfo receiptInfo = receiptContent.get("$1553207419136504TMSfD:matrix.org");
        assertNotNull(receiptInfo);
        assertEquals(1553208081353L, receiptInfo.getRead().get("@joupho:matrix.ketchupma.io").getTs());
    }

    @Test
    public void receiptTsAsString() throws IOException {
        Receipt receipt = mapper.fromJson("{\n" +
            "    \"content\": {\n" +
            "        \"$1553207419136504TMSfD:matrix.org\": {\n" +
            "            \"m.read\": {\n" +
            "                \"@joupho:matrix.ketchupma.io\": \"{\\\"ts\\\":1553208081353}\"\n" +
            "            }\n" +
            "        }\n" +
            "    },\n" +
            "    \"type\": \"m.receipt\",\n" +
            "    \"room_id\": \"123\"\n" +
            "}", Receipt.class);
        ReceiptContent receiptContent = receipt.getContent();
        ReceiptInfo receiptInfo = receiptContent.get("$1553207419136504TMSfD:matrix.org");
        assertNotNull(receiptInfo);
        assertEquals(1553208081353L, receiptInfo.getRead().get("@joupho:matrix.ketchupma.io").getTs());
    }
}
