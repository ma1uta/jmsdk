/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

package io.github.ma1uta.matrix.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.Certificate;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;

public class HomeServerVerifier implements HostnameVerifier {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeServerVerifier.class);
    private static final String ALT_DNS_NAME_TYPE = "2";
    private static final String ALT_IP_ADDRESS_TYPE = "7";

    private final String domain;

    public HomeServerVerifier(String domain) {
        this.domain = domain;
    }

    @Override
    public boolean verify(String hostname, SSLSession session) {
        try {
            Certificate peerCertificate = session.getPeerCertificates()[0];
            if (peerCertificate instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) peerCertificate;
                LOGGER.trace("Check certificate: {}", x509Certificate);
                if (x509Certificate.getSubjectAlternativeNames() == null) {
                    LOGGER.trace("Empty subject alternative names");
                    return false;
                }
                if (hasMatchedSubjectNames(x509Certificate)) {
                    return true;
                }
            } else {
                LOGGER.trace("Unknown certificate type \"{}\", ignore", peerCertificate.getType());
            }
        } catch (SSLPeerUnverifiedException | CertificateParsingException e) {
            LOGGER.error("Unable to check remote host", e);
            return false;
        }

        return false;
    }

    private boolean hasMatchedSubjectNames(X509Certificate x509Certificate) {
        try {
            for (List<?> subjectAlternativeNames : x509Certificate.getSubjectAlternativeNames()) {
                if (subjectAlternativeNames == null
                    || subjectAlternativeNames.size() < 2
                    || subjectAlternativeNames.get(0) == null
                    || subjectAlternativeNames.get(1) == null) {
                    continue;
                }
                String subjectType = subjectAlternativeNames.get(0).toString();
                switch (subjectType) {
                    case ALT_DNS_NAME_TYPE:
                    case ALT_IP_ADDRESS_TYPE:
                        String altSubjectName = subjectAlternativeNames.get(1).toString();
                        LOGGER.trace("Found subject name: {}", altSubjectName);
                        if (match(altSubjectName)) {
                            LOGGER.trace("Matched");
                            return true;
                        }
                        break;
                    default:
                        LOGGER.trace("Unusable subject type: " + subjectType);
                }
            }
        } catch (CertificateParsingException e) {
            LOGGER.error("Unable to parse the certificate", e);
            return false;
        }
        LOGGER.trace("Haven't found matched subject names");
        return false;
    }

    private boolean match(String altSubjectName) {
        if (altSubjectName.startsWith("*.")) {
            String subjectNameWithoutMask = altSubjectName.substring(1); // remove wildcard
            LOGGER.trace("Check the origin domain \"{}\" with the wildcard \"{}\"", domain, altSubjectName);
            return domain.toLowerCase().endsWith(subjectNameWithoutMask.toLowerCase());
        } else {
            LOGGER.trace("Compare the origin domain \"{}\" with the subject name: \"{}\"", domain, altSubjectName);
            return domain.equalsIgnoreCase(altSubjectName);
        }
    }
}
