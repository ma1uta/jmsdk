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

import io.github.ma1uta.matrix.client.api.AuthApi;
import io.github.ma1uta.matrix.client.model.auth.LoginType;
import io.github.ma1uta.matrix.client.test.ClientToJettyServer;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.MediaType;

class AuthMethodsTest extends ClientToJettyServer {

    @Test
    public void getLogin() throws Exception {
        getServlet().setGet((req, res) -> {
            try {
                assertTrue(req.getRequestURI().startsWith("/_matrix/client/r0/login"));

                res.setContentType(MediaType.APPLICATION_JSON);
                res.getWriter().println("{\n" +
                    "  \"flows\": [\n" +
                    "    {\n" +
                    "      \"type\": \"m.login.password\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        List<LoginType> loginTypes = getMatrixClient().auth().loginTypes().get(1000, TimeUnit.MILLISECONDS);
        assertNotNull(loginTypes);
        assertEquals(1, loginTypes.size());
        LoginType loginType = loginTypes.get(0);
        assertNotNull(loginType);
        assertEquals(AuthApi.AuthType.PASSWORD, loginType.getType());
    }
}
