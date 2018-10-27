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

package io.github.ma1uta.matrix.client.sync;

import io.github.ma1uta.matrix.client.methods.SyncMethods;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;

/**
 * Runnable to receive events and other data from the server.
 */
public class SyncLoop implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncLoop.class);

    private final SyncMethods syncMethods;
    private BiFunction<SyncResponse, SyncParams, SyncParams> inboundListener;
    private SyncParams init = new SyncParams();
    private final SyncParams current = new SyncParams();

    public SyncLoop(SyncMethods syncMethods) {
        this.syncMethods = syncMethods;
    }

    protected SyncMethods getSyncMethods() {
        return syncMethods;
    }

    public BiFunction<SyncResponse, SyncParams, SyncParams> getInboundListener() {
        return inboundListener;
    }

    public void setInboundListener(BiFunction<SyncResponse, SyncParams, SyncParams> inboundListener) {
        this.inboundListener = inboundListener;
    }

    public SyncParams getInit() {
        return init;
    }

    public void setInit(SyncParams init) {
        this.init = init;
    }

    public SyncParams getCurrent() {
        return new SyncParams(current);
    }

    /**
     * Set a new sync params.
     *
     * @param newParams The new sync params.
     */
    public void setCurrent(SyncParams newParams) {
        if (newParams != null) {
            synchronized (current) {
                current.from(newParams);
            }
        }
    }

    @Override
    public void run() {
        Objects.requireNonNull(getSyncMethods(), "The Matrix client must be specified.");
        Objects.requireNonNull(getInboundListener(), "Not found inbound listeners, the sync will erase the response.");

        setCurrent(getInit());

        while (!Thread.interrupted()) {
            try {
                CompletableFuture<SyncResponse> future;
                synchronized (current) {
                    future = getSyncMethods().sync(
                        current.getFilter(),
                        current.getNextBatch(),
                        current.isFullState(),
                        current.getPresence(),
                        current.getTimeout()
                    );
                }
                SyncResponse sync = future.get(2 * current.getTimeout(), TimeUnit.MILLISECONDS);

                synchronized (current) {
                    current.setNextBatch(sync.getNextBatch());
                }

                setCurrent(getInboundListener().apply(sync, new SyncParams(current)));
            } catch (TimeoutException e) {
                LOGGER.error("Timeout exceeded", e);
            } catch (InterruptedException e) {
                LOGGER.warn("Interrupted", e);
                return;
            } catch (Exception e) {
                LOGGER.error("Exception: ", e);
            }
        }
    }
}
