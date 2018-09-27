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

package io.github.ma1uta.matrix.client.methods;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.test.ClientToJettyServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

class ClientConfigMethodsTest extends ClientToJettyServer {

    @Test
    public void addConfigUnAutentificated() {
        assertThrows(NullPointerException.class, () -> addConfig(false));
    }

    @Test
    public void addConfig() throws Exception {
        addConfig(true);
    }

    void addConfig(boolean withToken) throws Exception {
        getServlet().setPut((req, res) -> {
            try {

                if (authenticated(req, res)) {
                    assertTrue(req.getRequestURI()
                        .startsWith("/_matrix/client/r0/user/%40alice%3Aexample.com/account_data/org.example.custom.config"));

                    JsonNode jsonNode = new ObjectMapper().readValue(req.getReader().lines().collect(Collectors.joining()), JsonNode.class);
                    assertEquals("custom_config_value", jsonNode.get("custom_account_data_key").asText());

                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType(MediaType.APPLICATION_JSON);
                    res.getWriter().println("{}");
                }
            } catch (IOException e) {
                fail();
            }
        });

        if (withToken) {
            getMatrixClient().getDefaultParams().userId("@alice:example.com");
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        }
        Map<String, String> config = new HashMap<>();
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
        addConfig(true);
    }

    void addRoomConfig(boolean withToken) throws Exception {
        getServlet().setPut((req, res) -> {
            try {

                if (authenticated(req, res)) {
                    assertTrue(req.getRequestURI()
                        .startsWith(
                            "/_matrix/client/r0/user/%40alice%3Aexample.com/rooms/%21726s6s6q%3Aexample.com/account_data/org.example.custom.room.config"));

                    JsonNode jsonNode = new ObjectMapper().readValue(req.getReader().lines().collect(Collectors.joining()), JsonNode.class);
                    assertEquals("custom_config_value", jsonNode.get("custom_account_data_key").asText());

                    res.setStatus(HttpServletResponse.SC_OK);
                    res.setContentType(MediaType.APPLICATION_JSON);
                    res.getWriter().println("{}");
                }
            } catch (IOException e) {
                fail();
            }
        });

        if (withToken) {
            getMatrixClient().getDefaultParams().userId("@alice:example.com");
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        }
        Map<String, String> config = new HashMap<>();
        config.put("custom_account_data_key", "custom_config_value");
        EmptyResponse emptyResponse = getMatrixClient().clientConfig().addRoomConfig("!726s6s6q", "org.example.custom.config", config)
            .get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(emptyResponse);
    }
}
