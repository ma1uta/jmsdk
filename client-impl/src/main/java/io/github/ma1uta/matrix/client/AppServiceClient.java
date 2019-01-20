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

package io.github.ma1uta.matrix.client;

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.UserId;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;

/**
 * Application Service Client.
 */
public class AppServiceClient extends MatrixClient {

    public AppServiceClient(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
        if (!(defaultParams.getUserId() instanceof UserId)) {
            throw new NullPointerException("The `user_id` should be specified.");
        }
    }

    /**
     * Return a new AppService client with the specified `user_id`.
     *
     * @param userId The user MXID.
     * @return The new AppService client.
     */
    public AppServiceClient userId(Id userId) {
        return new AppServiceClient(getRequestFactory(), getDefaultParams().clone().userId(userId));
    }

    @Override
    public LoginResponse afterLogin(LoginResponse loginResponse) {
        return loginResponse;
    }

    @Override
    public EmptyResponse afterLogout(EmptyResponse response) {
        return response;
    }

    /**
     * App service client builder.
     */
    public static class Builder extends AbstractClientBuilder<AppServiceClient> {

        @Override
        public AppServiceClient newInstance() {
            return new AppServiceClient(getFactory(), getDefaultParams());
        }
    }
}
