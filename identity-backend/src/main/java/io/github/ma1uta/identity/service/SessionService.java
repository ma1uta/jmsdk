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

import io.github.ma1uta.identity.model.Session;

/**
 * Service to create, verify, publish and cleanup sessions.
 */
public interface SessionService {

    /**
     * Create new session.
     *
     * @param clientSecret client secret.
     * @param medium       address type.
     * @param address      address.
     * @param nextLink     url to open.
     * @param sendAttempt  attempt
     * @return session id.
     */
    String create(String clientSecret, String medium, String address, Long sendAttempt, String nextLink);

    /**
     * Validate existing session.
     *
     * @param token        validation token.
     * @param clientSecret client secret.
     * @param sid          session id.
     * @return url to redirect if exist and session has been created.
     */
    String validate(String token, String clientSecret, String sid);

    /**
     * Find validated session.
     *
     * @param sid          session id.
     * @param clientSecret client secret.
     * @return validated session if exists.
     */
    Session getSession(String sid, String clientSecret);

    /**
     * Bind mxid and the 3pid.
     *
     * @param sid          session id.
     * @param mxid         matrix id.
     * @param clientSecret client secret.
     * @return {@code true} if publishing has been completed.
     */
    boolean publish(String sid, String clientSecret, String mxid);

    /**
     * Delete expired sessions.
     */
    void cleanup();
}
