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

package io.github.ma1uta.matrix.bot;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Matrix bot persistent configuration.
 */
@MappedSuperclass
public class BotConfig {

    /**
     * Default timeout.
     * <p/>
     * 10 second in milliseconds.
     */
    public static final long TIMEOUT = 10L * 1000L;

    /**
     * Bot's unique id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    /**
     * Bot's name.
     */
    @Column(name = "user_id")
    private String userId;

    /**
     * Bot's password.
     */
    private String password;

    /**
     * Bot's device id.
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * Bot's display name.
     */
    @Column(name = "display_name")
    private String displayName;

    /**
     * Bot's filter to query only m.room.message events.
     */
    @Column(name = "filter_id")
    private String filterId;

    /**
     * Bot's batch to prevent process old events.
     */
    @Column(name = "next_batch")
    private String nextBatch;

    /**
     * Transaction id.
     */
    @Column(name = "txn_id")
    private Long txnId = 0L;

    /**
     * Bot's owner.
     */
    private String owner;

    /**
     * State.
     */
    @Enumerated(EnumType.STRING)
    private BotState state = BotState.NEW;

    /**
     * Who can invoke commands.
     */
    @Enumerated(EnumType.STRING)
    private AccessPolicy policy = AccessPolicy.ALL;

    /**
     * Timeout for long-polling waiting.
     */
    private Long timeout = TIMEOUT;

    /**
     * Command prefix.
     */
    private String prefix = "!";

    /**
     * Default command to use without prefix.
     */
    private String defaultCommand;

    /**
     * Skip initial sync.
     */
    private Boolean skipInitialSync;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFilterId() {
        return filterId;
    }

    public void setFilterId(String filterId) {
        this.filterId = filterId;
    }

    public String getNextBatch() {
        return nextBatch;
    }

    public void setNextBatch(String nextBatch) {
        this.nextBatch = nextBatch;
    }

    public Long getTxnId() {
        return txnId;
    }

    public void setTxnId(Long txnId) {
        this.txnId = txnId;
    }

    public BotState getState() {
        return state;
    }

    public void setState(BotState state) {
        this.state = state;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public AccessPolicy getPolicy() {
        return policy;
    }

    public void setPolicy(AccessPolicy policy) {
        this.policy = policy;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getDefaultCommand() {
        return defaultCommand;
    }

    public void setDefaultCommand(String defaultCommand) {
        this.defaultCommand = defaultCommand;
    }

    public Boolean getSkipInitialSync() {
        return skipInitialSync;
    }

    public void setSkipInitialSync(Boolean skipInitialSync) {
        this.skipInitialSync = skipInitialSync;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BotConfig that = (BotConfig) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
