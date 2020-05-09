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

package io.github.ma1uta.matrix.client;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.github.ma1uta.matrix.client.model.version.VersionsResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

class VersionAsyncMethodsTest extends MockServer {

    @Test
    void versions() {
        wireMockServer.stubFor(get(urlMatching("/_matrix/client/versions/?"))
            .willReturn(okJson("{\"versions\":[\"r0.4.0\",\"r0.3.0\"]}")
            )
        );

        List<String> versions = getMatrixClient().versionsAsync().versions().thenApply(VersionsResponse::getVersions).join();
        assertNotNull(versions);
        assertEquals(2, versions.size());
        assertEquals("r0.4.0", versions.get(0));
        assertEquals("r0.3.0", versions.get(1));
    }
}
