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

package io.github.ma1uta.matrix.client.methods.blocked;

import io.github.ma1uta.matrix.Page;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import io.github.ma1uta.matrix.client.rest.blocked.SyncApi;
import io.github.ma1uta.matrix.event.Event;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

/**
 * Sync method.
 */
public class SyncMethods {

    private final SyncApi syncApi;

    public SyncMethods(RestClientBuilder restClientBuilder) {
        this.syncApi = restClientBuilder.build(SyncApi.class);
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
    public SyncResponse sync(String filter, String since, boolean fullState, String presence, Long timeout) {
        return syncApi.sync(filter, since, fullState, presence, timeout);
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
    public Page<Event> events(String from, Long timeout, String roomId) {
        return syncApi.events(from, timeout, roomId);
    }
}
