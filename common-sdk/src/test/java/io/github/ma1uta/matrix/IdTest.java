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

package io.github.ma1uta.matrix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

public class IdTest {

    @ParameterizedTest
    @CsvSource(value = {
        "$event:server.tld;event;server.tld",
        "@user:server.tld;user;server.tld",
        "@us1:127.0.0.1;us1;127.0.0.1",
        "@use2:0.0.0.0:200;use2;0.0.0.0:200",
        "!room:server.tld;room;server.tld",
        "!room:[1::];room;[1::]",
        "+group:server.tld;group;server.tld",
    }, delimiter = ';')
    public void id(String idValue, String localpart, String domain) {
        Id id = Id.getInstance();
        assertTrue(id.isId(idValue));
        assertEquals(localpart, id.localpart(idValue));
        assertEquals(domain, id.domain(idValue));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "@correct_user=/id.12:server.tld"
    })
    public void user(String id) {
        assertTrue(Id.getInstance().isUserId(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "###_alias_###@@@:server.tld",
        "#/ali12/=@#test:server.tld"
    })
    public void alias(String id) {
        assertTrue(Id.getInstance().isAliasId(id));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "!asd:server.tld",
        "@A:server.tld",
        "@a:b:server.tld"
    })
    public void wrongUserId(String id) {
        assertFalse(Id.getInstance().isUserId(id));
    }
}
