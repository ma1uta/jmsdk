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

import io.github.ma1uta.matrix.client.model.serverdiscovery.HomeserverInfo;
import io.github.ma1uta.matrix.client.model.serverdiscovery.IdentityServerInfo;
import io.github.ma1uta.matrix.client.model.serverdiscovery.ServerDiscoveryResponse;

/**
 * Connection info.
 */
public class ConnectionInfo {

    private String domain;

    private String userId;

    private String accessToken;

    private String deviceId;

    private ServerDiscoveryResponse serverInfo;

    public ConnectionInfo() {
    }

    public ConnectionInfo(String domain) {
        this.domain = domain;
    }

    public ConnectionInfo(ConnectionInfo connectionInfo) {
        this.domain = connectionInfo.getDomain();
        this.userId = connectionInfo.getUserId();
        this.accessToken = connectionInfo.getAccessToken();
        this.deviceId = connectionInfo.getDeviceId();
        if (connectionInfo.getServerInfo() != null) {
            this.serverInfo = new ServerDiscoveryResponse();
            if (connectionInfo.getServerInfo().getHomeserver() != null) {
                this.serverInfo.setHomeserver(new HomeserverInfo());
                this.serverInfo.getHomeserver().setBaseUrl(connectionInfo.getServerInfo().getHomeserver().getBaseUrl());
            }
            if (connectionInfo.getServerInfo().getIdentityServer() != null) {
                this.serverInfo.setIdentityServer(new IdentityServerInfo());
                this.serverInfo.getIdentityServer().setBaseUrl(connectionInfo.getServerInfo().getIdentityServer().getBaseUrl());
            }
        }
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public ServerDiscoveryResponse getServerInfo() {
        return serverInfo;
    }

    public void setServerInfo(ServerDiscoveryResponse serverInfo) {
        this.serverInfo = serverInfo;
    }
}
