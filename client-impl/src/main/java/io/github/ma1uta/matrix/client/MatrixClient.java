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

import io.github.ma1uta.matrix.client.filter.AuthorizationFilter;
import io.github.ma1uta.matrix.client.filter.ContentTypeFilter;
import io.github.ma1uta.matrix.client.filter.ErrorFilter;
import io.github.ma1uta.matrix.client.filter.LoggingFilter;
import io.github.ma1uta.matrix.client.methods.async.AccountAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.AdminAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.CapabilityAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.ClientConfigAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.ContentAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.DeviceAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.EncryptionAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.EventAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.EventContextAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.FilterAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.OpenIdAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.PresenceAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.ProfileAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.ProtocolAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.PushAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.ReceiptAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.ReportAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.RoomAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.SearchAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.SendToDeviceAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.SyncAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.TagAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.TypingAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.UserDirectoryAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.VersionAsyncMethods;
import io.github.ma1uta.matrix.client.methods.async.VoipAsyncMethods;
import io.github.ma1uta.matrix.client.methods.blocked.AccountMethods;
import io.github.ma1uta.matrix.client.methods.blocked.AdminMethods;
import io.github.ma1uta.matrix.client.methods.blocked.CapabilityMethods;
import io.github.ma1uta.matrix.client.methods.blocked.ClientConfigMethods;
import io.github.ma1uta.matrix.client.methods.blocked.ContentMethods;
import io.github.ma1uta.matrix.client.methods.blocked.DeviceMethods;
import io.github.ma1uta.matrix.client.methods.blocked.EncryptionMethods;
import io.github.ma1uta.matrix.client.methods.blocked.EventContextMethods;
import io.github.ma1uta.matrix.client.methods.blocked.EventMethods;
import io.github.ma1uta.matrix.client.methods.blocked.FilterMethods;
import io.github.ma1uta.matrix.client.methods.blocked.OpenIdMethods;
import io.github.ma1uta.matrix.client.methods.blocked.PresenceMethods;
import io.github.ma1uta.matrix.client.methods.blocked.ProfileMethods;
import io.github.ma1uta.matrix.client.methods.blocked.ProtocolMethods;
import io.github.ma1uta.matrix.client.methods.blocked.PushMethods;
import io.github.ma1uta.matrix.client.methods.blocked.ReceiptMethods;
import io.github.ma1uta.matrix.client.methods.blocked.ReportMethods;
import io.github.ma1uta.matrix.client.methods.blocked.RoomMethods;
import io.github.ma1uta.matrix.client.methods.blocked.SearchMethods;
import io.github.ma1uta.matrix.client.methods.blocked.SendToDeviceMethods;
import io.github.ma1uta.matrix.client.methods.blocked.SyncMethods;
import io.github.ma1uta.matrix.client.methods.blocked.TagMethods;
import io.github.ma1uta.matrix.client.methods.blocked.TypingMethods;
import io.github.ma1uta.matrix.client.methods.blocked.UserDirectoryMethods;
import io.github.ma1uta.matrix.client.methods.blocked.VersionMethods;
import io.github.ma1uta.matrix.client.methods.blocked.VoipMethods;
import io.github.ma1uta.matrix.impl.RestClientBuilderConfigurer;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import java.io.Closeable;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;
import javax.net.ssl.HostnameVerifier;

/**
 * Matrix client.
 */
public abstract class MatrixClient implements Closeable {

    private volatile URL homeserverURL = null;
    private volatile HostnameVerifier hostnameVerifier = null;
    private final ClientHomeServerResolver homeServerResolver = new ClientHomeServerResolver();
    private final Map<Class<?>, Object> methods = new ConcurrentHashMap<>();
    private final AuthorizationFilter authorizationFilter;
    private final ConnectionInfo connectionInfo;
    private final ExecutorService executorService;

    public MatrixClient(String domain) {
        this(new ConnectionInfo(domain));
    }

    public MatrixClient(ConnectionInfo connectionInfo) {
        this(connectionInfo, null);
    }

    public MatrixClient(ConnectionInfo connectionInfo, ExecutorService executorService) {
        this.connectionInfo = connectionInfo;
        this.authorizationFilter = new AuthorizationFilter(this.connectionInfo);
        this.executorService = executorService;
    }

