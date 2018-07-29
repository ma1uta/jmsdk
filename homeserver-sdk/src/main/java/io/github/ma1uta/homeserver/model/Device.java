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

package io.github.ma1uta.homeserver.model;

import java.util.Objects;

/**
 * Device.
 */
public class Device {

    /**
     * Device id.
     */
    private String deviceId;

    /**
     * Authentication token.
     */
    private String token;

    /**
     * Owner of this device.
     */
    private User user;

    /**
     * Display name.
     */
    private String displayName;

    /**
     * Last seen ip address.
     */
    private String lastSeenIp;

    /**
     * Last seen timestamp.
     */
    private Long lastSeenTs;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLastSeenIp() {
        return lastSeenIp;
    }

    public void setLastSeenIp(String lastSeenIp) {
        this.lastSeenIp = lastSeenIp;
    }

    public Long getLastSeenTs() {
        return lastSeenTs;
    }

    public void setLastSeenTs(Long lastSeenTs) {
        this.lastSeenTs = lastSeenTs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Device device = (Device) o;
        return Objects.equals(deviceId, device.deviceId) && Objects.equals(user, device.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deviceId, user);
    }
}
