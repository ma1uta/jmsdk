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

package io.github.ma1uta.identity.service.impl;

import io.github.ma1uta.identity.configuration.KeyServiceConfiguration;
import io.github.ma1uta.identity.key.KeyProvider;
import io.github.ma1uta.identity.service.KeyService;
import io.github.ma1uta.jeon.exception.MatrixException;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Default implementation.
 * <p/>
 * There are default implementation for all methods of the {@link KeyService}.
 * <p/>
 * You can create you own class for example with annotation @Transactional (jpa) or another and invoke the same method
 * with suffix "Internal".
 * <pre>
 * {@code
 *     @literal @Service
 *     public class MyKeyService extends AbstractKeyService {
 *         ...
 *         @literal @Override
 *         @literal @Transactional
 *         @literal @MyFavouriteAnnotation
 *         public String init() {
 *             // wrap next link to transaction via annotation or code.
 *             return super.initInternal();
 *         }
 *         ...
 *     }
 * }
 * </pre>
 */
public abstract class AbstractKeyService implements KeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyService.class);

    /**
     * Error message when cannot find the key.
     */
    public static final String M_MISSING_KEY = "M_MISSING_KEY";

    /**
     * Long-term keys.
     */
    private final KeyProvider longTermProvider;

    /**
     * Short-term keys.
     */
    private final KeyProvider shortTermProvider;

    /**
     * Service configuration.
     */
    private final KeyServiceConfiguration configuration;

    public AbstractKeyService(KeyProvider longTermProvider, KeyProvider shortTermProvider, KeyServiceConfiguration configuration) {
        this.longTermProvider = longTermProvider;
        this.shortTermProvider = shortTermProvider;
        this.configuration = configuration;
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#init()}
     */
    protected void initInternal() {
        this.longTermProvider.init();
        this.shortTermProvider.init();
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#key(String)}
     */
    protected Optional<Certificate> keyInternal(String key) {
        return getLongTermProvider().key(key);
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#validLongTerm(String)}
     */
    protected boolean validLongTermInternal(String publicKey) {
        return getLongTermProvider().valid(publicKey);
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#validShortTerm(String)}
     */
    protected boolean validShortTermInternal(String publicKey) {
        return getShortTermProvider().valid(publicKey);
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#sign(String)}
     */
    protected Map<String, Map<String, String>> signInternal(String content) {
        Pair<String, String> pair = getLongTermProvider().sign(content);
        Map<String, String> pairMap = new HashMap<>();
        pairMap.put(pair.getKey(), pair.getValue());
        Map<String, Map<String, String>> result = new HashMap<>();
        result.put(getConfiguration().getHostname(), pairMap);
        return result;
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#retrieveLongTermKey()}
     *
     * @return key alias.
     * @throws MatrixException if cannot find long-term key or key store isn't initialized.
     */
    protected String retrieveLongTermKeyInternal() {
        return getLongTermProvider().retrieveKey()
            .orElseThrow(() -> new MatrixException(MatrixException.M_INTERNAL, "Cannot find long-term key."));
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#generateShortTermKey()}
     *
     * @return key alias.
     * @throws MatrixException if key store isn't initialized.
     */
    protected String generateShortTermKeyInternal() {
        return getShortTermProvider().generateNewKey();
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#generateLongTermKey()}
     */
    protected void generateLongTermKeyInternal() {
        getLongTermProvider().generateNewKey();
    }

    /**
     * Default implementation.
     * <p/>
     * {@link KeyService#cleanKeyStores()}
     */
    protected void cleanKeyStoresInternal() {
        // long-term keys clean before regenerating.
        getShortTermProvider().clean();
    }

    protected KeyServiceConfiguration getConfiguration() {
        return configuration;
    }

    protected KeyProvider getLongTermProvider() {
        return longTermProvider;
    }

    protected KeyProvider getShortTermProvider() {
        return shortTermProvider;
    }
}
