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

import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.api.VersionApi;
import io.github.ma1uta.matrix.client.model.version.VersionsResponse;

/**
 * Version methods.
 */
public class VersionMethods {

    private final MatrixClient matrixClient;

    public VersionMethods(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    protected MatrixClient getMatrixClient() {
        return matrixClient;
    }

    /**
     * Gets the versions of the specification supported by the server.
     *
     * @return The versions supported by the server.
     */
    public String[] versions() {
        return getMatrixClient().getRequestMethods().get(VersionApi.class, "versions", new RequestParams(), VersionsResponse.class)
            .getVersions();
    }
}
