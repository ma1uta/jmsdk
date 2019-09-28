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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bot service.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 * @param <B> bot's class.
 */
public abstract class AbstractBotPool<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E,
    B extends Bot<C, D, S, E>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBotPool.class);

    private final String displayName;

    private final S service;

    private final List<Class<? extends Command<C, D, S, E>>> commandClasses;

    private Map<String, B> botMap = new HashMap<>();

    public AbstractBotPool(String displayName, S service, List<Class<? extends Command<C, D, S, E>>> commandClasses) {
        this.service = service;
        this.commandClasses = commandClasses;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public S getService() {
        return service;
    }

    public List<Class<? extends Command<C, D, S, E>>> getCommandClasses() {
        return commandClasses;
    }

    public Map<String, B> getBotMap() {
        return botMap;
    }

    protected abstract C createConfig(String username);

    protected abstract void initializeBot(Bot<C, D, S, E> bot);

    protected abstract B createBotInstance(C config);

    protected abstract void submitBot(B bot);

    /**
     * Run new bot.
     *
     * @param username bot's username
     */
    public void startNewBot(String username) {
        submit(createConfig(username));
    }

    protected void submit(C config) {
        getService().invoke(dao -> {
            dao.save(config);
        });
        B bot = createBotInstance(config);
        initializeBot(bot);
        String userId = bot.getContext().getConfig().getUserId();
        getBotMap().put(userId, bot);
        bot.getContext().addShutdownListener(() -> {
            getBotMap().remove(userId);
            return null;
        });
        submitBot(bot);
    }

    /**
     * Start pool.
     */
    public void start() {
        getService().invoke((dao) -> {
            dao.findAll().forEach(this::submit);
        });
    }

    /**
     * Stop pool.
     *
     * @throws InterruptedException when cannot stop bot's thread.
     */
    public void stop() throws InterruptedException {
    }
}
