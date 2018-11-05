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
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.sync.InvitedRoom;
import io.github.ma1uta.matrix.client.model.sync.JoinedRoom;
import io.github.ma1uta.matrix.client.model.sync.LeftRoom;
import io.github.ma1uta.matrix.client.model.sync.Rooms;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import io.github.ma1uta.matrix.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Matrix bot client.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> service.
 * @param <E> extra data.
 */
public class StandaloneBot<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> extends Bot<C, D, S, E> implements
    Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneBot.class);

    public StandaloneBot(RequestFactory factory, boolean exitOnEmptyRooms, C config, S service,
                         List<Class<? extends Command<C, D, S, E>>> commandsClasses) {
        super(factory, null, exitOnEmptyRooms, config, service, commandsClasses);
    }

    @Override
    public void run() {
        try {
            init();

            LoopState state = LoopState.RUN;
            while (!LoopState.EXIT.equals(state)) {
                switch (getContext().getConfig().getState()) {
                    case NEW:
                        state = newState();
                        break;
                    case REGISTERED:
                        state = registeredState();
                        break;
                    case JOINED:
                        state = joinedState();
                        break;
                    case DELETED:
                        state = deletedState();
                        break;
                    default:
                        LOGGER.error("Unknown state: " + getContext().getConfig().getState());
                }
            }

        } catch (Throwable e) {
            LOGGER.error("Exception:", e);
            throw e;
        } finally {
            getContext().getShutdownListeners().forEach(Supplier::get);
        }
    }

    /**
     * Main loop.
     *
     * @param loopAction state action.
     * @return next loop state.
     */
    protected LoopState loop(Function<SyncResponse, LoopState> loopAction) {
        C config = getContext().getConfig();
        MatrixClient matrixClient = getContext().getMatrixClient();
        SyncResponse sync = matrixClient.sync().sync(config.getFilterId(), config.getNextBatch(), false, null, null).join();

        String initialBatch = sync.getNextBatch();
        if (config.getNextBatch() == null && config.getSkipInitialSync() != null && config.getSkipInitialSync()) {
            getContext().runInTransaction((context, dao) -> {
                context.getConfig().setNextBatch(initialBatch);
            });
            sync = matrixClient.sync().sync(config.getFilterId(), initialBatch, false, null, config.getTimeout()).join();
        }

        while (true) {
            try {
                LoopState nextState = loopAction.apply(sync);

                String nextBatch = sync.getNextBatch();
                getContext().runInTransaction((context, dao) -> {
                    context.getConfig().setNextBatch(nextBatch);
                });

                if (LoopState.NEXT_STATE.equals(nextState)) {
                    return LoopState.NEXT_STATE;
                }

                if (Thread.currentThread().isInterrupted()) {
                    return LoopState.EXIT;
                }

                sync = matrixClient.sync().sync(config.getFilterId(), nextBatch, false, null, config.getTimeout()).join();
            } catch (Exception e) {
                LOGGER.error("Exception: ", e);
            }
        }
    }

    /**
     * Waiting to join.
     *
     * @return next loop state.
     */
    protected LoopState registeredState() {
        return loop(sync -> {
            Map<String, InvitedRoom> invite = sync.getRooms().getInvite();
            Map<String, List<Event>> eventMap = new HashMap<>();
            for (Map.Entry<String, InvitedRoom> entry : invite.entrySet()) {
                eventMap.put(entry.getKey(), entry.getValue().getInviteState().getEvents());
            }
            return registeredState(eventMap);
        });
    }

    /**
     * Logic of the joined state.
     *
     * @return next loop state.
     */
    protected LoopState joinedState() {
        return loop(sync -> {
            Rooms rooms = sync.getRooms();

            MatrixClient matrixClient = getContext().getMatrixClient();
            List<String> joinedRooms = matrixClient.room().joinedRooms().join();
            for (Map.Entry<String, LeftRoom> roomEntry : rooms.getLeave().entrySet()) {
                String leftRoom = roomEntry.getKey();
                if (joinedRooms.contains(leftRoom)) {
                    matrixClient.room().leave(leftRoom);
                }
            }

            LoopState nextState = LoopState.RUN;
            for (Map.Entry<String, JoinedRoom> joinedRoomEntry : rooms.getJoin().entrySet()) {
                LoopState state = processJoinedRoom(joinedRoomEntry.getKey(), joinedRoomEntry.getValue().getTimeline().getEvents());
                switch (state) {
                    case EXIT:
                        nextState = LoopState.EXIT;
                        break;
                    case NEXT_STATE:
                        if (!LoopState.EXIT.equals(nextState)) {
                            nextState = LoopState.NEXT_STATE;
                        }
                        break;
                    case RUN:
                    default:
                        // nothing to do
                        break;
                }
            }

            if (getContext().getMatrixClient().room().joinedRooms().join().isEmpty()) {
                getContext().runInTransaction((context, dao) -> {
                    context.getConfig().setState(isExitOnEmptyRooms() ? BotState.DELETED : BotState.REGISTERED);
                });
                return LoopState.NEXT_STATE;
            }
            return nextState;
        });
    }
}
