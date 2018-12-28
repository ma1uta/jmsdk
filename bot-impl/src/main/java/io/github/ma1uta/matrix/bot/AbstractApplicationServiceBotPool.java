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

import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.RoomEvent;
import io.github.ma1uta.matrix.event.RoomMember;
import io.github.ma1uta.matrix.event.content.RoomMemberContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Bot service.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> bot service.
 * @param <E> extra data.
 */
public abstract class AbstractApplicationServiceBotPool<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> extends
    AbstractBotPool<C, D, S, E, ApplicationServiceBot<C, D, S, E>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractApplicationServiceBotPool.class);

    private final String appToken;

    public AbstractApplicationServiceBotPool(RequestFactory requestFactory, String displayName, String appToken, S service,
                                             List<Class<? extends Command<C, D, S, E>>> commandClasses) {
        super(requestFactory, displayName, service, commandClasses);
        this.appToken = appToken;
    }

    public String getAppToken() {
        return appToken;
    }

    /**
     * Send an one event to the bot.
     *
     * @param roomId room id.
     * @param event  event.
     * @return {@code true} if event was processed, else {@code false}.
     */
    public boolean send(String roomId, Event event) {
        LOGGER.debug("Receive event in the room: {0}", roomId);
        Optional<ApplicationServiceBot<C, D, S, E>> bot = getBotMap().entrySet().stream()
            .filter(entry -> {
                Context<C, D, S, E> context = entry.getValue().getContext();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Bot \"{}\"", context.getConfig().getUserId());
                }
                List<String> joinedRooms;
                try {
                    joinedRooms = context.getMatrixClient().room().joinedRooms().join();
                } catch (Exception e) {
                    LOGGER.error("Cannot retrieve joined rooms.", e);
                    return false;
                }
                if (LOGGER.isDebugEnabled()) {
                    joinedRooms.forEach(joinedRoom -> LOGGER.debug("Room: {}", joinedRoom));
                }
                if (joinedRooms.contains(roomId)) {
                    return true;
                }
                if (event instanceof RoomMember) {
                    RoomMember roomMember = (RoomMember) event;
                    String stateKey = roomMember.getStateKey();
                    String membership = roomMember.getContent().getMembership();
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Membership: {}", membership);
                        LOGGER.debug("Event type: {}", event.getType());
                        LOGGER.debug("State key: {}", stateKey);
                    }
                    return context.getConfig().getUserId().equals(stateKey) && RoomMemberContent.INVITE.equals(membership);
                }
                return false;
            }).map(Map.Entry::getValue).findFirst();

        if (bot.isPresent() && event instanceof RoomEvent) {
            LOGGER.debug("Bot \"{}\" is found.", bot.get().getContext().getConfig().getUserId());
            bot.get().send((RoomEvent) event);
            return true;
        } else {
            LOGGER.debug("Bot didn't found.");
            return false;
        }
    }

    @Override
    protected ApplicationServiceBot<C, D, S, E> createBotInstance(C config) {
        return new ApplicationServiceBot<>(getRequestFactory(), getAppToken(), true, config, getService(), getCommandClasses());
    }

    @Override
    protected void submitBot(ApplicationServiceBot<C, D, S, E> bot) {
        if (BotState.NEW.equals(bot.getContext().getConfig().getState())) {
            bot.newState();
        }
        bot.init();
    }
}
