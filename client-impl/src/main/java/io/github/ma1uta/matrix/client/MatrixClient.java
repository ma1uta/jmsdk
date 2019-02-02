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
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.methods.AccountMethods;
import io.github.ma1uta.matrix.client.methods.AdminMethods;
import io.github.ma1uta.matrix.client.methods.AuthMethods;
import io.github.ma1uta.matrix.client.methods.CapabilityMethods;
import io.github.ma1uta.matrix.client.methods.ClientConfigMethods;
import io.github.ma1uta.matrix.client.methods.ContentMethods;
import io.github.ma1uta.matrix.client.methods.DeviceMethods;
import io.github.ma1uta.matrix.client.methods.EncryptionMethods;
import io.github.ma1uta.matrix.client.methods.EventContextMethods;
import io.github.ma1uta.matrix.client.methods.EventMethods;
import io.github.ma1uta.matrix.client.methods.FilterMethods;
import io.github.ma1uta.matrix.client.methods.PresenceMethods;
import io.github.ma1uta.matrix.client.methods.ProfileMethods;
import io.github.ma1uta.matrix.client.methods.ProtocolMethods;
import io.github.ma1uta.matrix.client.methods.PushMethods;
import io.github.ma1uta.matrix.client.methods.ReceiptMethods;
import io.github.ma1uta.matrix.client.methods.ReportMethods;
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

import java.io.Closeable;
import java.util.Objects;

/**
 * Matrix client.
 */
public class MatrixClient implements Closeable {

    private final RequestFactory requestFactory;
    private final RequestParams defaultParams;

    public MatrixClient(RequestFactory requestFactory) {
        this(requestFactory, new RequestParams());
    }

    public MatrixClient(RequestFactory requestFactory, RequestParams defaultParams) {
        this.defaultParams = Objects.requireNonNull(defaultParams, "The default `RequestParams` should be specified.");
        this.requestFactory = requestFactory;
    }

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return getDefaultParams().getAccessToken();
    }

    /**
     * Get the request factory.
     *
     * @return The {@link RequestFactory} instance.
     */
    public RequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * Get the default request params.
     *
     * @return The {@link RequestParams} instance.
     */
    public RequestParams getDefaultParams() {
        return defaultParams;
    }

    /**
     * Return homeserver url.
     *
     * @return homeserver url.
     */
    public String getHomeserverUrl() {
        return getRequestFactory().getHomeserverUrl();
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
        return new AccountMethods(getRequestFactory(), getDefaultParams(), this::afterLogin);
    }

    /**
     * Admin methods.
     *
     * @return admin methods.
     */
    public AdminMethods admin() {
        return new AdminMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Auth methods.
     *
     * @return auth methods.
     */
    public AuthMethods auth() {
        return new AuthMethods(getRequestFactory(), getDefaultParams(), this::afterLogin, this::afterLogout);
    }

    /**
     * Client config methods.
     *
     * @return client config methods.
     */
    public ClientConfigMethods clientConfig() {
        return new ClientConfigMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Content methods.
     *
     * @return content methods.
     */
    public ContentMethods content() {
        return new ContentMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Device methods.
     *
     * @return device methods.
     */
    public DeviceMethods device() {
        return new DeviceMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Encryption methods.
     *
     * @return encryption methods.
     */
    public EncryptionMethods encryption() {
        return new EncryptionMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Event context method.
     *
     * @return event context method.
     */
    public EventContextMethods eventContext() {
        return new EventContextMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Presence methods.
     *
     * @return presence methods.
     */
    public PresenceMethods presence() {
        return new PresenceMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Profile methods.
     *
     * @return profile methods.
     */
    public ProfileMethods profile() {
        return new ProfileMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Receipt method.
     *
     * @return receipt method.
     */
    public ReceiptMethods receipt() {
        return new ReceiptMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The report method.
     *
     * @return the report method.
     */
    public ReportMethods report() {
        return new ReportMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The search method.
     *
     * @return the search method.
     */
    public SearchMethods search() {
        return new SearchMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The send to device method.
     *
     * @return the send to device method.
     */
    public SendToDeviceMethods sendToDevice() {
        return new SendToDeviceMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Sync method.
     *
     * @return sync method.
     */
    public SyncMethods sync() {
        return new SyncMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Event methods.
     *
     * @return event methods.
     */
    public EventMethods event() {
        return new EventMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * FilterMethods methods.
     *
     * @return filter methods.
     */
    public FilterMethods filter() {
        return new FilterMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * RoomMethods apis.
     *
     * @return room apis.
     */
    public RoomMethods room() {
        return new RoomMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * TagInfo methods.
     *
     * @return tag methods.
     */
    public TagMethods tag() {
        return new TagMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Typing methods.
     *
     * @return typing methods.
     */
    public TypingMethods typing() {
        return new TypingMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * User directory method.
     *
     * @return user directory method.
     */
    public UserDirectoryMethods userDirectory() {
        return new UserDirectoryMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The versions method.
     *
     * @return the versions method.
     */
    public VersionMethods versions() {
        return new VersionMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The voip methods.
     *
     * @return the voip methods.
     */
    public VoipMethods turnServers() {
        return new VoipMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The push methods.
     *
     * @return the push methods.
     */
    public PushMethods push() {
        return new PushMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The protocol methods.
     *
     * @return the protocol methods.
     */
    public ProtocolMethods protocol() {
        return new ProtocolMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * The capabilities methods.
     *
     * @return the capabilities methods.
     */
    public CapabilityMethods capabilities() {
        return new CapabilityMethods(getRequestFactory(), getDefaultParams());
    }

    /**
     * Get the `user_id`.
     *
     * @return the user MXID.
     */
    public Id getUserId() {
        return getDefaultParams().getUserId();
    }

    /**
     * Action after login/register.
     *
     * @param loginResponse The login response.
     * @return The login response.
     */
    public LoginResponse afterLogin(LoginResponse loginResponse) {
        if (loginResponse == null) {
            getDefaultParams().accessToken(null);
        } else {
            getDefaultParams().userId(loginResponse.getUserId());
            getDefaultParams().accessToken(loginResponse.getAccessToken());
            getDefaultParams().deviceId(loginResponse.getDeviceId());
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
        getDefaultParams().accessToken(null);
        getDefaultParams().deviceId(null);
        return response;
    }

    /**
     * Matrix client builder.
     */
    public static class Builder extends AbstractClientBuilder<MatrixClient> {

        @Override
        public MatrixClient newInstance() {
            return new MatrixClient(getFactory(), getDefaultParams());
        }
    }
}
