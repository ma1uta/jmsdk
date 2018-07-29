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

import io.github.ma1uta.jeon.exception.MatrixException;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.regex.Matcher;

/**
 * Key provider implementation with the locks.
 */
public abstract class AbstractKeyProvider implements KeyProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractKeyProvider.class);

    private static final long TIMEOUT = 10 * 1000;

    protected final StoreHelper storeHelper;
    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    protected final AtomicInteger readBarrier = new AtomicInteger(0);

    private static final Object MONITOR = new Object();

    private final KeyGenerator keyGenerator;

    public AbstractKeyProvider(String secureRandomSeed, KeyGenerator keyGenerator) {
        this.storeHelper = new StoreHelper(secureRandomSeed);
        this.keyGenerator = keyGenerator;
    }

    protected long maxId(long maxId, Enumeration<String> aliases) {
        while (aliases.hasMoreElements()) {
            String id = aliases.nextElement();
            Matcher matcher = StoreHelper.KEY_PATTERN.matcher(id);
            if (matcher.matches()) {
                maxId = Math.max(maxId, Long.parseLong(matcher.group(2)));
            }
        }
        return maxId;
    }

    protected void generateKey(String keyId) {
        try {
            Pair<KeyPair, Certificate> keyPairCertificate = keyGenerator.generate();
            addKey(keyId, keyPairCertificate.getLeft(), keyPairCertificate.getRight());
        } catch (NoSuchAlgorithmException | OperatorCreationException | IOException | CertificateException e) {
            String msg = "Failed to create new key";
            LOGGER.error(msg, e);
            throw new MatrixException(MatrixException.M_INTERNAL, msg);
        }
    }

    protected StoreHelper getStoreHelper() {
        return storeHelper;
    }

    protected <T> T readLock(Supplier<T> action) {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try {
            while (readBarrier.get() > 0) {
                try {
                    readBarrier.wait(TIMEOUT);
                } catch (InterruptedException e) {
                    String msg = "Cannot acquire lock.";
                    LOGGER.error(msg, e);
                    throw new MatrixException(MatrixException.M_INTERNAL, msg);
                }
            }
            readLock.lock();
            return action.get();
        } finally {
            readLock.unlock();
        }
    }

    protected <T> T writeLock(Supplier<T> action) {
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try {
            readBarrier.incrementAndGet();
            writeLock.lock();
            return action.get();
        } finally {
            writeLock.unlock();
            synchronized (MONITOR) {
                readBarrier.decrementAndGet();
                readBarrier.notifyAll();
            }
        }
    }
}
