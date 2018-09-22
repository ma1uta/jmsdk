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

import io.github.ma1uta.matrix.client.MatrixClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class ClientToJettyServer {

    private ConfigurableServlet servlet;
    private MatrixClient matrixClient;
    private Server server;

    public ConfigurableServlet getServlet() {
        return servlet;
    }

    public MatrixClient getMatrixClient() {
        return matrixClient;
    }

    @BeforeEach
    void setUp() throws Exception {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0); // auto-bind to available port
        server.addConnector(connector);

        servlet = new ConfigurableServlet();
        ServletContextHandler context = new ServletContextHandler();
        ServletHolder aDefault = new ServletHolder("default", servlet);
        aDefault.setInitParameter("resourceBase", System.getProperty("user.dir"));
        aDefault.setInitParameter("dirAllowed", "true");
        context.addServlet(aDefault, "/");
        server.setHandler(context);

        server.start();

        String host = connector.getHost();
        if (host == null) {
            host = "localhost";
        }
        int port = connector.getLocalPort();

        matrixClient = new MatrixClient(String.format("http://%s:%d", host, port));
    }

    @AfterEach
    void tearDown() throws Exception {
        server.stop();
    }
}
