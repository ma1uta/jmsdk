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

package io.github.ma1uta.matrix.client.methods;

import io.github.ma1uta.matrix.Event;
import io.github.ma1uta.matrix.Page;
import io.github.ma1uta.matrix.client.api.SyncApi;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;

import java.util.concurrent.CompletableFuture;
import javax.ws.rs.core.GenericType;

/**
 * Sync method.
 */
public class SyncMethods extends AbstractMethods {

    public SyncMethods(RequestFactory factory, RequestParams defaultParams) {
        super(factory, defaultParams);
    }

    /**
     * Sync events.
     *
     * @param filter    The filter name.
     * @param since     The next batch token.
     * @param fullState The full state or not.
     * @param presence  The offline presence or not.
     * @param timeout   The timeout.
     * @return The sync data.
     */
    public CompletableFuture<SyncResponse> sync(String filter, String since, boolean fullState, String presence, Long timeout) {
        RequestParams params = defaults().clone()
            .query("filter", filter)
            .query("since", since)
            .query("fullState", fullState)
            .query("presence", presence)
            .query("timeout", timeout);
        return factory().get(SyncApi.class, "sync", params, SyncResponse.class);
    }

    /**
     * This will listen for new events related to a particular room and return them to the caller. This will block until an event is
     * received, or until the timeout is reached.
     *
     * @param from    The token to stream from. This token is either from a previous request to this API or from the initial sync
     *                API.
     * @param timeout The maximum time in milliseconds to wait for an event.
     * @param roomId  The room ID for which events should be returned.
     * @return The events received, which may be none.
     */
    public CompletableFuture<Page<Event>> events(String from, Long timeout, String roomId) {
        RequestParams params = defaults().clone()
            .query("from", from)
            .query("roomId", roomId)
            .query("timeout", timeout);
        return factory().get(SyncApi.class, "events", params, new GenericType<Page<Event>>() {
        });
    }
}
