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
 * Configuration for selfKey generator.
 */
public class SelfKeyGeneratorConfiguration {

    /**
     * Default time-to-live of the certificate, 3 month.
     */
    public static final long DEFAULT_CERTIFICATE_TS = 60L * 60L * 24L * 30L * 3L;

    /**
     * Default serial number length.
     */
    public static final int DEFAULT_SERIAL_NUMBER_LENGTH = 20;

    /**
     * Issuer name.
     */
    private String issuerName = "jidentity";

    /**
     * Serial number length.
     */
    private int serialNumberLength = DEFAULT_SERIAL_NUMBER_LENGTH;

    /**
     * Certificate time-to-live.
     */
    private long certificateValidTs = DEFAULT_CERTIFICATE_TS;

    /**
     * Random seed.
     */
    private String secureRandomSeed;

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public int getSerialNumberLength() {
        return serialNumberLength;
    }

    public void setSerialNumberLength(int serialNumberLength) {
        this.serialNumberLength = serialNumberLength;
    }

    public long getCertificateValidTs() {
        return certificateValidTs;
    }

    public void setCertificateValidTs(long certificateValidTs) {
        this.certificateValidTs = certificateValidTs;
    }

    public String getSecureRandomSeed() {
        return secureRandomSeed;
    }

    public void setSecureRandomSeed(String secureRandomSeed) {
        this.secureRandomSeed = secureRandomSeed;
    }
}
