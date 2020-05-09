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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Home server resolver.
 */
public class FederationHomeServerResolver extends AbstractHomeServerResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FederationHomeServerResolver.class);

    public FederationHomeServerResolver() {
        super(null);
    }

    public FederationHomeServerResolver(Boolean homeserverVerificationDisabled) {
        super(homeserverVerificationDisabled);
    }

    /**
     * Resolve homeserver url.
     *
     * @param domain homeserver domain.
     * @return homeserver url.
     */
    @Override
    public Optional<ResolvedHomeserver> resolve(String domain) {
        LOGGER.trace("Resolve: {}", domain);

        Optional<ResolvedHomeserver> resolvedHomeserver = tryParseIPAddresses(domain);
        if (!resolvedHomeserver.isPresent()) {
            resolvedHomeserver = tryWellKnown(domain);
        }
        if (!resolvedHomeserver.isPresent()) {
            resolvedHomeserver = trySrvRecord(domain);
        }
        if (!resolvedHomeserver.isPresent()) {
            resolvedHomeserver = tryDirectUrl(domain);
        }
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
