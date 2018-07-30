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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.client.Client;

/**
 * Bot service.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 */
public abstract class AbstractStandaloneBotPool<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> extends
    AbstractBotPool<C, D, S, E, StandaloneBot<C, D, S, E>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractStandaloneBotPool.class);

    private static final int TIMEOUT = 10;

    private final ExecutorService pool;

    public AbstractStandaloneBotPool(String homeserverUrl, String displayName, Client client, S service,
                                     List<Class<? extends Command<C, D, S, E>>> commandClasses) {
        super(homeserverUrl, displayName, client, service, commandClasses);
        pool = Executors.newCachedThreadPool();
    }

    public ExecutorService getPool() {
        return pool;
    }

    @Override
    protected StandaloneBot<C, D, S, E> createBotInstance(C config) {
        return new StandaloneBot<>(getClient(), getHomeserverUrl(), true, config, getService(), getCommandClasses());
    }

    @Override
    protected void submitBot(StandaloneBot<C, D, S, E> bot) {
        getPool().submit(bot);
    }

    /**
     * Stop pool.
     *
     * @throws InterruptedException when cannot stop bot's thread.
     */
    @Override
    public void stop() throws InterruptedException {
        getPool().awaitTermination(TIMEOUT, TimeUnit.SECONDS);
    }
}
