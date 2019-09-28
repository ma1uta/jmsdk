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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.auth.LoginRequest;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.model.auth.LoginType;
import io.github.ma1uta.matrix.client.model.auth.SupportedLoginResponse;
import io.github.ma1uta.matrix.client.model.auth.UserIdentifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;

class AuthMethodsTest extends MockServer {

    @Test
    public void getLogin() throws Exception {
        wireMockServer.stubFor(
            get(urlPathMatching("/_matrix/client/r0/login/?"))
                .willReturn(okJson("{\n" +
                    "  \"flows\": [\n" +
                    "    {\n" +
                    "      \"type\": \"m.login.password\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}")
                )
        );

        List<LoginType> loginTypes = getMatrixClient().auth().loginTypes().thenApply(SupportedLoginResponse::getFlows)
            .get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(loginTypes);
        assertEquals(1, loginTypes.size());
        LoginType loginType = loginTypes.get(0);
        assertNotNull(loginType);
        assertEquals(AuthApi.AuthType.PASSWORD, loginType.getType());
    }

    @Test
    public void login() throws Exception {
        wireMockServer.stubFor(
            post(urlEqualTo("/_matrix/client/r0/login"))
                .withHeader("Content-Type", equalTo(MediaType.APPLICATION_JSON))
                .withRequestBody(equalToJson("{\"type\":\"" + AuthApi.AuthType.PASSWORD + "\"," +
                    "\"password\":\"ilovebananas\",\"initial_device_display_name\":\"Jungle Phone\"," +
                    "\"identifier\":{\"type\":\"m.id.user\",\"user\":\"cheeky_monkey\"}}"))
                .willReturn(okJson("{\n" +
                    "  \"user_id\": \"@cheeky_monkey:matrix.org\",\n" +
                    "  \"access_token\": \"abc123\",\n" +
                    "  \"device_id\": \"GHTYAJCE\"\n" +
                    "}")
                )
        );

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setType(AuthApi.AuthType.PASSWORD);
        loginRequest.setInitialDeviceDisplayName("Jungle Phone");
        loginRequest.setPassword("ilovebananas".toCharArray());
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
        assertThrows(ExecutionException.class, () -> logout(false));
    }

    public void logout(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            post(urlEqualTo("/_matrix/client/r0/logout"))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(okJson("{}"))
        );

        if (withToken) {
            getMatrixClient().getConnectionInfo().setAccessToken(ACCESS_TOKEN);
        }
        EmptyResponse res = getMatrixClient().auth().logout().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(res);
    }

    @Test
    public void logoutAll() throws Exception {
        logoutAll(true);
    }

    @Test
    public void logoutAllUnAuthorized() {
        assertThrows(ExecutionException.class, () -> logoutAll(false));
    }

    public void logoutAll(boolean withToken) throws Exception {
        wireMockServer.stubFor(
            post(urlEqualTo("/_matrix/client/r0/logout/all"))
                .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
                .willReturn(okJson("{}"))
        );
        wireMockServer.stubFor(
            post(urlEqualTo("/_matrix/client/r0/logout/all"))
                .willReturn(unauthorized().withBody("{}"))
                .atPriority(10)
        );

        if (withToken) {
            getMatrixClient().getConnectionInfo().setAccessToken(ACCESS_TOKEN);
        }
        EmptyResponse res = getMatrixClient().auth().logoutAll().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(res);
    }
}
