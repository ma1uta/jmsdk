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

import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.operator.OperatorCreationException;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

/**
 * Generator new key pairs.
 */
public interface KeyGenerator {

    /**
     * Generate new key pair with certificate to store them in the key store.
     *
     * @return a pair (keypair, certificate)
     * @throws NoSuchAlgorithmException  if algorithm 'Ed25519' is missing.
     * @throws IOException               if there are I/O exception with the key store.
     * @throws CertificateException      if certificate is invalid.
     * @throws OperatorCreationException if cannot create issuer object.
     */
    Pair<KeyPair, Certificate> generate() throws NoSuchAlgorithmException, OperatorCreationException, IOException, CertificateException;
}
