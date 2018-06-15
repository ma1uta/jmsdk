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
 * Key service configuration.
 */
public class KeyServiceConfiguration {

    /**
     * Long-term keys configuration.
     */
    private KeyStoreConfiguration longTerm;

    /**
     * Used short-term keys configuration.
     */
    private KeyStoreConfiguration usedShortTerm;

    /**
     * Identity server hostname.
     */
    private String hostname;

    public KeyStoreConfiguration getLongTerm() {
        return longTerm;
    }

    public void setLongTerm(KeyStoreConfiguration longTerm) {
        this.longTerm = longTerm;
    }

    public KeyStoreConfiguration getUsedShortTerm() {
        return usedShortTerm;
    }

    public void setUsedShortTerm(KeyStoreConfiguration usedShortTerm) {
        this.usedShortTerm = usedShortTerm;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }
}
