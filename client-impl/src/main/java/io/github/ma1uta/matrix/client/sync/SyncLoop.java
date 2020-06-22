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

package io.github.ma1uta.matrix.client.sync;

import io.github.ma1uta.matrix.client.methods.blocked.SyncMethods;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Runnable to receive events and other data from the server.
 */
public class SyncLoop implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncLoop.class);

    private final SyncMethods syncMethods;
    private final BiConsumer<SyncResponse, SyncParams> inboundListener;
    private final SyncParams state = new SyncParams();
    private SyncParams init = null;

    public SyncLoop(SyncMethods syncMethods, BiConsumer<SyncResponse, SyncParams> inboundListener) {
        this.syncMethods = syncMethods;
        this.inboundListener = inboundListener;
    }

    public SyncParams getInit() {
        return init;
    }

    public void setInit(SyncParams init) {
        this.init = init;
    }

    @Override
    public void run() {
        Objects.requireNonNull(syncMethods, "The Matrix client must be specified.");
        Objects.requireNonNull(inboundListener, "Not found inbound listeners, the sync will erase the response.");

        if (getInit() != null) {
            state.from(getInit());
        }

        LOGGER.info("SyncLoop started");
        while (!(Thread.interrupted() || state.isTerminate())) {
            try {
                SyncResponse sync = syncMethods.sync(
                    state.getFilter(),
                    state.getNextBatch(),
                    state.isFullState(),
                    state.getPresence(),
                    state.getTimeout()
                );
                state.setNextBatch(sync.getNextBatch());
                inboundListener.accept(sync, state);
            } catch (Exception e) {
                LOGGER.error("Exception: ", e);
            }
        }
        LOGGER.info("SyncLoop stopped");
    }
}
