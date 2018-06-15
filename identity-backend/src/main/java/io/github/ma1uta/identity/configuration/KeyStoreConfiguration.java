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
 * Long-term and short-term keys are stored in the own keystore.
 * There is a configuration of the keystore.
 * <p>
 * Key store and key passwords are stored in String. This isn't safe, so don't use this code in the production.
 * </p>
 */
public class KeyStoreConfiguration {

    /**
     * Path to keystore.
     */
    private String keyStore;

    /**
     * Password of the keystore.
     */
    private String keyStorePassword;

    /**
     * Password of the key.
     */
    private String keyPassword;

    /**
     * Keystore type. Often os 'pkcs12'.
     */
    private String keyStoreType = "pkcs12";

    public String getKeyStore() {
        return keyStore;
    }

    public void setKeyStore(String keyStore) {
        this.keyStore = keyStore;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyPassword() {
        return keyPassword;
    }

    public void setKeyPassword(String keyPassword) {
        this.keyPassword = keyPassword;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }
}
