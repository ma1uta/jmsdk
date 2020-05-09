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

package io.github.ma1uta.matrix.client;

import io.github.ma1uta.matrix.client.filter.UserIdClientFilter;
import io.github.ma1uta.matrix.client.methods.async.AccountAsyncMethods;
import io.github.ma1uta.matrix.client.methods.blocked.AccountMethods;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Application Service Client.
 */
public class AppServiceClient extends MatrixClient {

    private final UserIdClientFilter userIdClientFilter;

    public AppServiceClient(String domain) {
        this(new ConnectionInfo(domain));
    }

    public AppServiceClient(ConnectionInfo connectionInfo) {
        this(connectionInfo, null);
    }

    public AppServiceClient(ConnectionInfo connectionInfo, ExecutorService executorService) {
        super(connectionInfo, executorService);
        String userId = connectionInfo.getUserId();
        Objects.requireNonNull(userId, "UserId must be configured.");
        String accessToken = connectionInfo.getAccessToken();
        Objects.requireNonNull(accessToken, "AccessToken must be configured");

        this.userIdClientFilter = new UserIdClientFilter(connectionInfo);
    }

    @Override
    protected RestClientBuilder newClientBuilder() {
        return super.newClientBuilder().register(userIdClientFilter);
    }

    /**
     * Return a new AppService client with the specified `user_id`.
     *
     * @param userId The user MXID.
     * @return The new AppService client.
     */
    public AppServiceClient userId(String userId) {
        ConnectionInfo newConnectionInfo = new ConnectionInfo(getConnectionInfo());
        newConnectionInfo.setUserId(userId);
        return new AppServiceClient(newConnectionInfo, getExecutorService());
    }

    /**
     * Account methods.
     *
     * @return account methods.
     */
    @Override
    public AccountAsyncMethods accountAsync() {
        return getMethod(AccountAsyncMethods.class, () -> new AccountAsyncMethods(getClientBuilder(), Function.identity()));
    }

    /**
     * Account methods.
     *
     * @return account methods.
     */
    @Override
    public AccountMethods account() {
        return getMethod(AccountMethods.class, () -> new AccountMethods(getClientBuilder(), Function.identity()));
    }

    /**
     * App service client builder.
     */
    public static class Builder extends AbstractClientBuilder<AppServiceClient> {

        @Override
        public AppServiceClient newInstance() {
            return new AppServiceClient(connectionInfo);
        }
    }
}
