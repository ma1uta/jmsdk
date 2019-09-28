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

import org.junit.jupiter.api.Test;

import java.net.URL;

public class HomeServerResolverTest {

    @Test
    public void wllKnownTest() {
        HomeServerResolver resolver = new HomeServerResolver();

        URL url = resolver.resolve("ru-matrix.org");
        assertEquals("https://ru-matrix.org:8448", url.toString());

        url = resolver.resolve("matrix.org");
        assertEquals("https://matrix.org", url.toString());

        url = resolver.resolve("sibnsk.net");
        assertEquals("https://matrix.sibnsk.net", url.toString());
    }
}
