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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.methods.AccountMethods;
import io.github.ma1uta.matrix.client.methods.AuthMethods;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;

import java.util.concurrent.ExecutorService;

/**
 * Application Service Client.
 */
public class StandaloneClient extends MatrixClient {

    public StandaloneClient(String domain) {
        super(domain);
    }

    public StandaloneClient(ConnectionInfo connectionInfo) {
        this(connectionInfo, null);
    }

    public StandaloneClient(ConnectionInfo connectionInfo, ExecutorService executorService) {
        super(connectionInfo, executorService);
    }

    /**
     * Auth methods.
     *
     * @return auth methods.
     */
    public AuthMethods auth() {
        return getMethod(AuthMethods.class, () -> new AuthMethods(getClientBuilder(), this::afterLogin, this::afterLogout));
    }

    /**
     * Account methods.
     *
     * @return account methods.
     */
    @Override
    public AccountMethods account() {
        return getMethod(AccountMethods.class, () -> new AccountMethods(getClientBuilder(), this::afterLogin));
    }

    @Override
    public void close() {
        auth().logout();
    }

    /**
     * Action after login/register.
     *
     * @param loginResponse The login response.
     * @return The login response.
     */
    public LoginResponse afterLogin(LoginResponse loginResponse) {
        if (loginResponse == null) {
            afterLogout(null);
        } else {
            getConnectionInfo().setUserId(loginResponse.getUserId());
            getConnectionInfo().setAccessToken(loginResponse.getAccessToken());
            getConnectionInfo().setDeviceId(loginResponse.getDeviceId());
            getConnectionInfo().setServerInfo(loginResponse.getWellKnown());
        }
        return loginResponse;
    }

    /**
     * Action after logout/logoutAll.
     *
     * @param response The empty response.
     * @return The login response.
     */
    public EmptyResponse afterLogout(EmptyResponse response) {
        getConnectionInfo().setAccessToken(null);
        getConnectionInfo().setDeviceId(null);
        return response;
    }

    /**
     * App service client builder.
     */
    public static class Builder extends AbstractClientBuilder<StandaloneClient> {

        @Override
        public StandaloneClient newInstance() {
            return new StandaloneClient(connectionInfo);
        }
    }
}
