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
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.unauthorized;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.ma1uta.matrix.client.model.voip.VoipResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletionException;
import javax.ws.rs.core.MediaType;

class VoipMethodsTest extends MockServer {

    @Test
    public void secured() {
        wireMockServer.stubFor(get(urlMatching("/_matrix/client/r0/voip/turnServer"))
            .atPriority(10)
            .willReturn(unauthorized().withHeader("Content-Type", MediaType.APPLICATION_JSON).withBody("{}"))
        );

        assertThrows(CompletionException.class, () -> getMatrixClient().turnServers().turnServers().join());
    }

    @Test
    public void voip() {
        wireMockServer.stubFor(get(urlMatching("/_matrix/client/r0/voip/turnServer"))
            .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
            .willReturn(okJson("{\n" +
                "  \"username\": \"1443779631:@user:example.com\",\n" +
                "  \"password\": \"JlKfBy1QwLrO20385QyAtEyIv0=\",\n" +
                "  \"uris\": [\n" +
                "    \"turn:turn.example.com:3478?transport=udp\",\n" +
                "    \"turn:10.20.30.40:3478?transport=tcp\",\n" +
                "    \"turns:10.20.30.40:443?transport=tcp\"\n" +
                "  ],\n" +
                "  \"ttl\": 86400\n" +
                "}")
            )
        );
        getMatrixClient().getConnectionInfo().setAccessToken(ACCESS_TOKEN);
        VoipResponse voip = getMatrixClient().turnServers().turnServers().join();
        assertNotNull(voip);
        assertEquals("1443779631:@user:example.com", voip.getUsername());
        assertEquals("JlKfBy1QwLrO20385QyAtEyIv0=", new String(voip.getPassword()));
        assertEquals(Long.valueOf(86400L), voip.getTtl());

        List<String> uris = voip.getUris();
        assertNotNull(uris);
        assertEquals(3, uris.size());
        assertEquals("turn:turn.example.com:3478?transport=udp", uris.get(0));
        assertEquals("turn:10.20.30.40:3478?transport=tcp", uris.get(1));
        assertEquals("turns:10.20.30.40:443?transport=tcp", uris.get(2));
    }
}
