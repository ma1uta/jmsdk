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

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Persistent operations with bot data.
 * <p/>
 *
 * @param <D> Dao type.
 */
public class PersistentService<D> {

    private final D dao;

    public PersistentService(D dao) {
        this.dao = dao;
    }

    public D getDao() {
        return dao;
    }

    /**
     * Execute within transaction with result.
     *
     * @param action action.
     * @param <R>    result's type.
     * @return result.
     */
    public <R> R invoke(Function<D, R> action) {
        return action.apply(getDao());
    }

    /**
     * Execute within transaction without result.
     *
     * @param action action.
     */
    public void invoke(Consumer<D> action) {
        action.accept(getDao());
    }
}
