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

package io.github.ma1uta.homeserver.dao;

import io.github.ma1uta.homeserver.model.Device;

/**
 * DAO for device models.
 */
public interface DeviceDao {

    /**
     * Find by token.
     *
     * @param token access token.
     * @return device or null.
     */
    Device findByToken(String token);

    /**
     * Delete token.
     *
     * @param device device which token should be deleted.
     */
    void deleteToken(Device device);

    /**
     * Insert of update device if exist.
     *
     * @param device device.
     */
    void insertOrUpdate(Device device);

    /**
     * Update last seen ip and ts.
     *
     * @param device device.
     */
    void updateLastSeen(Device device);
}
