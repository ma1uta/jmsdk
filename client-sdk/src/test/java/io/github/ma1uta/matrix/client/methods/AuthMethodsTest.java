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
import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.auth.LoginRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginType;
import io.github.ma1uta.matrix.client.model.auth.UserIdentifier;
import io.github.ma1uta.matrix.client.test.ClientToJettyServer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;

class AuthMethodsTest extends ClientToJettyServer {

    @Test
    public void getLogin() throws Exception {
        getServlet().setGet((req, res) -> {
            try {
                assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/login"));

                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"flows\": [\n" +
                    "    {\n" +
                    "      \"type\": \"m.login.password\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        List<LoginType> loginTypes = getMatrixClient().auth().loginTypes().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(loginTypes);
        assertEquals(1, loginTypes.size());
        LoginType loginType = loginTypes.get(0);
        assertNotNull(loginType);
        assertEquals(AuthApi.AuthType.PASSWORD, loginType.getType());
    }

    @Test
    public void login() throws Exception {
        getServlet().setPost((req, res) -> {
            try {
                assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/login"));
                assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

                JsonNode jsonNode = new ObjectMapper().readValue(req.getReader().lines().collect(Collectors.joining()), JsonNode.class);
                assertEquals(AuthApi.AuthType.PASSWORD, jsonNode.get("type").asText());
                assertEquals("ilovebananas", jsonNode.get("password").asText());
                assertEquals("Jungle Phone", jsonNode.get("initial_device_display_name").asText());

                JsonNode identifier = jsonNode.get("identifier");
                assertNotNull(identifier);
                assertEquals("m.id.user", identifier.get("type").asText());
                assertEquals("cheeky_monkey", identifier.get("user").asText());

                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"user_id\": \"@cheeky_monkey:matrix.org\",\n" +
                    "  \"access_token\": \"abc123\",\n" +
                    "  \"device_id\": \"GHTYAJCE\"\n" +
                    "}");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setType(AuthApi.AuthType.PASSWORD);
        loginRequest.setInitialDeviceDisplayName("Jungle Phone");
        loginRequest.setPassword("ilovebananas");
        UserIdentifier identifier = new UserIdentifier();
        identifier.setUser("cheeky_monkey");
        loginRequest.setIdentifier(identifier);
        LoginResponse loginResponse = getMatrixClient().auth().login(loginRequest).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(loginResponse);
        assertEquals("@cheeky_monkey:matrix.org", loginResponse.getUserId());
        assertEquals("abc123", loginResponse.getAccessToken());
        assertEquals("GHTYAJCE", loginResponse.getDeviceId());
    }

    @Test
    public void logout() throws Exception {
        logout(true);
    }

    @Test
    public void logoutUnAuthorized() {
        assertThrows(IllegalArgumentException.class, () -> logout(false));
    }

    public void logout(boolean withToken) throws Exception {
        getServlet().setPost((req, res) -> {
            try {
                assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/logout"));
                assertEquals(MediaType.APPLICATION_JSON, req.getContentType());
                if (authenticated(req, res)) {
                    res.setContentType(MediaType.APPLICATION_JSON);
                    res.getWriter().println("{}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        if (withToken) {
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        }
        EmptyResponse res = getMatrixClient().auth().logout().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(res);
    }

    @Test
    public void logoutAll() throws Exception {
        logout(true);
    }

    @Test
    public void logoutAllUnAuthorized() {
        assertThrows(IllegalArgumentException.class, () -> logout(false));
    }

    public void logoutAll(boolean withToken) throws Exception {
        getServlet().setPost((req, res) -> {
            try {
                assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/logoutAll"));
                assertEquals(MediaType.APPLICATION_JSON, req.getContentType());
                if (authenticated(req, res)) {
                    res.setContentType(MediaType.APPLICATION_JSON);
                    res.getWriter().println("{}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        });

        if (withToken) {
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        }
        EmptyResponse res = getMatrixClient().auth().logoutAll().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(res);
    }
}
