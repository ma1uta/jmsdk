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

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

import io.github.ma1uta.matrix.client.filter.QueryParamsClientFilter;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;

/**
 * Application Service Client.
 */
public class AppServiceClient extends MatrixClient {

    private final QueryParamsClientFilter queryParamsClientFilter = new QueryParamsClientFilter();

    public AppServiceClient(String domain, AccountInfo accountInfo) {
        super(domain, accountInfo);
        String userId = accountInfo.getUserId();
        Objects.requireNonNull(userId, "UserId must be configured.");
        queryParamsClientFilter.addParam("user_id", userId);

        String accessToken = accountInfo.getAccessToken();
        Objects.requireNonNull(accessToken, "AccessToken must be configured");
        getHeaderClientFilter().addHeader(AUTHORIZATION, "Bearer " + accessToken);
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
