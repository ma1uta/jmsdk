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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.Optional;

public class HomeServerResolverTest {

    @Test
    public void wellKnownTest() {
        HomeServerResolver resolver = new HomeServerResolver();

        Optional<ResolvedHomeserver> resolvedHomeserver = resolver.resolve("ru-matrix.org");
        assertTrue(resolvedHomeserver.isPresent());
        URL url = resolvedHomeserver.get().getUrl();
        assertEquals("https://ru-matrix.org:8448", url.toString());

        resolvedHomeserver = resolver.resolve("matrix.org");
        assertTrue(resolvedHomeserver.isPresent());
        url = resolvedHomeserver.get().getUrl();
        assertEquals("https://matrix-client.matrix.org", url.toString());

        resolvedHomeserver = resolver.resolve("sibnsk.net");
        assertTrue(resolvedHomeserver.isPresent());
        url = resolvedHomeserver.get().getUrl();
        assertEquals("https://matrix.sibnsk.net", url.toString());
    }
}
