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

package io.github.ma1uta.matrix.client;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.ma1uta.matrix.EmptyResponse;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;

class ClientConfigMethodsTest extends MockServer {

    @Test
    public void addConfigUnAutentificated() {
        assertThrows(NullPointerException.class, () -> addConfig(false));
    }

    @Test
    public void addConfig() throws Exception {
        addConfig(true);
    }

    void addConfig(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            put(urlPathMatching("/_matrix/client/r0/user/@alice:example.com/account_data/org.example.custom.config"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .withRequestBody(equalToJson("{\"custom_account_data_key\": \"custom_config_value\"}"))
                .willReturn(okJson("{}"))
        );

        if (withToken) {
            getMatrixClient().getAccountInfo().setUserId("@alice:example.com");
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        Map<String, Object> config = new HashMap<>();
        config.put("custom_account_data_key", "custom_config_value");
        EmptyResponse emptyResponse = getMatrixClient().clientConfig().addConfig("org.example.custom.config", config)
            .get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(emptyResponse);
    }

    @Test
    public void addRoomConfigUnAutentificated() {
        assertThrows(NullPointerException.class, () -> addRoomConfig(false));
    }

    @Test
    public void addRoomConfig() throws Exception {
        addRoomConfig(true);
    }

    void addRoomConfig(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            put(urlPathMatching(
                "/_matrix/client/r0/user/@alice:example.com/rooms/!726s6s6q:example.com/account_data/org.example.custom.room.config"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .withRequestBody(equalToJson("{\"custom_account_data_key\": \"custom_config_value\"}"))
                .willReturn(okJson("{}"))
        );

        if (withToken) {
            getMatrixClient().getAccountInfo().setUserId("@alice:example.com");
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        Map<String, Object> config = new HashMap<>();
        config.put("custom_account_data_key", "custom_config_value");
        EmptyResponse emptyResponse = getMatrixClient().clientConfig()
            .addRoomConfig("!726s6s6q:example.com", "org.example.custom.room.config", config).get(100000, TimeUnit.MILLISECONDS);
        assertNotNull(emptyResponse);
    }
}
