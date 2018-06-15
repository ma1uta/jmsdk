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

package io.github.ma1uta.identity.service;

import java.security.cert.Certificate;
import java.util.Map;
import java.util.Optional;

/**
 * Service to manipulate keys.
 * <p/>
 * Used to store keys, create new keys and sign some string.
 */
public interface KeyService {
    /**
     * Long and short keys provider initialization.
     */
    void init();

    /**
     * Find certificate by key.
     *
     * @param key key id.
     * @return certificate of this key.
     */
    Optional<Certificate> key(String key);

    /**
     * Check if the long-term public key is valid.
     *
     * @param publicKey public key.
     * @return {@code true} if key valid, else {@code false}.
     */
    boolean validLongTerm(String publicKey);

    /**
     * Check if the short-term public key is valid.
     *
     * @param publicKey public key.
     * @return {@code true} if key valid, else {@code false}.
     */
    boolean validShortTerm(String publicKey);

    /**
     * Sign content.
     *
     * @param content content.
     * @return map { "hostname" -> { "key": "signature" } }
     */
    Map<String, Map<String, String>> sign(String content);

    /**
     * Retrieve long-term key.
     *
     * @return alias of the next free key.
     */
    String retrieveLongTermKey();

    /**
     * Create new short-term key.
     *
     * @return alias of the new short-term key.
     */
    String generateShortTermKey();

    /**
     * Create new long-term key. Old key will be removed;
     */
    void generateLongTermKey();

    /**
     * Clean both keystores.
     */
    void cleanKeyStores();
}
