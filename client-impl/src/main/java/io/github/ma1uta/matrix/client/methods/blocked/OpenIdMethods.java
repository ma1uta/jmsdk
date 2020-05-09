/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

package io.github.ma1uta.matrix.client.methods.blocked;

import io.github.ma1uta.matrix.client.ConnectionInfo;
import io.github.ma1uta.matrix.client.model.openid.OpenIdResponse;
import io.github.ma1uta.matrix.client.rest.blocked.OpenIdApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

/**
 * OpenID methods.
 */
public class OpenIdMethods {

    private final OpenIdApi openIdApi;
    private final ConnectionInfo connectionInfo;

    public OpenIdMethods(RestClientBuilder restClientBuilder, ConnectionInfo connectionInfo) {
        this.openIdApi = restClientBuilder.build(OpenIdApi.class);
        this.connectionInfo = connectionInfo;
    }

    /**
     * Gets an OpenID token object that the requester may supply to another service to verify their identity in Matrix.
     * The generated token is only valid for exchanging for user information from the federation API for OpenID.
     *
     * @return OpenID token.
     */
    public OpenIdResponse requestToken() {
        return openIdApi.requestToken(connectionInfo.getUserId());
    }
}
