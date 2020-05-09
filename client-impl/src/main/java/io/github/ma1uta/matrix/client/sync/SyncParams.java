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

/**
 * Parameters for the sync operation.
 */
public class SyncParams {

    private String filter;

    private String nextBatch;

    private boolean fullState = false;

    private Long timeout;

    private String presence;

    private boolean terminate = false;

    public SyncParams() {
    }

    public SyncParams(SyncParams origin) {
        this.filter = origin.getFilter();
        this.nextBatch = origin.getNextBatch();
        this.fullState = origin.isFullState();
        this.timeout = origin.getTimeout();
        this.presence = origin.getPresence();
    }

    public SyncParams(String filter, String nextBatch, boolean fullState, Long timeout, String presence) {
        this.filter = filter;
        this.nextBatch = nextBatch;
        this.fullState = fullState;
        this.timeout = timeout;
        this.presence = presence;
    }

    /**
     * Copy parameters from the specified object.
     *
     * @param syncParams The object to copy parameters from.
     */
    public void from(SyncParams syncParams) {
        setFilter(syncParams.getFilter());
        setNextBatch(syncParams.getNextBatch());
        setFullState(syncParams.isFullState());
        setTimeout(syncParams.getTimeout());
        setPresence(syncParams.getPresence());
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getNextBatch() {
        return nextBatch;
    }

    public void setNextBatch(String nextBatch) {
        this.nextBatch = nextBatch;
    }

    public boolean isFullState() {
        return fullState;
    }

    public void setFullState(boolean fullState) {
        this.fullState = fullState;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getPresence() {
        return presence;
    }

    public void setPresence(String presence) {
        this.presence = presence;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    /**
     * Create a new builder.
     *
     * @return The new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String filter;

        private String nextBatch;

        private boolean fullState;

        private Long timeout;

        private String presence;

        public Builder filter(String filter) {
            this.filter = filter;
            return this;
        }

        public Builder nextBatch(String nextBatch) {
            this.nextBatch = nextBatch;
            return this;
        }

        public Builder fullState(boolean fullState) {
            this.fullState = fullState;
            return this;
        }

        public Builder timeout(Long timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder presence(String presence) {
            this.presence = presence;
            return this;
        }

        public SyncParams build() {
            return new SyncParams(filter, nextBatch, fullState, timeout, presence);
        }
    }
}
