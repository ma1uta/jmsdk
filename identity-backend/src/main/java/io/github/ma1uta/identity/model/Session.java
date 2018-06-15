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

package io.github.ma1uta.identity.model;

import java.time.LocalDateTime;

/**
 * Verification session.
 */
public class Session {

    /**
     * Session identifier.
     */
    private String sid;

    /**
     * Session token.
     */
    private String token;

    /**
     * Client secret.
     */
    private String clientSecret;

    /**
     * Address, email or msisdn.
     */
    private String address;

    /**
     * Address type, 'email' or 'msisdn'.
     */
    private String medium;

    /**
     * Send attempt.
     */
    private Long sendAttempt;

    /**
     * Link to redirect after verification complete.
     */
    private String nextLink;

    /**
     * Validation date.
     */
    private LocalDateTime validated;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public Long getSendAttempt() {
        return sendAttempt;
    }

    public void setSendAttempt(Long sendAttempt) {
        this.sendAttempt = sendAttempt;
    }

    public String getNextLink() {
        return nextLink;
    }

    public void setNextLink(String nextLink) {
        this.nextLink = nextLink;
    }

    public LocalDateTime getValidated() {
        return validated;
    }

    public void setValidated(LocalDateTime validated) {
        this.validated = validated;
    }
}
