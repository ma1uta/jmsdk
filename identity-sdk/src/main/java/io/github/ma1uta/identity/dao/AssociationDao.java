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

package io.github.ma1uta.identity.dao;

import io.github.ma1uta.identity.model.Association;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Association dao contract.
 */
public interface AssociationDao {

    /**
     * Find associations by the address and the medium.
     *
     * @param address email or msisdn.
     * @param medium  'email' or 'msisdn'.
     * @return associations.
     */
    List<Association> findByAddressMedium(String address, String medium);

    /**
     * Persist new association or skip if already exist.
     *
     * @param address email or msisdm.
     * @param medium  'email' or 'msisdn'.
     * @param mxid    matrix id.
     * @param expired expired date.
     */
    void insertOrIgnore(String address, String medium, String mxid, LocalDateTime expired);

    /**
     * Check and remove all associations which are expired.
     */
    void expire();
}
