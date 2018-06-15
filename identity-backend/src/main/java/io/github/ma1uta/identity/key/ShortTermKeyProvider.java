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

package io.github.ma1uta.identity.key;

import io.github.ma1uta.identity.configuration.KeyStoreConfiguration;
import io.github.ma1uta.jeon.exception.MatrixException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.Enumeration;
import java.util.Optional;

/**
 * Key store provider for the short-term keys.
 * <p/>
 * After post next available key, this key moved to another key store.
 */
public class ShortTermKeyProvider extends AbstractKeyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShortTermKeyProvider.class);

    /**
     * Keystore for used keys configuration.
     */
    private final KeyStoreConfiguration usedKeyStoreConfiguration;

    /**
     * Storage for used keys.
     */
    private KeyStore usedKeyStore;

    public ShortTermKeyProvider(KeyStoreConfiguration usedKeyStoreConfiguration, String secureRandomSeed, KeyGenerator keyGenerator) {
        super(secureRandomSeed, keyGenerator);
        this.usedKeyStoreConfiguration = usedKeyStoreConfiguration;
    }

    public KeyStoreConfiguration getUsedKeyStoreConfiguration() {
        return usedKeyStoreConfiguration;
    }

    public KeyStore getUsedKeyStore() {
        return usedKeyStore;
    }

    @Override
    public void init() {
        writeLock(() -> {
            this.usedKeyStore = getStoreHelper().init(getUsedKeyStoreConfiguration());
            return null;
        });
    }

    @Override
    public String generateNewKey() {
        return writeLock(() -> {
            long maxId = 0;
            try {
                Enumeration<String> aliases = getUsedKeyStore().aliases();
                maxId = maxId(maxId, aliases);
            } catch (KeyStoreException e) {
                String msg = "Key store is not initialized.";
                LOGGER.error(msg, e);
                throw new MatrixException(MatrixException.M_INTERNAL, msg);
            }
            String keyId = "Ed25519:" + Long.toString(maxId);
            generateKey(keyId);
            return keyId;
        });
    }

    @Override
    public Optional<String> retrieveKey() {
        return Optional.empty();
    }

    @Override
    public boolean valid(String publicKey) {
        return readLock(() -> getStoreHelper().valid(publicKey, getUsedKeyStore()));
    }

    @Override
    public void addKey(String key, KeyPair keyPair, Certificate certificate) {
        writeLock(() -> {
            this.usedKeyStore = getStoreHelper().addKey(key, keyPair, certificate, getUsedKeyStore(), getUsedKeyStoreConfiguration());
            return null;
        });
    }

    @Override
    public Pair<String, String> sign(String content) {
        return readLock(() -> getStoreHelper().sign(generateNewKey(), content, getUsedKeyStore(), getUsedKeyStoreConfiguration()));
    }

    @Override
    public Optional<Certificate> key(String key) {
        return readLock(() -> getStoreHelper().key(key, getUsedKeyStore()));
    }

    @Override
    public void clean() {
        writeLock(() -> {
            try {
                Enumeration<String> aliases = getUsedKeyStore().aliases();
                while (aliases.hasMoreElements()) {
                    getUsedKeyStore().deleteEntry(aliases.nextElement());
                }
            } catch (KeyStoreException e) {
                String msg = "Failed clean key store, key store isn't initialized";
                LOGGER.error(msg, e);
                throw new MatrixException(MatrixException.M_INTERNAL, msg);
            }
            return null;
        });
    }
}
