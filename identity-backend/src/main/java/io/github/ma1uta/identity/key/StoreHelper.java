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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulation of the keystore actions.
 */
public class StoreHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(StoreHelper.class);

    /**
     * All keys have format: [algorithm]:[alias].
     */
    public static final Pattern KEY_PATTERN = Pattern.compile("(\\w+):(\\w+)");

    /**
     * Used to sign content.
     */
    private final SecureRandom secureRandom;

    public StoreHelper(String secureRandomSeed) {
        Objects.requireNonNull(secureRandomSeed, "Secure random seed must be specified");
        this.secureRandom = new SecureRandom(secureRandomSeed.getBytes(StandardCharsets.UTF_8));
    }

    public SecureRandom getSecureRandom() {
        return secureRandom;
    }

    /**
     * Initialize key store provider.
     *
     * @param configuration key storage configuration.
     * @return initialized key store.
     */
    public KeyStore init(KeyStoreConfiguration configuration) {
        try {
            KeyStore keyStore = KeyStore.getInstance(configuration.getKeyStoreType());
            try (InputStream inputStream = Files.newInputStream(Paths.get(configuration.getKeyStore()))) {
                keyStore.load(inputStream, configuration.getKeyStorePassword().toCharArray());
            }
            return keyStore;
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            String msg = "Key store initialization failed.";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
    }

    /**
     * Retrieve next the unused key.
     *
     * @param keyStore key storage.
     * @return key or empty.
     */
    public Optional<String> firstKey(KeyStore keyStore) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            return aliases.hasMoreElements() ? Optional.of(aliases.nextElement()) : Optional.empty();
        } catch (KeyStoreException e) {
            String msg = "Key store isn't initialized";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
    }

    /**
     * Retrieve a pair of the alias and the certificate by the key id.
     *
     * @param key      the key id.
     * @param keyStore key storage.
     * @return the certificate of this key.
     */
    public Optional<Certificate> key(String key, KeyStore keyStore) {
        Matcher matcher = KEY_PATTERN.matcher(key.trim());
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Wrong key: %s", key));
        }

        String algorithm = matcher.group(1);
        Certificate certificate;
        try {
            certificate = keyStore.getCertificate(key);
        } catch (KeyStoreException e) {
            String msg = "Key store isn't initialized";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
        if (certificate == null) {
            return Optional.empty();
        }
        if (!"EdDSA".equals(certificate.getPublicKey().getAlgorithm()) || !"Ed25519".equals(algorithm)) {
            throw new IllegalArgumentException(String.format("Wrong key: %s", key));
        }

        return Optional.of(certificate);
    }

    /**
     * Verify the public key.
     *
     * @param publicKey the public key.
     * @param keyStore  key storage.
     * @return {@code true} is valid else {@code false}.
     */
    public boolean valid(String publicKey, KeyStore keyStore) {
        try {
            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                Certificate certificate = keyStore.getCertificate(aliases.nextElement());
                if (Base64.getEncoder().withoutPadding().encodeToString(certificate.getPublicKey().getEncoded()).equals(publicKey)) {
                    return true;
                }
            }
            return false;
        } catch (KeyStoreException e) {
            String msg = "Key store isn't initialized";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
    }

    /**
     * Add a new key to the key store.
     *
     * @param key           alias of the a new key.
     * @param keyPair       the pair of the new key (public and private).
     * @param certificate   a certificate of the new key.
     * @param keyStore      key storage.
     * @param configuration key storage configuration.
     * @return key storage with added key.
     */
    public KeyStore addKey(String key, KeyPair keyPair, Certificate certificate, KeyStore keyStore,
                           KeyStoreConfiguration configuration) {
        try {
            keyStore.setKeyEntry(key, keyPair.getPrivate(), configuration.getKeyPassword().toCharArray(), new Certificate[] {certificate});
            store(keyStore, configuration);
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            String msg = "Key store isn't initialized or missing algorithm or certificate is invalid or I/O error with key store.";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
        return init(configuration);
    }

    /**
     * Store key storage into the file.
     *
     * @param keyStore      key storage.
     * @param configuration key storage configuration.
     * @throws KeyStoreException        if the keystore has not been initialized (loaded).
     * @throws IOException              if there was an I/O problem with data
     * @throws NoSuchAlgorithmException if the appropriate data integrity algorithm could not be found
     * @throws CertificateException     if any of the certificates included in the keystore data could not be stored
     */
    public void store(KeyStore keyStore, KeyStoreConfiguration configuration) throws IOException, CertificateException,
        NoSuchAlgorithmException, KeyStoreException {
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(configuration.getKeyStore()))) {
            keyStore.store(outputStream, configuration.getKeyStorePassword().toCharArray());
        }
    }

    /**
     * Sign the content.
     *
     * @param alias         the key alias.
     * @param content       the content to sign.
     * @param keyStore      key storage.
     * @param configuration key storage configuration.
     * @return the pair of the key alias and the signature.
     */
    public Pair<String, String> sign(String alias, String content, KeyStore keyStore, KeyStoreConfiguration configuration) {
        try {
            Signature signature = Signature.getInstance("Ed25519");
            signature.initSign((PrivateKey) keyStore.getKey(alias, configuration.getKeyPassword().toCharArray()), getSecureRandom());
            signature.update(content.getBytes(StandardCharsets.UTF_8));
            return new ImmutablePair<>(alias, new String(signature.sign(), StandardCharsets.UTF_8));
        } catch (UnrecoverableKeyException | NoSuchAlgorithmException | KeyStoreException | InvalidKeyException | SignatureException e) {
            String msg = "Key store isn't initialized or invalid key, certificate or algorithm";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
    }
}