    /**
     * Get homeserver URL.
     *
     * @return homeserver URL.
     */
    public URL getHomeserverUrl() {
        resolveHomeserver();
        return homeserverURL;
    }

    public ConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    protected synchronized void resolveHomeserver() {
        if (homeserverURL == null) {
            Optional<ResolvedHomeserver> optionalResolvedHomeserver = homeServerResolver.resolve(connectionInfo.getDomain());
            if (optionalResolvedHomeserver.isPresent()) {
                ResolvedHomeserver homeserver = optionalResolvedHomeserver.get();
                homeserverURL = homeserver.getUrl();
                hostnameVerifier = homeserver.getOptionalHostnameVerifier().orElse(null);
            } else {
                throw new IllegalStateException("Unable to resolve homeserver url: " + connectionInfo.getDomain());
            }
        }
    }

    protected ExecutorService getExecutorService() {
        return executorService;
    }

    protected RestClientBuilder newClientBuilder() {
        resolveHomeserver();
        RestClientBuilder builder = RestClientBuilder.newBuilder()
            .register(new ErrorFilter())
            .register(new LoggingFilter())
            .register(authorizationFilter)
            .register(new ContentTypeFilter())
            .baseUrl(getHomeserverUrl());
        if (hostnameVerifier != null) {
            builder.hostnameVerifier(hostnameVerifier);
        }
        if (getExecutorService() != null) {
            builder.executorService(executorService);
        }
        return builder;
    }

    protected RestClientBuilder getClientBuilder() {
        RestClientBuilder builder = newClientBuilder();
        ServiceLoader.load(RestClientBuilderConfigurer.class).iterator().forEachRemaining(c -> c.configure(builder));
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
        return getConnectionInfo().getDomain();
    }

    /**
     * Get the access token.
     *
     * @return The access token.
     */
    public String getAccessToken() {
        return getConnectionInfo().getAccessToken();
    }

    @Override
    public void close() {
    }

    /**
     * Account methods.
     *
     * @return account methods.
     */
    public abstract AccountAsyncMethods accountAsync();

    /**
     * Account methods.
     *
     * @return account methods.
     */
    public abstract AccountMethods account();

