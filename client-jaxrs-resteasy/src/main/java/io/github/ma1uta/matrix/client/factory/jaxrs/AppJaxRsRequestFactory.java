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

package io.github.ma1uta.matrix.client.factory.jaxrs;

import io.github.ma1uta.matrix.client.RequestParams;

import java.util.concurrent.Executor;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Factory to invoke API.
 * <br>
 * Used by the {@link io.github.ma1uta.matrix.client.AppServiceClient}.
 * <br>
 * The only difference from the {@link JaxRsRequestFactory} is adding the `user_id` query params to the request.
 */
public class AppJaxRsRequestFactory extends JaxRsRequestFactory {

    public AppJaxRsRequestFactory(Client client, String homeserverUrl) {
        super(client, homeserverUrl);
    }

    public AppJaxRsRequestFactory(Client client, String homeserverUrl, Executor executor) {
        super(client, homeserverUrl, executor);
    }

    @Override
    protected WebTarget applyQueryParams(RequestParams params, WebTarget path) {
        if (params.getUserId() == null || params.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("The `user_id` should be specified.");
        }
        return super.applyQueryParams(params, path).queryParam("user_id", encode(params.getUserId().trim()));
    }
}


