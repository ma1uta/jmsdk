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

import io.github.ma1uta.identity.configuration.SelfKeyGeneratorConfiguration;
import net.i2p.crypto.eddsa.EdDSAEngine;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.gnu.GNUObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.OperatorStreamException;
import org.bouncycastle.operator.RuntimeOperatorException;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Generator for new keys.
 * <p/>
 * Also generate certificate for new key pair and store they in the key store.
 */
public class SelfCertificateKeyGenerator implements KeyGenerator {

    private final SelfKeyGeneratorConfiguration configuration;

    public SelfCertificateKeyGenerator(SelfKeyGeneratorConfiguration configuration) {
        this.configuration = configuration;
    }

    public SelfKeyGeneratorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Pair<KeyPair, Certificate> generate() throws NoSuchAlgorithmException,
        OperatorCreationException, IOException, CertificateException {
        KeyPairGenerator pairGenerator = KeyPairGenerator.getInstance("EdDSA");
        SecureRandom secureRandom = new SecureRandom(getConfiguration().getSecureRandomSeed().getBytes(StandardCharsets.UTF_8));

        Instant notBefore = Instant.now();
        Instant notAfter = notBefore.plus(Duration.ofSeconds(getConfiguration().getCertificateValidTs()));

        KeyPair keyPair = pairGenerator.generateKeyPair();
        EdDSAEngine engine = new EdDSAEngine();
        try {
            engine.initSign(keyPair.getPrivate());
        } catch (InvalidKeyException e) {
            throw new OperatorCreationException("cannot create signer: " + e.getMessage(), e);
        }
        ContentSigner signer = new ContentSigner() {

            private SignatureOutputStream stream = new SignatureOutputStream(engine);

            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(GNUObjectIdentifiers.Ed25519);
            }

            @Override
            public OutputStream getOutputStream() {
                return stream;
            }

            @Override
            public byte[] getSignature() {
                try {
                    return stream.getSignature();
                } catch (SignatureException e) {
                    throw new RuntimeOperatorException("exception obtaining signature: " + e.getMessage(), e);
                }
            }
        };

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.addProvider(provider);

        String issuer = String.format("CN=%s", getConfiguration().getIssuerName());
        JcaX509v3CertificateBuilder certificateBuilder = new JcaX509v3CertificateBuilder(
            new X500Name(issuer),
            new BigInteger(getConfiguration().getSerialNumberLength(), secureRandom),
            Date.from(notBefore),
            Date.from(notAfter),
            new X500Name(issuer),
            keyPair.getPublic()
        );

        certificateBuilder.addExtension(new ASN1ObjectIdentifier("2.5.29.19"), true, new BasicConstraints(false));

        X509Certificate certificate = new JcaX509CertificateConverter().setProvider(provider)
            .getCertificate(certificateBuilder.build(signer));

        return new ImmutablePair<>(keyPair, certificate);
    }

    private static final class SignatureOutputStream extends OutputStream {
        private Signature sig;

        private SignatureOutputStream(Signature sig) {
            this.sig = sig;
        }

        @Override
        public void write(byte[] bytes, int off, int len) throws IOException {
            try {
                sig.update(bytes, off, len);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override
        public void write(byte[] bytes) throws IOException {
            try {
                sig.update(bytes);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        @Override
        public void write(int bytes) throws IOException {
            try {
                sig.update((byte) bytes);
            } catch (SignatureException e) {
                throw new OperatorStreamException("exception in content signer: " + e.getMessage(), e);
            }
        }

        byte[] getSignature() throws SignatureException {
            return sig.sign();
        }
    }
}
