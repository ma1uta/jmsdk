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

package io.github.ma1uta.matrix.client.methods.async;

import io.github.ma1uta.matrix.client.model.capability.CapabilitiesResponse;
import io.github.ma1uta.matrix.client.rest.async.CapabilitiesApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * Capability methods.
 */
public class CapabilityAsyncMethods {

    private final CapabilitiesApi capabilitiesApi;

    public CapabilityAsyncMethods(RestClientBuilder restClientBuilder) {
        this.capabilitiesApi = restClientBuilder.build(CapabilitiesApi.class);
    }

    /**
     * Gets information about the server's supported feature set and other relevant capabilities.
     *
     * @return server capabilities.
     */
    public CompletableFuture<CapabilitiesResponse> capabilities() {
        return capabilitiesApi.capabilities().toCompletableFuture();
    }
}
