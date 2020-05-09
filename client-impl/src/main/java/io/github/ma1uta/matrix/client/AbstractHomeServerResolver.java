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

import io.github.ma1uta.matrix.client.filter.StreamHelper;
import io.github.ma1uta.matrix.client.model.serverdiscovery.HomeserverInfo;
import io.github.ma1uta.matrix.client.model.serverdiscovery.ServerDiscoveryResponse;
import io.github.ma1uta.matrix.client.model.version.VersionsResponse;
import io.github.ma1uta.matrix.impl.Deserializer;
import io.github.ma1uta.matrix.impl.exception.MatrixException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;
import javax.net.ssl.HttpsURLConnection;

/**
 * Home server resolver.
 */
public abstract class AbstractHomeServerResolver {

    private static final int SRV_RECORD_PARAMETER_COUNT = 4;

    private static final int SRV_RECORD_POST_INDEX = 2;

    private static final int SRV_RECORD_HOST_INDEX = 3;

    private static final String SCHEMA_PREFIX = "https://";

    private static final int DEFAULT_PORT = 8448;

    /**
     * Option to disable addition check correctness of the homeserver url.
     */
    public static final String DISABLE_HOMESERVER_URL_VERIFICATION = "jmsdk.resolver.homeserver.verification.disable";

    private static final Pattern IPv4_PATTERN = Pattern.compile(
        "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])(:\\d{2,5})?$");

