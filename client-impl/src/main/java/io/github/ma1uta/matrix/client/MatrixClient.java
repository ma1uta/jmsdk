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

import io.github.ma1uta.matrix.EmptyResponse;
import io.github.ma1uta.matrix.client.filter.CustomHeaderClientFilter;
import io.github.ma1uta.matrix.client.filter.ErrorFilter;
import io.github.ma1uta.matrix.client.filter.LoggingFilter;
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
import io.github.ma1uta.matrix.impl.RestClientBuilderConfigurer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.Closeable;
import java.net.URL;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Matrix client.
 */
public class MatrixClient implements Closeable {

    private volatile URL homeserverURL;
    private volatile RestClientBuilder builder;
    private final HomeServerResolver homeServerResolver = new HomeServerResolver();
    private final String domain;
    private final CustomHeaderClientFilter headerClientFilter = new CustomHeaderClientFilter();
    private final Map<Class<?>, Object> methods = new ConcurrentHashMap<>();
    private final AccountInfo accountInfo;

    public MatrixClient(String domain) {
        this(domain, new AccountInfo());
    }

    public MatrixClient(String domain, AccountInfo accountInfo) {
        this.domain = domain;
        this.accountInfo = accountInfo;
    }

    /**
     * Get homeserver URL.
     *
     * @return homeserver URL.
     */
    public URL getHomeserverUrl() {
        if (homeserverURL == null) {
            synchronized (this) {
                if (homeserverURL != null) {
                    return homeserverURL;
                }
                homeserverURL = homeServerResolver.resolve(domain);
            }
        }
        return homeserverURL;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    protected RestClientBuilder newClientBuilder() {
        return RestClientBuilder.newBuilder()
            .register(new ErrorFilter())
            .register(new LoggingFilter())
            .register(headerClientFilter)
            .baseUrl(getHomeserverUrl());
    }

    protected RestClientBuilder getClientBuilder() {
        if (builder == null) {
            synchronized (this) {
                if (builder != null) {
                    return builder;
                }
                builder = newClientBuilder();
                ServiceLoader.load(RestClientBuilderConfigurer.class).iterator().forEachRemaining(c -> c.configure(builder));
            }
        }
        return builder;
    }

    protected <T> T getMethod(Class<T> clazz, Supplier<T> creator) {
        return clazz.cast(methods.computeIfAbsent(clazz, key -> creator.get()));
    }

    /**
     * Get the homeserver domain.
     *
     * @return The homeserver domain.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return getAccountInfo().getAccessToken();
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
        return getMethod(AccountMethods.class, () -> new AccountMethods(getClientBuilder(), this::afterLogin));
    }

    /**
     * Admin methods.
     *
     * @return admin methods.
     */
    public AdminMethods admin() {
        return getMethod(AdminMethods.class, () -> new AdminMethods(getClientBuilder()));
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
     * Client config methods.
     *
     * @return client config methods.
     */
    public ClientConfigMethods clientConfig() {
        return getMethod(ClientConfigMethods.class, () -> new ClientConfigMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * Content methods.
     *
     * @return content methods.
     */
    public ContentMethods content() {
        return getMethod(ContentMethods.class, () -> new ContentMethods(getClientBuilder()));
    }

    /**
     * Device methods.
     *
     * @return device methods.
     */
    public DeviceMethods device() {
        return getMethod(DeviceMethods.class, () -> new DeviceMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * Encryption methods.
     *
     * @return encryption methods.
     */
    public EncryptionMethods encryption() {
        return getMethod(EncryptionMethods.class, () -> new EncryptionMethods(getClientBuilder()));
    }

    /**
     * Event context method.
     *
     * @return event context method.
     */
    public EventContextMethods eventContext() {
        return getMethod(EventContextMethods.class, () -> new EventContextMethods(getClientBuilder()));
    }

    /**
     * Presence methods.
     *
     * @return presence methods.
     */
    public PresenceMethods presence() {
        return getMethod(PresenceMethods.class, () -> new PresenceMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * Profile methods.
     *
     * @return profile methods.
     */
    public ProfileMethods profile() {
        return getMethod(ProfileMethods.class, () -> new ProfileMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * Receipt method.
     *
     * @return receipt method.
     */
    public ReceiptMethods receipt() {
        return getMethod(ReceiptMethods.class, () -> new ReceiptMethods(getClientBuilder()));
    }

    /**
     * The report method.
     *
     * @return the report method.
     */
    public ReportMethods report() {
        return getMethod(ReportMethods.class, () -> new ReportMethods(getClientBuilder()));
    }

    /**
     * The search method.
     *
     * @return the search method.
     */
    public SearchMethods search() {
        return getMethod(SearchMethods.class, () -> new SearchMethods(getClientBuilder()));
    }

    /**
     * The send to device method.
     *
     * @return the send to device method.
     */
    public SendToDeviceMethods sendToDevice() {
        return getMethod(SendToDeviceMethods.class, () -> new SendToDeviceMethods(getClientBuilder()));
    }

    /**
     * Sync method.
     *
     * @return sync method.
     */
    public SyncMethods sync() {
        return getMethod(SyncMethods.class, () -> new SyncMethods(getClientBuilder()));
    }

    /**
     * Event methods.
     *
     * @return event methods.
     */
    public EventMethods event() {
        return getMethod(EventMethods.class, () -> new EventMethods(getClientBuilder()));
    }

    /**
     * FilterMethods methods.
     *
     * @return filter methods.
     */
    public FilterMethods filter() {
        return getMethod(FilterMethods.class, () -> new FilterMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * RoomMethods apis.
     *
     * @return room apis.
     */
    public RoomMethods room() {
        return getMethod(RoomMethods.class, () -> new RoomMethods(getClientBuilder()));
    }

    /**
     * TagInfo methods.
     *
     * @return tag methods.
     */
    public TagMethods tag() {
        return getMethod(TagMethods.class, () -> new TagMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * Typing methods.
     *
     * @return typing methods.
     */
    public TypingMethods typing() {
        return getMethod(TypingMethods.class, () -> new TypingMethods(getClientBuilder(), getAccountInfo()));
    }

    /**
     * User directory method.
     *
     * @return user directory method.
     */
    public UserDirectoryMethods userDirectory() {
        return getMethod(UserDirectoryMethods.class, () -> new UserDirectoryMethods(getClientBuilder()));
    }

    /**
     * The versions method.
     *
     * @return the versions method.
     */
    public VersionMethods versions() {
        return getMethod(VersionMethods.class, () -> new VersionMethods(getClientBuilder()));
    }

    /**
     * The voip methods.
     *
     * @return the voip methods.
     */
    public VoipMethods turnServers() {
        return getMethod(VoipMethods.class, () -> new VoipMethods(getClientBuilder()));
    }

    /**
     * The push methods.
     *
     * @return the push methods.
     */
    public PushMethods push() {
        return getMethod(PushMethods.class, () -> new PushMethods(getClientBuilder()));
    }

    /**
     * The protocol methods.
     *
     * @return the protocol methods.
     */
    public ProtocolMethods protocol() {
        return getMethod(ProtocolMethods.class, () -> new ProtocolMethods(getClientBuilder()));
    }

    /**
     * The capabilities methods.
     *
     * @return the capabilities methods.
     */
    public CapabilityMethods capabilities() {
        return getMethod(CapabilityMethods.class, () -> new CapabilityMethods(getClientBuilder()));
    }

    /**
     * Get the `user_id`.
     *
     * @return the user MXID.
     */
    public String getUserId() {
        return getAccountInfo().getUserId();
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
            getAccountInfo().setUserId(loginResponse.getUserId());
            getAccountInfo().setAccessToken(loginResponse.getAccessToken());
            getAccountInfo().setDeviceId(loginResponse.getDeviceId());
            getAccountInfo().setServerInfo(loginResponse.getWellKnown());

            headerClientFilter.addHeader(AUTHORIZATION, "Bearer " + loginResponse.getAccessToken());
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
        getAccountInfo().setAccessToken(null);
        getAccountInfo().setDeviceId(null);

        headerClientFilter.removeHeader(AUTHORIZATION);
        return response;
    }

    /**
     * Matrix client builder.
     */
    public static class Builder extends AbstractClientBuilder<MatrixClient> {

        @Override
        public MatrixClient newInstance() {
            return new MatrixClient(domain, accountInfo);
        }
    }
}
