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

import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicLong;
import javax.ws.rs.client.Client;

/**
 * Matrix client.
 */
public class MatrixClient implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatrixClient.class);

    private final AtomicLong txn;

    private final boolean updateAccessToken;

    private final RequestMethods requestMethods;

    public MatrixClient(String homeserverUrl, Client client, boolean addUserIdToRequests, boolean updateAccessToken, Long txnId) {
        this.updateAccessToken = updateAccessToken;
        this.txn = new AtomicLong(txnId != null ? txnId : 0);
        this.requestMethods = new RequestMethods(client, homeserverUrl, addUserIdToRequests);
    }

    public String getUserId() {
        return getRequestMethods().getUserId();
    }

    /**
     * Setup user id.
     *
     * @param userId user mxid.
     */
    public void setUserId(String userId) {
        getRequestMethods().setUserId(userId);
    }

    public boolean isUpdateAccessToken() {
        return updateAccessToken;
    }

    public AtomicLong getTxn() {
        return txn;
    }

    public RequestMethods getRequestMethods() {
        return requestMethods;
    }

    /**
     * Return access token.
     *
     * @return access token.
     */
    public String getAccessToken() {
        return getRequestMethods().getAccessToken();
    }

    /**
     * Setup access token.
     *
     * @param accessToken a new access token.
     */
    public void setAccessToken(String accessToken) {
        getRequestMethods().setAccessToken(accessToken);
    }

    protected void updateCredentials(LoginResponse registered) {
        if (isUpdateAccessToken()) {
            getRequestMethods().setAccessToken(registered.getAccessToken());
        }
        getRequestMethods().setUserId(registered.getUserId());
    }


    @Override
    public void close() {
        auth().logout();
    }

    /**
     * Auth methods.
     *
     * @return auth methods.
     */
    public AuthMethods auth() {
        return new AuthMethods(this);
    }

    /**
     * Account methods.
     *
     * @return account methods.
     */
    public AccountMethods account() {
        return new AccountMethods(this);
    }

    /**
     * Profile methods.
     *
     * @return profile methods.
     */
    public ProfileMethods profile() {
        return new ProfileMethods(this);
    }

    /**
     * Receipt method.
     *
     * @return receipt method.
     */
    public ReceiptMethods receipt() {
        return new ReceiptMethods(this);
    }

    /**
     * Sync method.
     *
     * @return sync method.
     */
    public SyncMethods sync() {
        return new SyncMethods(this);
    }

    /**
     * Event methods.
     *
     * @return event methods.
     */
    public EventMethods event() {
        return new EventMethods(this);
    }

    /**
     * FilterMethods methods.
     *
     * @return filter methods.
     */
    public FilterMethods filter() {
        return new FilterMethods(this);
    }

    /**
     * RoomMethods apis.
     *
     * @return room apis.
     */
    public RoomMethods room() {
        return new RoomMethods(this);
    }
}
