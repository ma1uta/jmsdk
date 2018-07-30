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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.ws.rs.client.Client;

/**
 * Matrix bot client.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> service.
 * @param <E> extra data.
 */
public class ApplicationServiceBot<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> extends Bot<C, D, S, E> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationServiceBot.class);

    public ApplicationServiceBot(Client client, String homeserverUrl, String asToken, boolean exitOnEmptyRooms, C config, S service,
                                 List<Class<? extends Command<C, D, S, E>>> commandsClasses) {
        super(client, homeserverUrl, asToken, true, false, exitOnEmptyRooms, config, service, commandsClasses);
    }

    /**
     * Send event.
     *
     * @param event event.
     */
    public void send(Event event) {
        LoopState state = LoopState.RUN;
        LOGGER.debug("State: {}", state);
        switch (getHolder().getConfig().getState()) {
            case NEW:
                state = newState();
                break;
            case REGISTERED:
                Map<String, List<Event>> eventMap = new HashMap<>();
                eventMap.put(event.getRoomId(), Collections.singletonList(event));
                state = registeredState(eventMap);
                break;
            case JOINED:
                state = processJoinedRoom(event.getRoomId(), Collections.singletonList(event));
                break;
            case DELETED:
                state = deletedState();
                break;
            default:
                LOGGER.error("Unknown state: " + getHolder().getConfig().getState());
        }

        if (LoopState.EXIT.equals(state)) {
            getHolder().getShutdownListeners().forEach(Supplier::get);
        }
    }
}
