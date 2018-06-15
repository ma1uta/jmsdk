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

package io.github.ma1uta.identity.api;

import io.github.ma1uta.identity.service.KeyService;
import io.github.ma1uta.identity.service.impl.AbstractKeyService;
import io.github.ma1uta.jeon.exception.MatrixException;
import io.github.ma1uta.matrix.ErrorResponse;
import io.github.ma1uta.matrix.identity.api.KeyManagementApi;
import io.github.ma1uta.matrix.identity.model.key.KeyValidationResponse;
import io.github.ma1uta.matrix.identity.model.key.PublicKeyResponse;
import org.apache.commons.lang3.StringUtils;

import java.security.cert.Certificate;
import java.util.Base64;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Implementation of the {@link KeyManagementApi}.
 */
public class KeyManagement implements KeyManagementApi {

    private final KeyService keyService;

    public KeyManagement(KeyService keyService) {
        this.keyService = keyService;
    }

    public KeyService getKeyService() {
        return keyService;
    }

    @Override
    public PublicKeyResponse get(String keyId, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (StringUtils.isBlank(keyId)) {
            throw new MatrixException(ErrorResponse.Code.M_NOT_FOUND, "Missing key.");
        }
        Certificate certificate = getKeyService().key(keyId)
            .orElseThrow(() -> new MatrixException(ErrorResponse.Code.M_NOT_FOUND, "Key not found", HttpServletResponse.SC_NOT_FOUND));
        PublicKeyResponse response = new PublicKeyResponse();
        response.setPublicKey(Base64.getEncoder().withoutPadding().encodeToString(certificate.getPublicKey().getEncoded()));
        return response;
    }

    @Override
    public KeyValidationResponse valid(String publicKey, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (StringUtils.isBlank(publicKey)) {
            throw new MatrixException(AbstractKeyService.M_MISSING_KEY, "Missing key.");
        }
        KeyValidationResponse response = new KeyValidationResponse();
        response.setValid(getKeyService().validLongTerm(publicKey));
        return response;
    }

    @Override
    public KeyValidationResponse ephemeralValid(String publicKey, HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        if (StringUtils.isBlank(publicKey)) {
            throw new MatrixException(AbstractKeyService.M_MISSING_KEY, "Missing key.");
        }
        KeyValidationResponse response = new KeyValidationResponse();
        response.setValid(getKeyService().validShortTerm(publicKey));
        return response;
    }
}
