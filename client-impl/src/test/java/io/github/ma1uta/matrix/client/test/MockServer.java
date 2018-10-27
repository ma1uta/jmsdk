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

package io.github.ma1uta.matrix.client.test;

import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MockServer {

    public static final String ACCESS_TOKEN = "$ome_sEcreT_t0keN";

    public static final Pattern BEARER = Pattern.compile("Bearer (.*)");

    private MatrixClient matrixClient;
    private Undertow server;

    public MatrixClient getMatrixClient() {
        return matrixClient;
    }

    @BeforeEach
    void setUp() throws Exception {
        DeploymentInfo servletBuilder = Servlets.deployment()
            .setClassLoader(getClass().getClassLoader())
            .setContextPath("/")
            .setDeploymentName("test.war")
            .addServlets(
                Servlets.servlet("DefaultServlet", ConfigurableServlet.class).addMapping("/*")
            );

        DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
        manager.deploy();
        PathHandler path = Handlers.path(Handlers.redirect("/")).addPrefixPath("/", manager.start());

        server = Undertow.builder()
            .addHttpListener(8080, "localhost")
            .setHandler(path)
            .build();
        server.start();

        matrixClient = new MatrixClient(String.format("http://%s:%d", "localhost", 8080));
    }

    @AfterEach
    void tearDown() throws Exception {
        server.stop();
    }

    public boolean authenticated(HttpServletRequest req, HttpServletResponse res) {
        String accessToken = null;
        String authorization = req.getHeader("Authorization");
        if (authorization == null || authorization.trim().isEmpty()) {
            accessToken = req.getParameter("access_token");
        } else {
            Matcher matcher = BEARER.matcher(authorization);
            if (matcher.matches()) {
                accessToken = matcher.group(1);
            }
        }
        if (!ACCESS_TOKEN.equals(accessToken)) {
            try {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "{}");
            } catch (IOException e) {
                fail();
            }
            return false;
        }

        return true;
    }

    public JsonNode incomingJson(String content) {
        try {
            return new ObjectMapper().readValue(content, JsonNode.class);
        } catch (IOException e) {
            fail();
            return null;
        }
    }

    public JsonNode incomingJson(HttpServletRequest request) {
        try {
            return new ObjectMapper().readValue(request.getInputStream(), JsonNode.class);
        } catch (IOException e) {
            fail();
            return null;
        }
    }
}