    /**
     * Admin methods.
     *
     * @return admin methods.
     */
    public AdminAsyncMethods adminAsync() {
        return getMethod(AdminAsyncMethods.class, () -> new AdminAsyncMethods(getClientBuilder()));
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
     * Client config methods.
     *
     * @return client config methods.
     */
    public ClientConfigAsyncMethods clientConfigAsync() {
        return getMethod(ClientConfigAsyncMethods.class, () -> new ClientConfigAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Client config methods.
     *
     * @return client config methods.
     */
    public ClientConfigMethods clientConfig() {
        return getMethod(ClientConfigMethods.class, () -> new ClientConfigMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Content methods.
     *
     * @return content methods.
     */
    public ContentAsyncMethods contentAsync() {
        return getMethod(ContentAsyncMethods.class, () -> new ContentAsyncMethods(getClientBuilder()));
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
    public DeviceAsyncMethods deviceAsync() {
        return getMethod(DeviceAsyncMethods.class, () -> new DeviceAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Device methods.
     *
     * @return device methods.
     */
    public DeviceMethods device() {
        return getMethod(DeviceMethods.class, () -> new DeviceMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Encryption methods.
     *
     * @return encryption methods.
     */
    public EncryptionAsyncMethods encryptionAsync() {
        return getMethod(EncryptionAsyncMethods.class, () -> new EncryptionAsyncMethods(getClientBuilder()));
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
    public EventContextAsyncMethods eventContextAsync() {
        return getMethod(EventContextAsyncMethods.class, () -> new EventContextAsyncMethods(getClientBuilder()));
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
    public PresenceAsyncMethods presenceAsync() {
        return getMethod(PresenceAsyncMethods.class, () -> new PresenceAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Presence methods.
     *
     * @return presence methods.
     */
    public PresenceMethods presence() {
        return getMethod(PresenceMethods.class, () -> new PresenceMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Profile methods.
     *
     * @return profile methods.
     */
    public ProfileAsyncMethods profileAsync() {
        return getMethod(ProfileAsyncMethods.class, () -> new ProfileAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Profile methods.
     *
     * @return profile methods.
     */
    public ProfileMethods profile() {
        return getMethod(ProfileMethods.class, () -> new ProfileMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Receipt method.
     *
     * @return receipt method.
     */
    public ReceiptAsyncMethods receiptAsync() {
        return getMethod(ReceiptAsyncMethods.class, () -> new ReceiptAsyncMethods(getClientBuilder()));
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
    public ReportAsyncMethods reportAsync() {
        return getMethod(ReportAsyncMethods.class, () -> new ReportAsyncMethods(getClientBuilder()));
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
    public SearchAsyncMethods searchAsync() {
        return getMethod(SearchAsyncMethods.class, () -> new SearchAsyncMethods(getClientBuilder()));
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
    public SendToDeviceAsyncMethods sendToDeviceAsync() {
        return getMethod(SendToDeviceAsyncMethods.class, () -> new SendToDeviceAsyncMethods(getClientBuilder()));
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
    public SyncAsyncMethods syncAsync() {
        return getMethod(SyncAsyncMethods.class, () -> new SyncAsyncMethods(getClientBuilder()));
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
    public EventAsyncMethods eventAsync() {
        return getMethod(EventAsyncMethods.class, () -> new EventAsyncMethods(getClientBuilder()));
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
     * FilterAsyncMethods methods.
     *
     * @return filter methods.
     */
    public FilterAsyncMethods filterAsync() {
        return getMethod(FilterAsyncMethods.class, () -> new FilterAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * FilterAsyncMethods methods.
     *
     * @return filter methods.
     */
    public FilterMethods filter() {
        return getMethod(FilterMethods.class, () -> new FilterMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * RoomAsyncMethods apis.
     *
     * @return room apis.
     */
    public RoomAsyncMethods roomAsync() {
        return getMethod(RoomAsyncMethods.class, () -> new RoomAsyncMethods(getClientBuilder()));
    }

    /**
     * RoomAsyncMethods apis.
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
    public TagAsyncMethods tagAsync() {
        return getMethod(TagAsyncMethods.class, () -> new TagAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * TagInfo methods.
     *
     * @return tag methods.
     */
    public TagMethods tag() {
        return getMethod(TagMethods.class, () -> new TagMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Typing methods.
     *
     * @return typing methods.
     */
    public TypingAsyncMethods typingAsync() {
        return getMethod(TypingAsyncMethods.class, () -> new TypingAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Typing methods.
     *
     * @return typing methods.
     */
    public TypingMethods typing() {
        return getMethod(TypingMethods.class, () -> new TypingMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * User directory method.
     *
     * @return user directory method.
     */
    public UserDirectoryAsyncMethods userDirectoryAsync() {
        return getMethod(UserDirectoryAsyncMethods.class, () -> new UserDirectoryAsyncMethods(getClientBuilder()));
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
    public VersionAsyncMethods versionsAsync() {
        return getMethod(VersionAsyncMethods.class, () -> new VersionAsyncMethods(getClientBuilder()));
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
    public VoipAsyncMethods turnServersAsync() {
        return getMethod(VoipAsyncMethods.class, () -> new VoipAsyncMethods(getClientBuilder()));
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
    public PushAsyncMethods pushAsync() {
        return getMethod(PushAsyncMethods.class, () -> new PushAsyncMethods(getClientBuilder()));
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
    public ProtocolAsyncMethods protocolAsync() {
        return getMethod(ProtocolAsyncMethods.class, () -> new ProtocolAsyncMethods(getClientBuilder()));
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
    public CapabilityAsyncMethods capabilitiesAsync() {
        return getMethod(CapabilityAsyncMethods.class, () -> new CapabilityAsyncMethods(getClientBuilder()));
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
     * The OpenID methods.
     *
     * @return The openID methods.
     */
    public OpenIdAsyncMethods openIdAsync() {
        return getMethod(OpenIdAsyncMethods.class, () -> new OpenIdAsyncMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * The OpenID methods.
     *
     * @return The openID methods.
     */
    public OpenIdMethods openId() {
        return getMethod(OpenIdMethods.class, () -> new OpenIdMethods(getClientBuilder(), getConnectionInfo()));
    }

    /**
     * Get the `user_id`.
     *
     * @return the user MXID.
     */
    public String getUserId() {
        return getConnectionInfo().getUserId();
    }
}
