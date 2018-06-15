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

import io.github.ma1uta.matrix.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public abstract class AbstractBotPool<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBotPool.class);

    private static final int TIMEOUT = 10;

    private final ExecutorService pool;

    private final String homeserverUrl;

    private final String displayName;

    private final Client client;

    private final String appToken;

    private final S service;

    private final List<Class<? extends Command<C, D, S, E>>> commandClasses;

    private Map<String, Bot<C, D, S, E>> botMap = new HashMap<>();

    private final RunState runState;

    public AbstractBotPool(String homeserverUrl, String displayName, Client client, String appToken,
                           S service, List<Class<? extends Command<C, D, S, E>>> commandClasses,
                           RunState runState) {
        this.service = service;
        this.commandClasses = commandClasses;
        this.runState = runState;
        this.pool = Executors.newCachedThreadPool();
        this.homeserverUrl = homeserverUrl;
        this.displayName = displayName;
        this.client = client;
        this.appToken = appToken;
    }

    public ExecutorService getPool() {
        return pool;
    }

    public String getHomeserverUrl() {
        return homeserverUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Client getClient() {
        return client;
    }

    public String getAppToken() {
        return appToken;
    }

    public S getService() {
        return service;
    }

    public List<Class<? extends Command<C, D, S, E>>> getCommandClasses() {
        return commandClasses;
    }

    public Map<String, Bot<C, D, S, E>> getBotMap() {
        return botMap;
    }

    public RunState getRunState() {
        return runState;
    }

    protected abstract C createConfig(String username);

    protected abstract void initializeBot(Bot<C, D, S, E> bot);

    /**
     * Run new bot.
     *
     * @param username bot's username
     */
    public void startNewBot(String username) {
        submit(createConfig(username));
    }

    /**
     * Send an one event to the bot.
     *
     * @param roomId room id.
     * @param event  event.
     * @return {@code true} if event was processed, else {@code false}.
     */
    public boolean send(String roomId, Event event) {
        if (RunState.APPLICATION_SERVICE.equals(getRunState())) {
            Optional<Bot<C, D, S, E>> bot = getBotMap().entrySet().stream()
                .filter(entry -> {
                    BotHolder<C, D, S, E> holder = entry.getValue().getHolder();
                    List<String> joinedRooms = holder.getMatrixClient().room().joinedRooms();
                    C config = holder.getConfig();
                    if (joinedRooms.contains(roomId)) {
                        return true;
                    }
                    Object membership = event.getContent().get("membership");
                    return Event.MembershipState.INVITE.equals(membership)
                        && Event.EventType.ROOM_MEMBER.equals(event.getType())
                        && config.getUserId().equals(event.getStateKey());
                }).map(Map.Entry::getValue).findFirst();

            if (bot.isPresent()) {
                bot.get().send(event);
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    protected void submit(C config) {
        getService().invoke(dao -> {
            dao.save(config);
        });
        Bot<C, D, S, E> bot = new Bot<>(getClient(), getHomeserverUrl(), getAppToken(), true, false, true, config, getService(),
            getCommandClasses());
        initializeBot(bot);
        String userId = bot.getHolder().getConfig().getUserId();
        getBotMap().put(userId, bot);
        bot.getHolder().addShutdownListener(() -> {
            getBotMap().remove(userId);
            return null;
        });
        switch (getRunState()) {
            case STANDALONE:
                getPool().submit(bot);
                break;
            case APPLICATION_SERVICE:
                if (BotState.NEW.equals(config.getState())) {
                    bot.newState();
                }
                bot.init();
                break;
            default:
                LOGGER.warn("Unknown run state: " + getRunState());
        }
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
        getPool().awaitTermination(TIMEOUT, TimeUnit.SECONDS);
    }
}
