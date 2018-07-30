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
import io.github.ma1uta.matrix.events.RoomMember;
import io.github.ma1uta.matrix.events.RoomMessage;
import io.github.ma1uta.matrix.events.messages.Text;
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
public class Bot<C extends BotConfig, D extends BotDao<C>, S extends PersistentService<D>, E> {

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
        this.exitOnEmptyRooms = exitOnEmptyRooms;
        this.commands = new HashMap<>(commandsClasses.size());
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
     * Register a new bot.
     * <br>
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

            LOGGER.debug("Finish registration.");
        });
        return LoopState.NEXT_STATE;
    }

    protected LoopState registeredState(Map<String, List<Event>> eventMap) {
        LOGGER.debug("Wait for invite");
        if (!eventMap.isEmpty()) {
            return joinRoom(eventMap) ? LoopState.NEXT_STATE : LoopState.RUN;
        }

        return LoopState.RUN;
    }

    /**
     * Join to room.
     *
     * @param eventMap invited eventMap. Map &lt;roomId&gt; - &lt;[event]&gt; room_id to invite_state.
     * @return true if bot joined else false.
     */
    public boolean joinRoom(Map<String, List<Event>> eventMap) {
        return getHolder().runInTransaction((holder, dao) -> {
            LOGGER.debug("Start joining.");
            boolean joined = false;
            for (Map.Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
                List<Event> inviteEvents = eventEntry.getValue().stream().peek(event -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Event type: {}", event.getType());
                    }
                }).filter(event -> {
                    if (event.getContent() instanceof RoomMember) {
                        RoomMember content = (RoomMember) event.getContent();
                        LOGGER.debug("Membership: {}", content.getMembership());
                        return Event.MembershipState.INVITE.equals(content.getMembership());
                    }
                    return false;
                }).collect(Collectors.toList());

                for (Event event : inviteEvents) {
                    String roomId = eventEntry.getKey();
                    LOGGER.debug("Join to room {}", roomId);
                    holder.getMatrixClient().room().joinByIdOrAlias(roomId);

                    C config = holder.getConfig();
                    config.setState(BotState.JOINED);
                    config.setOwner(event.getSender());
                    LOGGER.debug("Finish joining");
                    joined = true;
                }
            }
            return joined;
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
     * Process an one event.
     *
     * @param roomId room id.
     * @param event  event.
     * @return {@code true} if any command was invoked, else {@code false}.
     */
    protected boolean processEvent(String roomId, Event event) {
        MatrixClient matrixClient = getHolder().getMatrixClient();
        C config = getHolder().getConfig();
        boolean invoked = false;
        if (event.getContent() instanceof RoomMessage) {
            RoomMessage content = (RoomMessage) event.getContent();
            String body = content.getBody().trim();
            boolean permit = permit(event);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Sender: {}", event.getSender());
                LOGGER.debug("Msgtype: {}", content.getMsgtype());
                LOGGER.debug("Permit: {}", permit);
            }
            boolean defaultCommand = config.getDefaultCommand() != null && !config.getDefaultCommand().trim().isEmpty();
            if (!matrixClient.getUserId().equals(event.getSender())
                && content instanceof Text
                && permit
                && (body.startsWith(getPrefix()) || defaultCommand)) {
                try {
                    invoked = getHolder().runInTransaction((holder, dao) -> {
                        return processAction(roomId, event, body);
                    });
                } catch (Exception e) {
                    LOGGER.error(String.format("Cannot perform action '%s'", body), e);
                }
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
        String contentWithoutPrefix = content.substring(getPrefix().length());
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
