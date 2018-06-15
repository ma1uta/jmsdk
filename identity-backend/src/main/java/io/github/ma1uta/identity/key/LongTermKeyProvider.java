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
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulation of the keystore actions.
 */
public class LongTermKeyProvider extends AbstractKeyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(LongTermKeyProvider.class);

    /**
     * Keystore configuration.
     */
    private final KeyStoreConfiguration keyStoreConfiguration;

    /**
     * Storage for keys.
     */
    private KeyStore keyStore;

    public LongTermKeyProvider(KeyStoreConfiguration keyStoreConfiguration, String secureRandomSeed, KeyGenerator keyGenerator) {
        super(secureRandomSeed, keyGenerator);
        this.keyStoreConfiguration = Objects.requireNonNull(keyStoreConfiguration, "Key store configuration shouldn't be empty");
    }

    public KeyStoreConfiguration getKeyStoreConfiguration() {
        return keyStoreConfiguration;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    @Override
    public void init() {
        writeLock(() -> {
            this.keyStore = getStoreHelper().init(getKeyStoreConfiguration());
            return null;
        });
    }

    @Override
    public String generateNewKey() {
        return writeLock(() -> {
            long maxId = 0;
            try {
                Enumeration<String> aliases = getKeyStore().aliases();
                maxId = maxId(maxId, aliases);
            } catch (KeyStoreException e) {
                String msg = "Key store is not initialized.";
                LOGGER.error(msg, e);
                throw new MatrixException(MatrixException.M_INTERNAL, msg);
            }
            try {
                Enumeration<String> aliases1 = getKeyStore().aliases();
                while (aliases1.hasMoreElements()) {
                    getKeyStore().deleteEntry(aliases1.nextElement());
                }
            } catch (KeyStoreException e) {
                String msg = "Key store is not initialized.";
                LOGGER.error(msg, e);
                throw new MatrixException(MatrixException.M_INTERNAL, msg);
            }
            String keyId = "Ed25519:" + Long.toString(maxId + 1);
            generateKey(keyId);
            return keyId;
        });
    }

    @Override
    public Optional<String> retrieveKey() {
        return readLock(() -> getStoreHelper().firstKey(getKeyStore()));
    }

    @Override
    public Optional<Certificate> key(String key) {
        return readLock(() -> getStoreHelper().key(key, getKeyStore()));
    }

    @Override
    public boolean valid(String publicKey) {
        return readLock(() -> getStoreHelper().valid(publicKey, getKeyStore()));
    }

    @Override
    public Pair<String, String> sign(String content) {
        return readLock(() -> getStoreHelper()
            .sign(retrieveKey().orElseThrow(() -> new MatrixException(MatrixException.M_INTERNAL, "Cannot find key")), content,
                getKeyStore(), getKeyStoreConfiguration()));
    }

    @Override
    public void addKey(String key, KeyPair keyPair, Certificate certificate) {
        writeLock(() -> {
            this.keyStore = getStoreHelper().addKey(key, keyPair, certificate, getKeyStore(), getKeyStoreConfiguration());
            return null;
        });
    }

    @Override
    public void clean() {
        writeLock(() -> {
            try {
                Enumeration<String> aliases = getKeyStore().aliases();
                while (aliases.hasMoreElements()) {
                    getKeyStore().deleteEntry(aliases.nextElement());
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
