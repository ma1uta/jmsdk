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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.ma1uta.matrix.client.AuthenticationRequred;
import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.account.AuthenticationData;
import io.github.ma1uta.matrix.client.model.account.EmailRequestToken;
import io.github.ma1uta.matrix.client.model.account.MsisdnRequestToken;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.test.ClientToJettyServer;
import io.github.ma1uta.matrix.thirdpid.SessionResponse;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

public class AccountMethodsTest extends ClientToJettyServer {

    @Test
    public void registerAndLogin() throws Exception {
        LoginResponse loginResponse = register(false, true);

        assertEquals("abc123", getMatrixClient().getDefaultParams().getAccessToken());
        assertEquals("@cheeky_monkey:matrix.org", getMatrixClient().getDefaultParams().getUserId());
        assertEquals("abc123", loginResponse.getAccessToken());
        assertEquals("GHTYAJCE", loginResponse.getDeviceId());
    }

    @Test
    public void onlyRegister() throws Exception {
        LoginResponse loginResponse = register(true, true);

        assertNull(getMatrixClient().getDefaultParams().getAccessToken());
        assertEquals("@cheeky_monkey:matrix.org", getMatrixClient().getDefaultParams().getUserId());
        assertNull(loginResponse.getDeviceId());
        assertNull(loginResponse.getAccessToken());
    }

    @Test
    public void tryToRegister() {
        ExecutionException e = assertThrows(ExecutionException.class, () -> register(false, false));
        assertTrue(e.getCause() instanceof AuthenticationRequred);
    }

    public LoginResponse register(boolean inhibitLogin, boolean withAuth) throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/register"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            try {
                JsonNode jsonNode = incomingJson(req);

                assertEquals("cheeky_monkey", jsonNode.get("username").asText());
                assertEquals("GHTYAJCE", jsonNode.get("device_id").asText());
                assertEquals("ilovebananas", jsonNode.get("password").asText());
                assertEquals("Jungle Phone", jsonNode.get("initial_device_display_name").asText());
                assertFalse(jsonNode.get("bind_email").asBoolean());
                assertEquals(inhibitLogin, jsonNode.get("inhibit_login").asBoolean());

