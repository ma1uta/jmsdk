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
import io.github.ma1uta.matrix.client.api.VoipApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.voip.VoipResponse;

import java.util.concurrent.CompletableFuture;

/**
 * Voip methods.
 */
public class VoipMethods extends AbstractMethods {

    public VoipMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * This API provides credentials for the client to use when initiating calls.
     *
     * @return The TURN server credentials.
     */
    public CompletableFuture<VoipResponse> turnServers() {
        return factory().get(VoipApi.class, "turnServer", defaults(), VoipResponse.class);
    }
}
