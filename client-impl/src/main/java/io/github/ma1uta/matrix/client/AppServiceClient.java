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
import io.github.ma1uta.matrix.client.filter.CustomQueryParamsClientFilter;
import io.github.ma1uta.matrix.client.methods.AuthMethods;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;

/**
 * Application Service Client.
 */
public class AppServiceClient extends MatrixClient {

    private final CustomQueryParamsClientFilter queryParamsClientFilter = new CustomQueryParamsClientFilter();

    public AppServiceClient(String domain, AccountInfo accountInfo) {
        super(domain, accountInfo);
        String userId = accountInfo.getUserId();
        Objects.requireNonNull(userId, "UserId must be configured.");
        queryParamsClientFilter.addParam("user_id", userId);
    }

    @Override
    protected RestClientBuilder newClientBuilder() {
        return super.newClientBuilder().register(queryParamsClientFilter);
    }

    /**
     * Return a new AppService client with the specified `user_id`.
     *
     * @param userId The user MXID.
     * @return The new AppService client.
     */
    public AppServiceClient userId(String userId) {
        AccountInfo newAccountInfo = new AccountInfo(getAccountInfo());
        newAccountInfo.setUserId(userId);
        return new AppServiceClient(getDomain(), newAccountInfo);
    }

    @Override
    public AuthMethods auth() {
        throw new UnsupportedOperationException(
            "Application service client must not login/logout due access_token setup via the configuration.");
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
            return new AppServiceClient(domain, accountInfo);
        }
    }
}
