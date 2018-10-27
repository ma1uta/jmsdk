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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.ma1uta.matrix.client.test.ConfigurableServlet;
import io.github.ma1uta.matrix.client.test.MockServer;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.ws.rs.core.MediaType;

class ContentMethodsTest extends MockServer {

    @Test
    void upload() throws Exception {
        ConfigurableServlet.post = (req, res) -> {
            try {
                assertTrue(req.getRequestURI().startsWith("/_matrix/media/r0/upload"));
                assertEquals(MediaType.TEXT_PLAIN, req.getContentType());
                assertEquals("content.txt", req.getParameter("filename"));
                assertEquals("Sample.", req.getReader().lines().collect(Collectors.joining()));

                res.setContentType("application/json");
                res.getWriter().println("{\"content_uri\":\"mxc://example.com/AQwafuaFswefuhsfAFAgsw\"}");
            } catch (Exception e) {
                e.printStackTrace();
                fail();
            }
        };

        try (InputStream inputStream = getClass().getResourceAsStream("/content.txt")) {
            getMatrixClient().getDefaultParams().accessToken(ACCESS_TOKEN);
            String uri = getMatrixClient().content().upload(inputStream, "content.txt", MediaType.TEXT_PLAIN)
                .get(1000, TimeUnit.MILLISECONDS);
            assertEquals("mxc://example.com/AQwafuaFswefuhsfAFAgsw", uri);
        }
    }
}
