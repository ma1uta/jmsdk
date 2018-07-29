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

import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

/**
 * Service to create invitation and sent invites when session verification completed.
 */
public interface InvitationService {
    /**
     * Store invitation.
     *
     * @param medium  'email' or 'msisdn'.
     * @param address email or phone number.
     * @param roomId  roomId to invite.
     * @param sender  who send invite.
     * @return triple of the invitation (display_name, token, [ephemeral_key, long_term_key]).
     */
    Triple<String, String, List<String>> create(String address, String medium, String roomId, String sender);

    /**
     * Send invite to the user's home server.
     *
     * @param medium  'email' or 'msisdn'.
     * @param address email or phone number.
     * @param mxid    matrix id.
     */
    void sendInvite(String address, String medium, String mxid);
}
