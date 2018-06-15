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

package io.github.ma1uta.identity.configuration;

/**
 * Configuration of the invitation service.
 */
public class InvitationServiceConfiguration {

    /**
     * Url of the bind action.
     */
    private String onBindUrl;

    /**
     * Protocol of the bind action.
     */
    private String onBindProtocol;

    /**
     * Port of the bind action.
     */
    private String onBindPort;

    public String getOnBindUrl() {
        return onBindUrl;
    }

    public void setOnBindUrl(String onBindUrl) {
        this.onBindUrl = onBindUrl;
    }

    public String getOnBindProtocol() {
        return onBindProtocol;
    }

    public void setOnBindProtocol(String onBindProtocol) {
        this.onBindProtocol = onBindProtocol;
    }

    public String getOnBindPort() {
        return onBindPort;
    }

    public void setOnBindPort(String onBindPort) {
        this.onBindPort = onBindPort;
    }
}
