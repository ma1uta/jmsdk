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

import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.client.factory.RequestFactory;

import java.util.Objects;

/**
 * A Client builder.
 *
 * @param <C> class of the matrix client.
 */
public abstract class AbstractClientBuilder<C> {

    private RequestParams defaultParams = new RequestParams();
    private RequestFactory factory;

    protected AbstractClientBuilder() {
    }

    protected RequestParams getDefaultParams() {
        return defaultParams;
    }

    public RequestFactory getFactory() {
        return factory;
    }

    /**
     * Specify a user ID.
     *
     * @param userId The user ID.
     * @return This builder.
     */
    public AbstractClientBuilder<C> userId(Id userId) {
        this.defaultParams.userId(userId);
        return this;
    }

    /**
     * Specify an access token.
     *
     * @param accessToken The access token.
     * @return This builder.
     */
    public AbstractClientBuilder<C> accessToken(String accessToken) {
        this.defaultParams.accessToken(accessToken);
        return this;
    }

    /**
     * Specify a request factory.
     *
     * @param factory The request factory.
     * @return This builder.
     */
    public AbstractClientBuilder<C> requestFactory(RequestFactory factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Build a new client.
     *
     * @return The new client.
     */
    public C build() {
        Objects.requireNonNull(this.factory, "Request factory must be set.");
        return newInstance();
    }

    /**
     * Create a new client instance.
     *
     * @return The new client.
     */
    protected abstract C newInstance();
}
