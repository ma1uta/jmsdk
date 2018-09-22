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

    private BiConsumer<HttpServletRequest, HttpServletResponse> get;
    private BiConsumer<HttpServletRequest, HttpServletResponse> post;
    private BiConsumer<HttpServletRequest, HttpServletResponse> put;
    private BiConsumer<HttpServletRequest, HttpServletResponse> delete;

    public BiConsumer<HttpServletRequest, HttpServletResponse> getGet() {
        return get;
    }

    public void setGet(BiConsumer<HttpServletRequest, HttpServletResponse> get) {
        this.get = get;
    }

    public BiConsumer<HttpServletRequest, HttpServletResponse> getPost() {
        return post;
    }

    public void setPost(BiConsumer<HttpServletRequest, HttpServletResponse> post) {
        this.post = post;
    }

    public BiConsumer<HttpServletRequest, HttpServletResponse> getPut() {
        return put;
    }

    public void setPut(BiConsumer<HttpServletRequest, HttpServletResponse> put) {
        this.put = put;
    }

    public BiConsumer<HttpServletRequest, HttpServletResponse> getDelete() {
        return delete;
    }

    public void setDelete(BiConsumer<HttpServletRequest, HttpServletResponse> delete) {
        this.delete = delete;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (getGet() != null) {
            getGet().accept(req, resp);
        } else {
            super.doGet(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (getPost() != null) {
            getPost().accept(req, resp);
        } else {
            super.doPost(req, resp);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (getPut() != null) {
            getPut().accept(req, resp);
        } else {
            super.doPut(req, resp);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (getDelete() != null) {
            getDelete().accept(req, resp);
        } else {
            super.doDelete(req, resp);
        }
    }
}
