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

import io.github.ma1uta.matrix.client.MatrixClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Holder of the matrix client, bot configuration and some extra data.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 */
public class BotHolder<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> {

    private final Object monitor = new Object();

    private final MatrixClient matrixClient;

    private final S service;

    private C config;

    private E data;

    private List<Supplier<Void>> shutdownListeners = new ArrayList<>();

    private final Bot<C, D, S, E> bot;

    public BotHolder(MatrixClient matrixClient, S service, Bot<C, D, S, E> bot) {
        this.matrixClient = matrixClient;
        this.service = service;
        this.bot = bot;
    }

    public MatrixClient getMatrixClient() {
        return matrixClient;
    }

    public C getConfig() {
        return config;
    }

    public void setConfig(C config) {
        this.config = config;
    }

    public E getData() {
        return data;
    }

    public void setData(E data) {
        this.data = data;
    }

    protected S getService() {
        return service;
    }

    public List<Supplier<Void>> getShutdownListeners() {
        return shutdownListeners;
    }

    public Bot<C, D, S, E> getBot() {
        return bot;
    }

    /**
     * Invoke separate transaction.
     *
     * @param action action.
     */
    public void runInTransaction(BiConsumer<BotHolder<C, D, S, E>, D> action) {
        synchronized (monitor) {
            getService().invoke(dao -> {
                action.accept(this, dao);
            });
        }
    }

    /**
     * INvoke separate transaction.
     *
     * @param action action.
     * @param <R>    result's class.
     * @return result.
     */
    public <R> R runInTransaction(BiFunction<BotHolder<C, D, S, E>, D, R> action) {
        synchronized (monitor) {
            return getService().invoke(dao -> {
                return action.apply(this, dao);
            });
        }
    }

    /**
     * Add a new shutdown listener.
     *
     * @param listener shutdown listener.
     */
    public void addShutdownListener(Supplier<Void> listener) {
        getShutdownListeners().add(listener);
    }
}