                if (withAuth) {
                    JsonNode auth = jsonNode.get("auth");
                    assertEquals("m.login.password", auth.get("type").asText());
                    assertEquals("xxxxx", auth.get("session").asText());

                    res.setContentType(MediaType.APPLICATION_JSON);
                    res.getWriter().println("{\n" +
                        "  \"user_id\": \"@cheeky_monkey:matrix.org\"" + (inhibitLogin ? "" : ",\n" +
                        "  \"access_token\": \"abc123\",\n" +
                        "  \"device_id\": \"GHTYAJCE\"\n") +
                        "}");
                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
                }
            } catch (IOException e) {
                fail();
            }
        });

        RegisterRequest request = new RegisterRequest();
        request.setUsername("cheeky_monkey");
        request.setDeviceId("GHTYAJCE");
        request.setPassword("ilovebananas");
        request.setInitialDeviceDisplayName("Jungle Phone");
        request.setBindEmail(false);
        request.setInhibitLogin(inhibitLogin);

        if (withAuth) {
            AuthenticationData auth = new AuthenticationData();
            auth.setType(AuthApi.AuthType.PASSWORD);
            auth.setSession("xxxxx");
            request.setAuth(auth);
        }

        LoginResponse loginResponse = getMatrixClient().account().register(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(loginResponse);
        assertEquals("@cheeky_monkey:matrix.org", loginResponse.getUserId());

        return loginResponse;
    }

    @Test
    public void emailRequestToken() throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/register/email/requestToken"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            JsonNode jsonNode = incomingJson(req);

            assertEquals("monkeys_are_GREAT", jsonNode.get("client_secret").asText());
            assertEquals("alice@example.org", jsonNode.get("email").asText());
            assertEquals(1, jsonNode.get("send_attempt").asInt());
            assertEquals("https://example.org/congratulations.html", jsonNode.get("next_link").asText());
            assertEquals("id.example.com", jsonNode.get("id_server").asText());

            try {
                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}");
            } catch (IOException e) {
                fail();
            }
        });

        EmailRequestToken request = new EmailRequestToken();
        request.setClientSecret("monkeys_are_GREAT");
        request.setEmail("alice@example.org");
        request.setSendAttempt(1L);
        request.setNextLink("https://example.org/congratulations.html");
        request.setIdServer("id.example.com");
        SessionResponse sessionResponse = getMatrixClient().account().emailRequestToken(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(sessionResponse);
        assertEquals("123abc", sessionResponse.getSid());
    }

    @Test
    public void phoneRequestToken() throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/register/msisdn/requestToken"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            JsonNode jsonNode = incomingJson(req);

            assertEquals("monkeys_are_GREAT", jsonNode.get("client_secret").asText());
            assertEquals("GB", jsonNode.get("country").asText());
            assertEquals("07700900001", jsonNode.get("phone_number").asText());
            assertEquals(1, jsonNode.get("send_attempt").asInt());
            assertEquals("https://example.org/congratulations.html", jsonNode.get("next_link").asText());
            assertEquals("id.example.com", jsonNode.get("id_server").asText());

            try {
                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}");
            } catch (IOException e) {
                fail();
            }
        });

        MsisdnRequestToken request = new MsisdnRequestToken();
        request.setClientSecret("monkeys_are_GREAT");
        request.setCountry("GB");
        request.setPhoneNumber("07700900001");
        request.setSendAttempt(1L);
        request.setNextLink("https://example.org/congratulations.html");
        request.setIdServer("id.example.com");
        SessionResponse sessionResponse = getMatrixClient().account().msisdnRequestToken(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(sessionResponse);
        assertEquals("123abc", sessionResponse.getSid());
    }

    @Test
    public void password() throws Exception {
        password(true, true);
    }

    @Test
    public void tryToPassword() {
        assertThrows(IllegalArgumentException.class, () -> password(false, false));
    }

    @Test
    public void unauthincatedTryToPassword() {
        ExecutionException e = assertThrows(ExecutionException.class, () -> password(true, false));
        assertTrue(e.getCause() instanceof AuthenticationRequred);
    }

    public void password(boolean withToken, boolean withAuth) throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/account/password"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            if (authenticated(req, res)) {
                JsonNode jsonNode = incomingJson(req);

                assertEquals("ihatebananas", jsonNode.get("new_password").asText());
                JsonNode auth = jsonNode.get("auth");

                if (auth == null || auth.isNull()) {
                    try {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
                    } catch (IOException e) {
                        fail();
                    }
                } else {
                    try {
                        try {
                            assertEquals("m.login.password", auth.get("type").asText());
                            assertEquals("xxxxx", auth.get("session").asText());
                        } catch (AssertionFailedError e) {
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
                        }

                        res.setContentType(MediaType.APPLICATION_JSON);
                        res.getWriter().println("{}");
                    } catch (IOException e) {
                        fail();
                    }
                }
            }
        });

        if (withToken) {
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        }
        AuthenticationData auth = new AuthenticationData();
        if (withAuth) {
            auth.setType(AuthApi.AuthType.PASSWORD);
            auth.setSession("xxxxx");
        }
        assertNotNull(getMatrixClient().account().password("ihatebananas", auth).get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void passwordEmailRequestToken() throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/account/password/email/requestToken"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            JsonNode jsonNode = incomingJson(req);

            assertEquals("monkeys_are_GREAT", jsonNode.get("client_secret").asText());
            assertEquals("alice@example.org", jsonNode.get("email").asText());
            assertEquals(1, jsonNode.get("send_attempt").asInt());
            assertEquals("https://example.org/congratulations.html", jsonNode.get("next_link").asText());
            assertEquals("id.example.com", jsonNode.get("id_server").asText());

            try {
                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}");
            } catch (IOException e) {
                fail();
            }
        });

        EmailRequestToken request = new EmailRequestToken();
        request.setClientSecret("monkeys_are_GREAT");
        request.setEmail("alice@example.org");
        request.setSendAttempt(1L);
        request.setNextLink("https://example.org/congratulations.html");
        request.setIdServer("id.example.com");
        SessionResponse sessionResponse = getMatrixClient().account().passwordEmailRequestToken(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(sessionResponse);
        assertEquals("123abc", sessionResponse.getSid());
    }

    @Test
    public void passwordPhoneRequestToken() throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/account/password/msisdn/requestToken"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            JsonNode jsonNode = incomingJson(req);

            assertEquals("monkeys_are_GREAT", jsonNode.get("client_secret").asText());
            assertEquals("GB", jsonNode.get("country").asText());
            assertEquals("07700900001", jsonNode.get("phone_number").asText());
            assertEquals(1, jsonNode.get("send_attempt").asInt());
            assertEquals("https://example.org/congratulations.html", jsonNode.get("next_link").asText());
            assertEquals("id.example.com", jsonNode.get("id_server").asText());

            try {
                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}");
            } catch (IOException e) {
                fail();
            }
        });

        MsisdnRequestToken request = new MsisdnRequestToken();
        request.setClientSecret("monkeys_are_GREAT");
        request.setCountry("GB");
        request.setPhoneNumber("07700900001");
        request.setSendAttempt(1L);
        request.setNextLink("https://example.org/congratulations.html");
        request.setIdServer("id.example.com");
        SessionResponse sessionResponse = getMatrixClient().account().passwordMsisdnRequestToken(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(sessionResponse);
        assertEquals("123abc", sessionResponse.getSid());
    }

    @Test
    public void deactivate() throws Exception {
        deactivate(true, true);
    }

    @Test
    public void tryToDeactivate() {
        assertThrows(IllegalArgumentException.class, () -> deactivate(false, true));
    }

    @Test
    public void tryToDeactivateWithouAuth() {
        ExecutionException e = assertThrows(ExecutionException.class, () -> deactivate(true, false));
        assertTrue(e.getCause() instanceof AuthenticationRequred);
    }

    public void deactivate(boolean withToken, boolean withAuth) throws Exception {
        getServlet().setPost((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/account/deactivate"));
            assertEquals(MediaType.APPLICATION_JSON, req.getContentType());

            if (authenticated(req, res)) {
                JsonNode jsonNode = incomingJson(req);

                JsonNode auth = jsonNode.get("auth");

                if (auth == null || auth.isNull()) {
                    try {
                        res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
                    } catch (IOException e) {
                        fail();
                    }
                } else {
                    try {
                        try {
                            assertEquals("m.login.password", auth.get("type").asText());
                            assertEquals("sss", auth.get("session").asText());
                        } catch (AssertionFailedError e) {
                            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
                        }

                        res.setContentType(MediaType.APPLICATION_JSON);
                        res.getWriter().println("{}");
                    } catch (IOException e) {
                        fail();
                    }
                }
            }
        });

        if (withToken) {
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
        }
        AuthenticationData auth = new AuthenticationData();
        if (withAuth) {
            auth.setType(AuthApi.AuthType.PASSWORD);
            auth.setSession("sss");
        }
        assertNotNull(getMatrixClient().account().deactivate(auth).get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void availableTest() throws Exception {
        getServlet().setGet((req, res) -> {
            try {
                if ("my_cool_localpart".equals(req.getParameter("username"))) {
                    res.setContentType(MediaType.APPLICATION_JSON);
                    res.getWriter().println("{\"available\":true}");
                }
            } catch (Exception e) {
                fail();
            }
        });

        assertTrue(getMatrixClient().account().available("my_cool_localpart").get(1000, TimeUnit.MILLISECONDS));
    }
}
