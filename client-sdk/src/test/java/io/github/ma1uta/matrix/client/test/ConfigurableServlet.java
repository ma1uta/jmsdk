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

import java.io.IOException;
import java.util.function.BiConsumer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ConfigurableServlet extends HttpServlet {

    public static BiConsumer<HttpServletRequest, HttpServletResponse> get;
    public static BiConsumer<HttpServletRequest, HttpServletResponse> post;
    public static BiConsumer<HttpServletRequest, HttpServletResponse> put;
    public static BiConsumer<HttpServletRequest, HttpServletResponse> delete;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (get != null) {
            get.accept(req, resp);
        } else {
            super.doGet(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (post != null) {
            post.accept(req, resp);
        } else {
            super.doPost(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (put != null) {
            put.accept(req, resp);
        } else {
            super.doPut(req, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (delete != null) {
            delete.accept(req, resp);
        } else {
            super.doDelete(req, resp);
        }
    }
}
