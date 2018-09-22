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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.ma1uta.matrix.client.test.ClientToJettyServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import javax.ws.rs.core.MediaType;

class VersionMethodsTest extends ClientToJettyServer {

    @Test
    void versions() {
        getServlet().setGet((req, res) -> {
            assertTrue(req.getRequestURI().startsWith("/_matrix/client/versions"));
            try {
                res.setContentType(MediaType.APPLICATION_JSON);
                PrintWriter writer = res.getWriter();
                writer.println("{\"versions\":[\"r0.4.0\",\"r0.3.0\"]}");
            } catch (IOException e) {
                fail();
            }
        });

        String[] versions = getMatrixClient().versions().versions().join();
        assertNotNull(versions);
        assertEquals(2, versions.length);
        assertEquals("r0.4.0", versions[0]);
        assertEquals("r0.3.0", versions[1]);
    }
}
