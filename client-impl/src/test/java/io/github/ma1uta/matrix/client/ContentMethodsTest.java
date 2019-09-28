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
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.ma1uta.matrix.client.model.content.ContentUri;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;

class ContentMethodsTest extends MockServer {

    @Test
    void upload() throws Exception {
        wireMockServer.stubFor(post(urlPathMatching("/_matrix/media/r0/upload"))
            .withHeader("Content-Type", equalTo(MediaType.TEXT_PLAIN))
            .withHeader("Authorization", equalTo("Bearer " + ACCESS_TOKEN))
            .withQueryParam("filename", equalTo("content.txt"))
            .willReturn(okJson("{\"content_uri\":\"mxc://example.com/AQwafuaFswefuhsfAFAgsw\"}")
            )
        );

        try (InputStream inputStream = getClass().getResourceAsStream("/content.txt")) {
            getMatrixClient().setAccessToken(ACCESS_TOKEN);
            String uri = getMatrixClient().content().upload(inputStream, "content.txt", "text/plain").thenApply(ContentUri::getContentUri)
                .get(1000, TimeUnit.MILLISECONDS);
            assertEquals("mxc://example.com/AQwafuaFswefuhsfAFAgsw", uri);
        }
    }
}
