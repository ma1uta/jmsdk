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

import io.github.ma1uta.matrix.client.methods.AccountMethods;
import io.github.ma1uta.matrix.client.methods.AdminMethods;
import io.github.ma1uta.matrix.client.methods.AuthMethods;
import io.github.ma1uta.matrix.client.methods.ClientConfigMethods;
import io.github.ma1uta.matrix.client.methods.ContentMethods;
import io.github.ma1uta.matrix.client.methods.DeviceMethods;
import io.github.ma1uta.matrix.client.methods.EncryptionMethods;
import io.github.ma1uta.matrix.client.methods.EventContextMethods;
import io.github.ma1uta.matrix.client.methods.EventMethods;
import io.github.ma1uta.matrix.client.methods.FilterMethods;
import io.github.ma1uta.matrix.client.methods.PresenceMethods;
import io.github.ma1uta.matrix.client.methods.ProfileMethods;
import io.github.ma1uta.matrix.client.methods.PushMethods;
import io.github.ma1uta.matrix.client.methods.ReceiptMethods;
import io.github.ma1uta.matrix.client.methods.ReportMethods;
import io.github.ma1uta.matrix.client.methods.RequestMethods;
import io.github.ma1uta.matrix.client.methods.RoomMethods;
import io.github.ma1uta.matrix.client.methods.SearchMethods;
import io.github.ma1uta.matrix.client.methods.SendToDeviceMethods;
import io.github.ma1uta.matrix.client.methods.SyncMethods;
import io.github.ma1uta.matrix.client.methods.TagMethods;
import io.github.ma1uta.matrix.client.methods.TypingMethods;
import io.github.ma1uta.matrix.client.methods.UserDirectoryMethods;
import io.github.ma1uta.matrix.client.methods.VersionMethods;
import io.github.ma1uta.matrix.client.methods.VoipMethods;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import javax.ws.rs.client.Client;

/**
 * Matrix client.
 */
public class MatrixClient implements Closeable {

    private static final Logger LOGGER = LoggerFactory.getLogger(MatrixClient.class);

    private final boolean updateAccessToken;

    private final RequestMethods requestMethods;

    public MatrixClient(String homeserverUrl, Client client, boolean addUserIdToRequests, boolean updateAccessToken) {
        this.updateAccessToken = updateAccessToken;
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

    /**
     * Update the user ID and the access token.
     *
     * @param credentials user ID and the access token.
     */
    public void updateCredentials(LoginResponse credentials) {
        if (isUpdateAccessToken()) {
            getRequestMethods().setAccessToken(credentials.getAccessToken());
        }
        getRequestMethods().setUserId(credentials.getUserId());
    }


    @Override
    public void close() {
        auth().logout();
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
     * Admin methods.
     *
     * @return admin methods.
     */
    public AdminMethods admin() {
        return new AdminMethods(this);
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
     * Client config methods.
     *
     * @return client config methods.
     */
    public ClientConfigMethods clientConfig() {
        return new ClientConfigMethods(this);
    }

    /**
     * Content methods.
     *
     * @return content methods.
     */
    public ContentMethods content() {
        return new ContentMethods(this);
    }

    /**
     * Device methods.
     *
     * @return device methods.
     */
    public DeviceMethods device() {
        return new DeviceMethods(this);
    }

    /**
     * Encryption methods.
     *
     * @return encryption methods.
     */
    public EncryptionMethods encryption() {
        return new EncryptionMethods(this);
    }

    /**
     * Event context method.
     *
     * @return event context method.
     */
    public EventContextMethods eventContext() {
        return new EventContextMethods(this);
    }

    /**
     * Presence methods.
     *
     * @return presence methods.
     */
    public PresenceMethods presence() {
        return new PresenceMethods(this);
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
     * The report method.
     *
     * @return the report method.
     */
    public ReportMethods report() {
        return new ReportMethods(this);
    }

    /**
     * The search method.
     *
     * @return the search method.
     */
    public SearchMethods search() {
        return new SearchMethods(this);
    }

    /**
     * The send to device method.
     *
     * @return the send to device method.
     */
    public SendToDeviceMethods sendToDevice() {
        return new SendToDeviceMethods(this);
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

    /**
     * TagInfo methods.
     *
     * @return tag methods.
     */
    public TagMethods tag() {
        return new TagMethods(this);
    }

    /**
     * Typing methods.
     *
     * @return typing methods.
     */
    public TypingMethods typing() {
        return new TypingMethods(this);
    }

    /**
     * User directory method.
     *
     * @return user directory method.
     */
    public UserDirectoryMethods userDirectory() {
        return new UserDirectoryMethods(this);
    }

    /**
     * The versions method.
     *
     * @return the versions method.
     */
    public VersionMethods versions() {
        return new VersionMethods(this);
    }

    /**
     * The voip methods.
     *
     * @return the voip methods.
     */
    public VoipMethods turnServers() {
        return new VoipMethods(this);
    }

    /**
     * The push methods.
     *
     * @return the push methods.
     */
    public PushMethods push() {
        return new PushMethods(this);
    }
}
