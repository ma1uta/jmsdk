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

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.account.AuthenticationData;
import io.github.ma1uta.matrix.client.model.account.AvailableResponse;
import io.github.ma1uta.matrix.client.model.account.DeactivateResponse;
import io.github.ma1uta.matrix.client.model.account.EmailRequestToken;
import io.github.ma1uta.matrix.client.model.account.MsisdnRequestToken;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.account.ThirdPartyIdentifier;
import io.github.ma1uta.matrix.client.model.account.ThreePidCred;
import io.github.ma1uta.matrix.client.model.account.ThreePidRequest;
import io.github.ma1uta.matrix.client.model.account.ThreePidResponse;
import io.github.ma1uta.matrix.client.model.account.WhoamiResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.thirdpid.SessionResponse;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class AccountMethodsTest extends MockServer {

    @Test
    public void registerAndLogin() throws Exception {
        LoginResponse loginResponse = register(false, true);

        assertEquals("abc123", getMatrixClient().getAccessToken());
        assertEquals("@cheeky_monkey:matrix.org", getMatrixClient().getUserId());
        assertEquals("abc123", loginResponse.getAccessToken());
        assertEquals("GHTYAJCE", loginResponse.getDeviceId());
    }

    @Test
    public void onlyRegister() throws Exception {
        LoginResponse loginResponse = register(true, true);

        assertNull(getMatrixClient().getAccessToken());
        assertEquals("@cheeky_monkey:matrix.org", getMatrixClient().getUserId());
        assertNull(loginResponse.getDeviceId());
        assertNull(loginResponse.getAccessToken());
    }

    @Test
    public void tryToRegister() {
        assertThrows(ExecutionException.class, () -> register(false, false));
    }

    public LoginResponse register(boolean inhibitLogin, boolean withAuth) throws Exception {
        if (withAuth) {
            wireMockServer.stubFor(
                post(urlPathMatching("/_matrix/client/r0/register/?"))
                    .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                    .withRequestBody(
                        equalToJson("{\"username\":\"cheeky_monkey\",\"device_id\":\"GHTYAJCE\",\"password\":\"ilovebananas\"," +
                            "\"initial_device_display_name\":\"Jungle Phone\",\"bind_email\":false,\"inhibit_login\":" + inhibitLogin + "," +
                            "\"auth\":{\"type\":\"m.login.password\",\"session\":\"xxxxx\"}}"))
                    .willReturn(okJson("{\n" +
                        "  \"user_id\": \"@cheeky_monkey:matrix.org\"" + (inhibitLogin ? "" : ",\n" +
                        "  \"access_token\": \"abc123\",\n" +
                        "  \"device_id\": \"GHTYAJCE\"\n") +
                        "}")
                    )
            );
        } else {
            wireMockServer.stubFor(
                post(urlPathMatching("/_matrix/client/r0/register/?"))
                    .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                    .withRequestBody(
                        equalToJson("{\"username\":\"cheeky_monkey\",\"device_id\":\"GHTYAJCE\",\"password\":\"ilovebananas\"," +
                            "\"initial_device_display_name\":\"Jungle Phone\",\"bind_email\":false,\"inhibit_login\":" + inhibitLogin + "}}"))
                    .willReturn(aResponse()
                        .withStatus(Response.Status.UNAUTHORIZED.getStatusCode())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                        .withBody("{}")
                    )
            );

        }

        RegisterRequest request = new RegisterRequest();
        request.setUsername("cheeky_monkey");
        request.setDeviceId("GHTYAJCE");
        request.setPassword("ilovebananas".toCharArray());
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
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/register/email/requestToken/?"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\"client_secret\":\"monkeys_are_GREAT\",\"email\":\"alice@example.org\"," +
                    "\"send_attempt\":1,\"next_link\":\"https://example.org/congratulations.html\",\"id_server\":\"id.example.com\"}"))
                .willReturn(okJson("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}")
                )
        );

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
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/register/msisdn/requestToken/?"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\n" +
                    "  \"client_secret\": \"monkeys_are_GREAT\",\n" +
                    "  \"country\": \"GB\",\n" +
                    "  \"phone_number\": \"07700900001\",\n" +
                    "  \"send_attempt\": 1,\n" +
                    "  \"next_link\": \"https://example.org/congratulations.html\",\n" +
                    "  \"id_server\": \"id.example.com\"\n" +
                    "}\n"))
                .willReturn(okJson("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}")
                )
        );

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
        assertThrows(ExecutionException.class, () -> password(false, false));
    }

    @Test
    public void unauthincatedTryToPassword() {
        assertThrows(ExecutionException.class, () -> password(true, false));
    }

    public void password(boolean withToken, boolean withAuth) throws Exception {
        if (withAuth) {
            wireMockServer.stubFor(
                post(urlPathMatching("/_matrix/client/r0/account/password/?"))
                    .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                    .withRequestBody(equalToJson("{\n" +
                        "  \"new_password\": \"ihatebananas\",\n" +
                        "  \"auth\": {\n" +
                        "    \"type\": \"m.login.password\",\n" +
                        "    \"session\": \"xxxxx\"\n" +
                        "  }\n" +
                        "}"))
                    .willReturn(okJson("{}"))
            );
        } else {
            wireMockServer.stubFor(
                post(urlPathMatching("/_matrix/client/r0/account/password/?"))
                    .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                    .willReturn(unauthorized().withHeader("Content-Type", MediaType.APPLICATION_JSON).withBody("{}"))
            );
        }

        if (withToken) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        AuthenticationData auth = new AuthenticationData();
        if (withAuth) {
            auth.setType(AuthApi.AuthType.PASSWORD);
            auth.setSession("xxxxx");
        }
        assertNotNull(getMatrixClient().account().password("ihatebananas".toCharArray(), auth).get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void passwordEmailRequestToken() throws Exception {
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/password/email/requestToken/?"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\n" +
                    "  \"client_secret\": \"monkeys_are_GREAT\",\n" +
                    "  \"email\": \"alice@example.org\",\n" +
                    "  \"send_attempt\": 1,\n" +
                    "  \"next_link\": \"https://example.org/congratulations.html\",\n" +
                    "  \"id_server\": \"id.example.com\"\n" +
                    "}"))
                .willReturn(okJson("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}\n"))
        );

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
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/password/msisdn/requestToken/?"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\n" +
                    "  \"client_secret\": \"monkeys_are_GREAT\",\n" +
                    "  \"country\": \"GB\",\n" +
                    "  \"phone_number\": \"07700900001\",\n" +
                    "  \"send_attempt\": 1,\n" +
                    "  \"next_link\": \"https://example.org/congratulations.html\",\n" +
                    "  \"id_server\": \"id.example.com\"\n" +
                    "}\n"))
                .willReturn(okJson("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}"))
        );

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
        assertThrows(ExecutionException.class, () -> deactivate(false, true));
    }

    @Test
    public void tryToDeactivateWithouAuth() {
        assertThrows(ExecutionException.class, () -> deactivate(true, false));
    }

    public void deactivate(boolean withToken, boolean withAuth) throws Exception {
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/deactivate/?"))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\n" +
                    "  \"auth\": {\n" +
                    "    \"type\": \"m.login.password\",\n" +
                    "    \"session\": \"sss\"\n" +
                    "  }\n" +
                    "}"))
                .willReturn(okJson("{}"))
        );
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/deactivate/?"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\"auth\":{}}"))
                .willReturn(unauthorized()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody("{\n" +
                        "  \"flows\": [\n" +
                        "    {\n" +
                        "      \"stages\": [\n" +
                        "        \"m.login.password\"\n" +
                        "      ]\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"))
        );

        if (withToken) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
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
        wireMockServer.stubFor(
            get(urlPathMatching("/_matrix/client/r0/register/available/?"))
                .withQueryParam("username", equalTo("my_cool_localpart"))
                .willReturn(
                    okJson("{\"available\":true}")
                )
        );

        assertTrue(getMatrixClient().account().available("my_cool_localpart").thenApply(AvailableResponse::getAvailable)
            .get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void get3pid() throws Exception {
        get3pid(true);
    }

    @Test
    public void get3pidUnauthorized() {
        assertThrows(ExecutionException.class, () -> get3pid(false));
    }

    public void get3pid(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            get(urlPathMatching("/_matrix/client/r0/account/3pid/?"))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(
                    okJson("{\n" +
                        "  \"threepids\": [\n" +
                        "    {\n" +
                        "      \"medium\": \"email\",\n" +
                        "      \"address\": \"monkey@banana.island\",\n" +
                        "      \"validated_at\": 1535176800000,\n" +
                        "      \"added_at\": 1535336848756\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}")
                )
        );
        wireMockServer.stubFor(
            get(urlPathMatching("/_matrix/client/r0/account/3pid/?"))
                .atPriority(10)
                .willReturn(unauthorized()
                    .withHeader("Content-Type", MediaType.APPLICATION_JSON)
                    .withBody("{}")
                )
        );

        if (withToken) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        ThreePidResponse threePidResponse = getMatrixClient().account().getThreePid().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(threePidResponse);
        assertEquals(1, threePidResponse.getThreepids().size());

        ThirdPartyIdentifier threepid = threePidResponse.getThreepids().get(0);
        assertNotNull(threepid);
        assertEquals("email", threepid.getMedium());
        assertEquals("monkey@banana.island", threepid.getAddress());
        assertEquals(1535176800000L, threepid.getValidatedAt().longValue());
        assertEquals(1535336848756L, threepid.getAddedAt().longValue());
    }

    @Test
    public void update3pid() throws Exception {
        update3pid(true);
    }

    @Test
    public void update3pidUnaithorized() {
        assertThrows(ExecutionException.class, () -> update3pid(false));
    }

    public void update3pid(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/3pid"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .withRequestBody(equalToJson("{\n" +
                    "  \"three_pid_creds\": {\n" +
                    "    \"id_server\": \"matrix.org\",\n" +
                    "    \"sid\": \"abc123987\",\n" +
                    "    \"client_secret\": \"d0n'tT3ll\"\n" +
                    "  },\n" +
                    "  \"bind\": false\n" +
                    "}"))
                .willReturn(okJson("{}"))
        );

        if (withToken) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        ThreePidRequest request = new ThreePidRequest();
        request.setBind(false);
        ThreePidCred cred = new ThreePidCred();
        cred.setIdServer("matrix.org");
        cred.setSid("abc123987");
        cred.setClientSecret("d0n'tT3ll");
        request.setThreePidCreds(cred);
        assertNotNull(getMatrixClient().account().updateThreePid(request).get(1000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void delete3pid() throws Exception {
        delete3pid(true);
    }

    @Test
    public void delete3pidUnaithorized() {
        assertThrows(ExecutionException.class, () -> delete3pid(false));
    }

    public void delete3pid(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/3pid/delete"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .withRequestBody(equalToJson("{\n" +
                    "  \"medium\": \"email\",\n" +
                    "  \"address\": \"example@domain.com\"\n" +
                    "}"))
                .willReturn(okJson("{}"))
        );
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/3pid/delete"))
                .atPriority(10)
                .willReturn(unauthorized().withBody("{}"))
        );

        if (withToken) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        DeactivateResponse res = getMatrixClient().account().deleteThreePid("email", "example@domain.com").get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(res);
    }

    @Test
    public void email3PidRequestToken() throws Exception {
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/3pid/email/requestToken"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\n" +
                    "  \"client_secret\": \"monkeys_are_GREAT\",\n" +
                    "  \"email\": \"alice@example.org\",\n" +
                    "  \"send_attempt\": 1,\n" +
                    "  \"next_link\": \"https://example.org/congratulations.html\",\n" +
                    "  \"id_server\": \"id.example.com\"\n" +
                    "}"))
                .willReturn(okJson("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}\n"))
        );

        EmailRequestToken request = new EmailRequestToken();
        request.setClientSecret("monkeys_are_GREAT");
        request.setEmail("alice@example.org");
        request.setSendAttempt(1L);
        request.setNextLink("https://example.org/congratulations.html");
        request.setIdServer("id.example.com");
        SessionResponse sessionResponse = getMatrixClient().account().threePidEmailRequestToken(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(sessionResponse);
        assertEquals("123abc", sessionResponse.getSid());
    }

    @Test
    public void phone3PidRequestToken() throws Exception {
        wireMockServer.stubFor(
            post(urlPathMatching("/_matrix/client/r0/account/3pid/msisdn/requestToken"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\n" +
                    "  \"client_secret\": \"monkeys_are_GREAT\",\n" +
                    "  \"country\": \"GB\",\n" +
                    "  \"phone_number\": \"07700900001\",\n" +
                    "  \"send_attempt\": 1,\n" +
                    "  \"next_link\": \"https://example.org/congratulations.html\",\n" +
                    "  \"id_server\": \"id.example.com\"\n" +
                    "}"))
                .willReturn(okJson("{\n" +
                    "  \"sid\": \"123abc\"\n" +
                    "}\n"))
        );

        MsisdnRequestToken request = new MsisdnRequestToken();
        request.setClientSecret("monkeys_are_GREAT");
        request.setCountry("GB");
        request.setPhoneNumber("07700900001");
        request.setSendAttempt(1L);
        request.setNextLink("https://example.org/congratulations.html");
        request.setIdServer("id.example.com");
        SessionResponse sessionResponse = getMatrixClient().account().threePidMsisdnRequestToken(request).get(1000, TimeUnit.MILLISECONDS);

        assertNotNull(sessionResponse);
        assertEquals("123abc", sessionResponse.getSid());
    }

    @Test
    public void whoami() throws Exception {
        whoami(true);
    }

    @Test
    public void whoamiUnauthenticated() {
        assertThrows(ExecutionException.class, () -> whoami(false));
    }

    public void whoami(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            get(urlPathMatching("/_matrix/client/r0/account/whoami"))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(okJson("{\n" +
                    "  \"user_id\": \"@joe:example.org\"\n" +
                    "}\n"))
        );
        wireMockServer.stubFor(
            get(urlPathMatching("/_matrix/client/r0/account/whoami"))
                .atPriority(10)
                .willReturn(unauthorized().withBody("{}"))
        );

        if (withToken) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
        }
        WhoamiResponse whoamiResponse = getMatrixClient().account().whoami().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(whoamiResponse);
        assertEquals("@joe:example.org", whoamiResponse.getUserId());
    }
}
