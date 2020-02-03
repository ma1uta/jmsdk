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

import java.util.Objects;

/**
 * A Client builder.
 *
 * @param <C> class of the matrix client.
 */
public abstract class AbstractClientBuilder<C> {

    protected ConnectionInfo connectionInfo = new ConnectionInfo();

    protected AbstractClientBuilder() {
    }

    /**
     * Specify a user ID.
     *
     * @param userId The user ID.
     * @return This builder.
     */
    public AbstractClientBuilder<C> userId(String userId) {
        this.connectionInfo.setUserId(userId);
        return this;
    }

    /**
     * Specify an access token.
     *
     * @param accessToken The access token.
     * @return This builder.
     */
    public AbstractClientBuilder<C> accessToken(String accessToken) {
        this.connectionInfo.setAccessToken(accessToken);
        return this;
    }

    /**
     * Specify the domain.
     *
     * @param domain Domain.
     * @return This builder.
     */
    public AbstractClientBuilder<C> domain(String domain) {
        this.connectionInfo.setDomain(domain);
        return this;
    }

    /**
     * Build a new client.
     *
     * @return The new client.
     */
    public C build() {
        Objects.requireNonNull(this.connectionInfo.getDomain(), "Domain must be set.");
        return newInstance();
    }

    /**
     * Create a new client instance.
     *
     * @return The new client.
     */
    protected abstract C newInstance();
}
