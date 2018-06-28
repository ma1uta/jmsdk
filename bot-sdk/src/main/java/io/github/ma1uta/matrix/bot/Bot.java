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
import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.filter.FilterData;
import io.github.ma1uta.matrix.client.model.filter.RoomEventFilter;
import io.github.ma1uta.matrix.client.model.filter.RoomFilter;
import io.github.ma1uta.matrix.client.model.sync.InvitedRoom;
import io.github.ma1uta.matrix.client.model.sync.JoinedRoom;
import io.github.ma1uta.matrix.client.model.sync.LeftRoom;
import io.github.ma1uta.matrix.client.model.sync.Rooms;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.ws.rs.client.Client;

/**
 * Matrix bot client.
 *
 * @param <C> bot configuration.
 * @param <D> bot dao.
 * @param <S> service.
 * @param <E> extra data.
 */
public class Bot<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bot.class);

    private final Map<String, Command<C, D, S, E>> commands;

    private BiConsumer<BotHolder<C, D, S, E>, D> initAction;

    private final BotHolder<C, D, S, E> holder;

    private final boolean exitOnEmptyRooms;

    private final Set<String> skipTimelineRooms = new HashSet<>();

    public Bot(Client client, String homeserverUrl, String asToken, boolean addUserIdToRequests, boolean updateAccessToken,
               boolean exitOnEmptyRooms, C config, S service, List<Class<? extends Command<C, D, S, E>>> commandsClasses) {
        MatrixClient matrixClient = new MatrixClient(homeserverUrl, client, addUserIdToRequests, updateAccessToken);
        matrixClient.setAccessToken(asToken);
        matrixClient.setUserId(config.getUserId());
        this.holder = new BotHolder<>(matrixClient, service, this);
        this.holder.setConfig(config);
        this.commands = new HashMap<>(commandsClasses.size());
        this.exitOnEmptyRooms = exitOnEmptyRooms;
        commandsClasses.forEach(cl -> {
            try {
                Command<C, D, S, E> command = cl.newInstance();
                this.commands.put(command.name(), command);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Cannot create new instance of the command: " + cl.getCanonicalName(), e);
            }
        });
    }

    public Map<String, Command<C, D, S, E>> getCommands() {
        return commands;
    }

    public BotHolder<C, D, S, E> getHolder() {
        return holder;
    }

    public BiConsumer<BotHolder<C, D, S, E>, D> getInitAction() {
        return initAction;
    }

    public boolean isExitOnEmptyRooms() {
        return exitOnEmptyRooms;
    }

    public void setInitAction(BiConsumer<BotHolder<C, D, S, E>, D> initAction) {
        this.initAction = initAction;
    }

    public Set<String> getSkipTimelineRooms() {
        return skipTimelineRooms;
    }

    @Override
    public void run() {
        try {
            init();

            LoopState state = LoopState.RUN;
            while (!LoopState.EXIT.equals(state)) {
                switch (getHolder().getConfig().getState()) {
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
                        LOGGER.error("Unknown state: " + getHolder().getConfig().getState());
                }
            }

        } catch (Throwable e) {
            LOGGER.error("Exception:", e);
            throw e;
        }

        getHolder().getShutdownListeners().forEach(Supplier::get);
    }

    /**
     * Run startup action.
     */
    public void init() {
        BotHolder<C, D, S, E> holder = getHolder();
        if (holder.getMatrixClient().isUpdateAccessToken()) {
            C config = holder.getConfig();
            holder.getMatrixClient().auth().login(config.getUserId(), config.getPassword());
        }

        if (getInitAction() != null) {
            holder.runInTransaction((txHolder, dao) -> {
                getInitAction().accept(txHolder, dao);
            });
        }
    }

    /**
     * Save bot's config.
     *
     * @param dao DAO.
     */
    protected void saveData(BotHolder<C, D, S, E> holder, D dao) {
        C oldConfig = holder.getConfig();
        C newConfig = dao.save(oldConfig);
        holder.setConfig(newConfig);
    }

    /**
     * Main loop.
     *
     * @param loopAction state action.
     * @return next loop state.
     */
    protected LoopState loop(Function<SyncResponse, LoopState> loopAction) {
        C config = getHolder().getConfig();
        MatrixClient matrixClient = getHolder().getMatrixClient();
        SyncResponse sync = matrixClient.sync().sync(config.getFilterId(), config.getNextBatch(), false, null, null);

        String initialBatch = sync.getNextBatch();
        if (config.getNextBatch() == null && config.getSkipInitialSync() != null && config.getSkipInitialSync()) {
            getHolder().runInTransaction((holder, dao) -> {
                holder.getConfig().setNextBatch(initialBatch);
                saveData(holder, dao);
            });
            sync = matrixClient.sync().sync(config.getFilterId(), initialBatch, false, null, config.getTimeout());
        }

        while (true) {
            try {
                LoopState nextState = loopAction.apply(sync);

                String nextBatch = sync.getNextBatch();
                getHolder().runInTransaction((holder, dao) -> {
                    holder.getConfig().setNextBatch(nextBatch);
                    saveData(holder, dao);
                });

                if (LoopState.NEXT_STATE.equals(nextState)) {
                    return LoopState.NEXT_STATE;
                }

                if (Thread.currentThread().isInterrupted()) {
                    return LoopState.EXIT;
                }

                sync = matrixClient.sync().sync(config.getFilterId(), nextBatch, false, null, config.getTimeout());
            } catch (Exception e) {
                LOGGER.error("Exception: ", e);
            }
        }
    }

    /**
     * Register a new bot.
     * <p/>
     * After registration setup a filter to receive only message events.
     *
     * @return {@link LoopState#NEXT_STATE} always. Move to the next state.
     */
    public LoopState newState() {
        getHolder().runInTransaction((holder, dao) -> {
            LOGGER.debug("Start registration.");
            BotConfig config = holder.getConfig();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(Id.localpart(config.getUserId()));
            registerRequest.setInitialDeviceDisplayName(config.getDisplayName());
            registerRequest.setDeviceId(config.getDeviceId());

            MatrixClient matrixClient = holder.getMatrixClient();
            matrixClient.account().register(registerRequest);
            LOGGER.debug("Set new display name: {}", config.getDisplayName());
            matrixClient.profile().setDisplayName(config.getDisplayName());

            RoomEventFilter roomEventFilter = new RoomEventFilter();
            roomEventFilter.setTypes(Collections.singletonList(Event.EventType.ROOM_MESSAGE));
            RoomFilter roomFilter = new RoomFilter();
            roomFilter.setTimeline(roomEventFilter);
            FilterData filter = new FilterData();
            filter.setRoom(roomFilter);
            config.setFilterId(matrixClient.filter().uploadFilter(filter).getFilterId());
            LOGGER.debug("Set new filter: {}", config.getFilterId());

            config.setState(BotState.REGISTERED);

            saveData(holder, dao);
            LOGGER.debug("Finish registration.");
        });
        return LoopState.NEXT_STATE;
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

    protected LoopState registeredState(Map<String, List<Event>> eventMap) {
        LOGGER.debug("Wait for invite");
        if (!eventMap.isEmpty()) {
            joinRoom(eventMap);
            return LoopState.NEXT_STATE;
        }

        return LoopState.RUN;
    }

    /**
     * Logic of the joined state.
     *
     * @return next loop state.
     */
    protected LoopState joinedState() {
        return loop(sync -> {
            Rooms rooms = sync.getRooms();

            MatrixClient matrixClient = getHolder().getMatrixClient();
            List<String> joinedRooms = matrixClient.room().joinedRooms();
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

            if (getHolder().getMatrixClient().room().joinedRooms().isEmpty()) {
                getHolder().runInTransaction((holder, dao) -> {
                    holder.getConfig().setState(isExitOnEmptyRooms() ? BotState.DELETED : BotState.REGISTERED);
                    saveData(holder, dao);
                });
                return LoopState.NEXT_STATE;
            }
            return nextState;
        });
    }

    /**
     * Join to room.
     *
     * @param eventMap invited eventMap. Map &lt;roomId&gt; - &lt;[event]&gt; room_id to invite_state.
     */
    public void joinRoom(Map<String, List<Event>> eventMap) {
        getHolder().runInTransaction((holder, dao) -> {
            LOGGER.debug("Start joining.");
            eventMap.forEach((roomId, events) -> events.stream().filter(event -> {
                Object membership = event.getContent().get("membership");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Membership: {}", membership);
                    LOGGER.debug("Event type: {}", event.getType());
                }
                return Event.MembershipState.INVITE.equals(membership)
                    && Event.EventType.ROOM_MEMBER.equals(event.getType());
            }).findFirst().ifPresent(event -> {
                LOGGER.debug("Join to room {}", roomId);
                holder.getMatrixClient().room().joinByIdOrAlias(roomId);

                C config = holder.getConfig();
                config.setState(BotState.JOINED);
                config.setOwner(event.getSender());
                saveData(holder, dao);
                LOGGER.debug("Finish joining");
            }));
        });
    }

    /**
     * Delete bot.
     *
     * @return stop running.
     */
    public LoopState deletedState() {
        getHolder().runInTransaction((holder, dao) -> {
            LOGGER.debug("Delete bot");
            holder.getMatrixClient().account().deactivate(null);
            dao.delete(holder.getConfig());
        });
        return LoopState.EXIT;
    }

    /**
     * Process commands.
     *
     * @param roomId room id.
     * @param events events.
     * @return next loop state.
     */
    public LoopState processJoinedRoom(String roomId, List<Event> events) {
        String lastEvent = null;
        long lastOriginTs = 0;
        MatrixClient matrixClient = getHolder().getMatrixClient();
        boolean invoked = false;
        for (Event event : events) {
            if (!getSkipTimelineRooms().contains(roomId)) {
                LOGGER.debug("Process events");
                invoked = processEvent(roomId, event);
            } else {
                LOGGER.debug("Skip timelines");
            }

            if (event.getOriginServerTs() != null && event.getOriginServerTs() > lastOriginTs) {
                lastOriginTs = event.getOriginServerTs();
                lastEvent = event.getEventId();
            }
        }
        C config = getHolder().getConfig();
        boolean read = config.getReceiptPolicy() == null || ReceiptPolicy.READ.equals(config.getReceiptPolicy());
        boolean executed = config.getReceiptPolicy() != null && ReceiptPolicy.EXECUTED.equals(config.getReceiptPolicy()) && invoked;

        LOGGER.debug("Read: {}", read);
        LOGGER.debug("Executed: {}", executed);
        LOGGER.debug("Last event: {}", lastEvent);
        if (lastEvent != null && (read || executed)) {
            LOGGER.debug("send receipt");
            matrixClient.receipt().sendReceipt(roomId, lastEvent);
        }
        getSkipTimelineRooms().remove(roomId);
        return LoopState.RUN;
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

    /**
     * Process an one event.
     *
     * @param roomId room id.
     * @param event  event.
     * @return {@code true} if any command was invoked, else {@code false}.
     */
    protected boolean processEvent(String roomId, Event event) {
        MatrixClient matrixClient = getHolder().getMatrixClient();
        C config = getHolder().getConfig();
        Map<String, Object> content = event.getContent();
        String body = (String) content.get("body");
        boolean invoked = false;
        boolean permit = permit(event);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Event type: {}", event.getType());
            LOGGER.debug("Sender: {}", event.getSender());
            LOGGER.debug("Msgtype: {}", content.get("msgtype"));
            LOGGER.debug("Permit: {}", permit);
        }
        if (Event.EventType.ROOM_MESSAGE.equals(event.getType())
            && !matrixClient.getUserId().equals(event.getSender())
            && Event.MessageType.TEXT.equals(content.get("msgtype"))
            && permit
            && (body.trim().startsWith(getPrefix()) || (config.getDefaultCommand() != null && !config.getDefaultCommand().trim()
            .isEmpty()))) {
            try {
                invoked = getHolder().runInTransaction((holder, dao) -> {
                    boolean processed = processAction(roomId, event, body);
                    saveData(holder, dao);
                    return processed;
                });
            } catch (Exception e) {
                LOGGER.error(String.format("Cannot perform action '%s'", body), e);
            }
        }
        return invoked;
    }

    /**
     * Permission check.
     *
     * @param event event.
     * @return {@code true}, if process event, else {@code false}.
     */
    protected boolean permit(Event event) {
        C config = getHolder().getConfig();
        return config.getPolicy() == null
            || AccessPolicy.ALL.equals(config.getPolicy())
            || config.getOwner().equals(event.getSender());
    }

    /**
     * Get bot's command prefix.
     *
     * @return command prefix.
     */
    public String getPrefix() {
        C config = getHolder().getConfig();
        String prefix = config.getPrefix();
        return prefix == null ? "!" : prefix.replaceAll("\\{\\{display_name}}", config.getDisplayName());
    }

    /**
     * Process action.
     *
     * @param roomId  room id.
     * @param event   event.
     * @param content command.
     * @return {@code true} if invoked command, else {@code false}.
     */
    protected boolean processAction(String roomId, Event event, String content) {
        String contentWithoutPrefix = content.trim().substring(getPrefix().length());
        String[] arguments = contentWithoutPrefix.trim().split("\\s");
        String commandName = arguments[0];
        Command<C, D, S, E> command = getCommands().get(commandName);
        C config = getHolder().getConfig();
        String argument = Arrays.stream(arguments).skip(1).collect(Collectors.joining(" "));
        String defaultCommand = config.getDefaultCommand();
        if (command == null && defaultCommand != null && !defaultCommand.trim().isEmpty()) {
            command = getCommands().get(defaultCommand);
            argument = content;
        }
        if (command != null) {
            LOGGER.debug("invoke command: {}", command.getClass());
            return command.invoke(getHolder(), roomId, event, argument);
        } else {
            getHolder().getMatrixClient().event().sendNotice(roomId, "Unknown command: " + commandName);
            return false;
        }
    }
}