    private static final Pattern IPv6_PATTERN = Pattern
        .compile("^([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}$|^\\[([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}]:\\d{2,5}$");

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHomeServerResolver.class);

    private final Deserializer deserializer;
    private final Boolean homeserverVerificationDisabled;

    public AbstractHomeServerResolver() {
        this(null);
    }

    public AbstractHomeServerResolver(Boolean homeserverVerificationDisabled) {
        this.deserializer = ServiceLoader.load(Deserializer.class).iterator().next();
        this.homeserverVerificationDisabled = homeserverVerificationDisabled;
    }

    protected boolean isHomeserverVerificationDisabled() {
        return (homeserverVerificationDisabled != null && homeserverVerificationDisabled)
            || Objects.equals(System.getProperty(DISABLE_HOMESERVER_URL_VERIFICATION), Boolean.TRUE.toString());
    }

    /**
     * Resolve homeserver url.
     *
     * @param domain homeserver domain.
     * @return homeserver url.
     */
    public Optional<ResolvedHomeserver> resolve(String domain) {
        LOGGER.trace("Resolve: {}", domain);
        return validateResolvedHomeserver(domain, resolveDomain(domain));
    }

    /**
     * Resolve homeserver url.
     *
     * @param domain homeserver domain.
     * @return homeserver url.
     */
    protected abstract Optional<ResolvedHomeserver> resolveDomain(String domain);

    protected boolean isValidHomeserverUrl(ResolvedHomeserver homeserver) {
        String version = homeserver.getUrl().toString() + "/_matrix/client/versions";
        try {
            VersionsResponse response;
            URLConnection connection = new URL(version).openConnection();
            if (connection instanceof HttpsURLConnection && homeserver.getOptionalHostnameVerifier().isPresent()) {
                ((HttpsURLConnection) connection).setHostnameVerifier(homeserver.getOptionalHostnameVerifier().get());
            }
            try (InputStream inputStream = connection.getInputStream()) {
                byte[] content = StreamHelper.toByteArray(inputStream);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Content from {}: {}", version, new String(content, StandardCharsets.UTF_8));
                }
                response = deserializer.deserialize(content, VersionsResponse.class);
            }
            if (LOGGER.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder("Server: ").append(homeserver.getUrl()).append("\nVersions:\n");
                for (String responseVersion : response.getVersions()) {
                    sb.append("- ").append(responseVersion).append("\n");
                }
                if (response.getUnstableFeatures() != null) {
                    sb.append("Features:\n");
                    for (Map.Entry<String, Boolean> features : response.getUnstableFeatures().entrySet()) {
                        sb.append("- ").append(features.getKey()).append(": ").append(features.getValue()).append("\n");
                    }
                }
                LOGGER.trace(sb.toString());
            }
        } catch (IOException e) {
            LOGGER.error("Wrong url: " + version, e);
            return false;
        }
        return true;
    }

    protected Optional<ResolvedHomeserver> tryParseIPAddresses(String domain) {
        LOGGER.trace("Try resolve as ip addresses");

        Matcher matcher = IPv4_PATTERN.matcher(domain);
        if (matcher.matches()) {
            try {
                return Optional.of(new ResolvedHomeserver(new URL(domain)));
            } catch (MalformedURLException e) {
                LOGGER.error("Unable to parse IPv4 address: " + domain, e);
            }
        }

        matcher = IPv6_PATTERN.matcher(domain);
        if (matcher.matches()) {
            try {
                return Optional.of(new ResolvedHomeserver(new URL(domain)));
            } catch (MalformedURLException e) {
                LOGGER.error("Unable to parse IPv6 address: " + domain, e);
            }
        }

        LOGGER.trace("Unable to resolve as IPv4 or IPv6 addresses: {}, try other way.", domain);
        return Optional.empty();
    }

    protected Optional<ResolvedHomeserver> tryDirectUrl(String domain) {
        LOGGER.trace("Try resolve via direct url.");

        int portIndex = domain.lastIndexOf(":");
        String domainWithPort = portIndex != -1 ? domain : domain + ":" + DEFAULT_PORT;
        int schemaIndex = domain.indexOf("://");
        String homeserverUrl = schemaIndex != -1 ? domainWithPort : SCHEMA_PREFIX + domainWithPort;
        try {
            return Optional.of(new ResolvedHomeserver(new URL(homeserverUrl)));
        } catch (MalformedURLException e) {
            LOGGER.warn("Malformed homeserver url: " + homeserverUrl, e);
        }
        return Optional.empty();
    }

    protected Optional<ResolvedHomeserver> trySrvRecord(String domain) {
        LOGGER.trace("Try resolve via SRV record.");

        String srvRecord = String.format("_matrix._tcp.%s", domain);
        try {
            InitialDirContext dirContext = prepareContext();
            if (dirContext == null) {
                LOGGER.warn("Unable to initialize DNS context");
                return Optional.empty();
            }
            Attributes attributes = dirContext.getAttributes(srvRecord, new String[] {"SRV"});
            NamingEnumeration<?> srv = attributes.get("srv").getAll();
            if (srv.hasMore()) {
                Object nextValue = srv.next();
                if (nextValue instanceof String) {
                    URL homeserverUrl = parseSrvRecord((String) nextValue);
                    if (homeserverUrl != null) {
                        return Optional.of(new ResolvedHomeserver(homeserverUrl, new HomeServerVerifier(domain)));
                    }
                } else {
                    LOGGER.warn("Unrecognized SRV record: {}", nextValue.getClass());
                }
            }
        } catch (NamingException e) {
            LOGGER.warn("Unable to fetch SRV record: " + srvRecord, e);
        }
        LOGGER.trace("Unable to resolve via SRV record: {}, try other way.", domain);
        return Optional.empty();
    }

    protected InitialDirContext prepareContext() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
        env.put("java.naming.provider.url", "dns:");
        InitialDirContext context;
        try {
            context = new InitialDirContext(env);
        } catch (NamingException e) {
            LOGGER.error("Unable to create naming context", e);
            return null;
        }
        return context;
    }

    @SuppressWarnings("checkstyle:RegexpSingleLine")
    protected URL parseSrvRecord(String record) {
        LOGGER.trace("SRV record: {}", record);
        String[] params = record.split(" ");
        if (params.length == SRV_RECORD_PARAMETER_COUNT) {
            String homeserverUrl = SCHEMA_PREFIX + params[SRV_RECORD_HOST_INDEX];
            if (homeserverUrl.endsWith(".")) {
                homeserverUrl = homeserverUrl.substring(0, homeserverUrl.length() - 1);
            }
            homeserverUrl += ":" + params[SRV_RECORD_POST_INDEX];
            try {
                return new URL(homeserverUrl);
            } catch (MalformedURLException e) {
                LOGGER.error("Malformed homeserver url: " + homeserverUrl);
            }
        }
        LOGGER.info("Unable to parse SRV record: {}, try other way.", record);
        return null;
    }

    protected Optional<ResolvedHomeserver> tryWellKnown(String domain) {
        LOGGER.trace("Try resolve via well-known");
        String homeserverUrl = SCHEMA_PREFIX + domain;
        ServerDiscoveryResponse response = null;
        try {
            String wellKnownUrl = homeserverUrl + "/.well-known/matrix/client";
            try (InputStream inputStream = new URL(wellKnownUrl).openStream()) {
                byte[] content = StreamHelper.toByteArray(inputStream);
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("Content from {}: {}", wellKnownUrl, new String(content, StandardCharsets.UTF_8));
                }
                response = deserializer.deserialize(content, ServerDiscoveryResponse.class);
            }
        } catch (MatrixException e) {
            LOGGER.trace("Unable to connect to homeserver " + homeserverUrl, e);
        } catch (Exception e) {
            LOGGER.trace("Unable to discover homeserver url: " + homeserverUrl, e);
        }
        if (response == null) {
            LOGGER.trace("Unable to get homeserver url of the domain '{}' via well-known, try other steps.", domain);
            return Optional.empty();
        }
        HomeserverInfo homeserver = response.getHomeserver();
        if (homeserver != null && homeserver.getBaseUrl() != null && !homeserver.getBaseUrl().trim().isEmpty()) {
            try {
                return Optional.of(new ResolvedHomeserver(new URL(homeserver.getBaseUrl())));
            } catch (MalformedURLException e) {
                LOGGER.warn("Malformed homeserver url: " + homeserver.getBaseUrl(), e);
            }
        }
        LOGGER.trace("Unable to get homeserver url of the domain '{}' via well-known, try other way.", domain);
        return Optional.empty();
    }

    protected Optional<ResolvedHomeserver> validateResolvedHomeserver(String domain, Optional<ResolvedHomeserver> resolvedHomeserver) {
        if (!resolvedHomeserver.isPresent()) {
            LOGGER.error("Unable to resolve homeserver url of the domain: {}", domain);
            return Optional.empty();
        }

        ResolvedHomeserver homeserver = resolvedHomeserver.get();
        if (isHomeserverVerificationDisabled()) {
            LOGGER.trace("Checking homeserver url disabled.");
        } else {
            LOGGER.trace("Check homeserver url: {}", homeserver);
            boolean valid = isValidHomeserverUrl(homeserver);
            if (!valid) {
                LOGGER.error("Unable to check the homeserver url: {}", homeserver);
                return Optional.empty();
            }
        }
        LOGGER.info("Resolved: {} => {}", domain, homeserver.toString());
        return resolvedHomeserver;
    }
}
