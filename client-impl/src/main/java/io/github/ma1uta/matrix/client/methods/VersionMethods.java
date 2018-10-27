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

import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.api.VersionApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.version.VersionsResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Version methods.
 */
public class VersionMethods extends AbstractMethods {

    public VersionMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Gets the versions of the specification supported by the server.
     *
     * @return The versions supported by the server.
     */
    public CompletableFuture<List<String>> versions() {
        return factory().get(VersionApi.class, "versions", defaults(), VersionsResponse.class).thenApply(VersionsResponse::getVersions);
    }
}
