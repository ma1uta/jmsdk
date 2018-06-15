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

package io.github.ma1uta.matrix.bot;

import java.util.List;

/**
 * DAO of the bot configuration.
 *
 * @param <C> class of the configuration.
 */
public interface BotDao<C extends BotConfig> {
    /**
     * Find all matrix bots.
     *
     * @return matrix bots.
     */
    List<C> findAll();

    /**
     * Check that specified user exists.
     *
     * @param userId mxid.
     * @return {@code true} if user exists or {@code false}.
     */
    boolean user(String userId);

    /**
     * Save new bot's data.
     *
     * @param data bot's data.
     * @return saved entity.
     */
    C save(C data);

    /**
     * Delete bot's data.
     *
     * @param data data to delete.
     */
    void delete(C data);
}
