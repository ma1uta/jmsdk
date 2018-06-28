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

import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Runnable to receive events and other data from the server.
 */
public class SyncLoop implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncLoop.class);

    private final MatrixClient matrixClient;

    private Consumer<SyncResponse> inboundListener;
    private String filter;
    private String startBatch;
    private boolean fullState;
    private String presence;
    private Long timeout;

    public SyncLoop(MatrixClient matrixClient) {
        this.matrixClient = matrixClient;
    }

    public MatrixClient getMatrixClient() {
        return matrixClient;
    }

    public Consumer<SyncResponse> getInboundListener() {
        return inboundListener;
    }

    public void setInboundListener(Consumer<SyncResponse> inboundListener) {
        this.inboundListener = inboundListener;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getStartBatch() {
        return startBatch;
    }

    public void setStartBatch(String startBatch) {
        this.startBatch = startBatch;
    }

    public boolean isFullState() {
        return fullState;
    }

    public void setFullState(boolean fullState) {
        this.fullState = fullState;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public void run() {

        String nextBatch = getStartBatch();

        while (true) {
            try {
                SyncResponse sync = getMatrixClient().sync().sync(getFilter(), nextBatch, isFullState(), getPresence(), getTimeout());

                nextBatch = sync.getNextBatch();
                getInboundListener().accept(sync);

                if (Thread.currentThread().isInterrupted()) {
                    break;
                }
            } catch (Exception e) {
                LOGGER.error("Exception: ", e);
            }
        }
    }
}
