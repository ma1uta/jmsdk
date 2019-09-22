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
import io.github.ma1uta.matrix.client.factory.RequestFactory;
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

    protected RestClientBuilder createClientBuilder() {
        if (builder == null) {
            synchronized (this) {
                if (builder != null) {
                    return builder;
                }
                builder = RestClientBuilder.newBuilder()
                    .register(new ErrorFilter())
                    .register(new LoggingFilter())
                    .register(headerClientFilter)
                    .baseUrl(getHomeserverUrl());
                ServiceLoader.load(RestClientBuilderConfigurer.class).iterator().forEachRemaining(c -> c.configure(builder));
            }
        }
        return builder;
    }

    protected <T> T getMethod(Class<T> clazz, Supplier<T> creator) {
        return clazz.cast(methods.computeIfAbsent(clazz, key -> creator.get()));
    }

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return getAccountInfo().getAccessToken();
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
        return getMethod(VersionMethods.class, () -> new VersionMethods(createClientBuilder()));
    }

    /**
     * The voip methods.
     *
     * @return the voip methods.
     */
    public VoipMethods turnServers() {
        return getMethod(VoipMethods.class, () -> new VoipMethods(createClientBuilder()));
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
        return getMethod(CapabilityMethods.class, () -> new CapabilityMethods(createClientBuilder()));
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
