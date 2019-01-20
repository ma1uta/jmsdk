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

import io.github.ma1uta.matrix.Id;
import io.github.ma1uta.matrix.client.MatrixClient;
import io.github.ma1uta.matrix.client.RequestParams;
import io.github.ma1uta.matrix.client.factory.RequestFactory;
import io.github.ma1uta.matrix.client.model.account.RegisterRequest;
import io.github.ma1uta.matrix.client.model.filter.FilterData;
import io.github.ma1uta.matrix.client.model.filter.RoomEventFilter;
import io.github.ma1uta.matrix.client.model.filter.RoomFilter;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.RoomEvent;
import io.github.ma1uta.matrix.event.RoomMember;
import io.github.ma1uta.matrix.event.RoomMessage;
import io.github.ma1uta.matrix.event.content.RoomMemberContent;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;
import io.github.ma1uta.matrix.event.message.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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

    private BiConsumer<Context<C, D, S, E>, D> initAction;

    private Context<C, D, S, E> context;

    private final boolean exitOnEmptyRooms;

    private final Set<String> skipTimelineRooms = new HashSet<>();

    public Bot(RequestFactory factory, String asToken, boolean exitOnEmptyRooms, C config, S service,
               List<Class<? extends Command<C, D, S, E>>> commandsClasses) {
        this.context = init(factory, asToken, config, service);
        this.exitOnEmptyRooms = exitOnEmptyRooms;
        this.commands = new HashMap<>(commandsClasses.size());
        commandsClasses.forEach(cl -> {
            try {
                Command<C, D, S, E> command = cl.getDeclaredConstructor().newInstance();
                this.commands.put(command.name(), command);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                LOGGER.error("Cannot create new instance of the command: " + cl.getCanonicalName(), e);
            }
        });
    }

    protected Context<C, D, S, E> init(RequestFactory factory, String asToken, C config, S service) {
        MatrixClient matrixClient = new MatrixClient(factory, new RequestParams().userId(config.getUserId()).accessToken(asToken));
        Context<C, D, S, E> context = new Context<>(matrixClient, service, this);
        context.setConfig(config);
        return context;
    }

    /**
     * Run startup action.
     */
    public void init() {
        Context<C, D, S, E> context = getContext();
        C config = context.getConfig();
        context.getMatrixClient().auth().login(config.getUserId(), config.getPassword());

        if (getInitAction() != null) {
            context.runInTransaction((ctx, dao) -> {
                getInitAction().accept(ctx, dao);
            });
        }
    }

    public Map<String, Command<C, D, S, E>> getCommands() {
        return commands;
    }

    public Context<C, D, S, E> getContext() {
        return context;
    }

    public BiConsumer<Context<C, D, S, E>, D> getInitAction() {
        return initAction;
    }

    public boolean isExitOnEmptyRooms() {
        return exitOnEmptyRooms;
    }

    public void setInitAction(BiConsumer<Context<C, D, S, E>, D> initAction) {
        this.initAction = initAction;
    }

    public Set<String> getSkipTimelineRooms() {
        return skipTimelineRooms;
    }

    /**
     * Register a new bot.
     * <br>
     * After registration setup a filter to receive only message events.
     *
     * @return {@link LoopState#NEXT_STATE} always. Move to the next state.
     */
    public LoopState newState() {
        getContext().runInTransaction((context, dao) -> {
            LOGGER.debug("Start registration.");
            BotConfig config = context.getConfig();

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(Id.of(config.getUserId()).getLocalpart());
            registerRequest.setInitialDeviceDisplayName(config.getDisplayName());
            registerRequest.setDeviceId(config.getDeviceId());

            MatrixClient matrixClient = context.getMatrixClient();
            matrixClient.account().register(registerRequest);
            LOGGER.debug("Set new display name: {}", config.getDisplayName());
            matrixClient.profile().setDisplayName(config.getDisplayName());

            RoomEventFilter roomEventFilter = new RoomEventFilter();
            roomEventFilter.setTypes(Collections.singletonList(RoomMessage.TYPE));
            RoomFilter roomFilter = new RoomFilter();
            roomFilter.setTimeline(roomEventFilter);
            FilterData filter = new FilterData();
            filter.setRoom(roomFilter);
            config.setFilterId(matrixClient.filter().uploadFilter(filter).join().getFilterId());
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
        return getContext().runInTransaction((context, dao) -> {
            LOGGER.debug("Start joining.");
            boolean joined = false;
            for (Map.Entry<String, List<Event>> eventEntry : eventMap.entrySet()) {
                List<Event> inviteEvents = eventEntry.getValue().stream().peek(state -> {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Event type: {}", state.getType());
                    }
                }).filter(state -> {
                    if (state instanceof RoomMember) {
                        RoomMember roomMember = (RoomMember) state;
                        String membership = roomMember.getContent().getMembership();
                        LOGGER.debug("Membership: {}", membership);
                        return RoomMemberContent.INVITE.equals(membership);
                    }
                    return false;
                }).collect(Collectors.toList());

                for (Event state : inviteEvents) {
                    if (state instanceof RoomEvent) {
                        String roomId = eventEntry.getKey();
                        LOGGER.debug("Join to room {}", roomId);
                        context.getMatrixClient().room().joinByIdOrAlias(roomId);

                        C config = context.getConfig();
                        config.setState(BotState.JOINED);
                        config.setOwner(((RoomEvent) state).getSender());
                        LOGGER.debug("Finish joining");
                        joined = true;
                    }
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
        getContext().runInTransaction((context, dao) -> {
            LOGGER.debug("Delete bot");
            context.getMatrixClient().account().deactivate(null);
            dao.delete(context.getConfig());
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
        MatrixClient matrixClient = getContext().getMatrixClient();
        boolean invoked = false;
        for (Event event : events) {
            if (!getSkipTimelineRooms().contains(roomId)) {
                LOGGER.debug("Process events");
                invoked = processEvent(roomId, event);
            } else {
                LOGGER.debug("Skip timelines");
            }
            if (event instanceof RoomEvent) {
                RoomEvent roomEvent = (RoomEvent) event;
                if (roomEvent.getOriginServerTs() != null && roomEvent.getOriginServerTs() > lastOriginTs) {
                    lastOriginTs = roomEvent.getOriginServerTs();
                    lastEvent = roomEvent.getEventId();
                }
            }
        }
        C config = getContext().getConfig();
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
        MatrixClient matrixClient = getContext().getMatrixClient();
        C config = getContext().getConfig();
        boolean invoked = false;
        if (event instanceof RoomMessage) {
            RoomMessage roomMessage = (RoomMessage) event;
            RoomMessageContent content = (RoomMessageContent) roomMessage.getContent();
            String body = content.getBody().trim();
            boolean permit = permit(event);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Sender: {}", roomMessage.getSender());
                LOGGER.debug("Msgtype: {}", content.getMsgtype());
                LOGGER.debug("Permit: {}", permit);
            }
            boolean defaultCommand = config.getDefaultCommand() != null && !config.getDefaultCommand().trim().isEmpty();
            if (!matrixClient.getUserId().equals(roomMessage.getSender())
                && content instanceof Text
                && permit
                && (body.startsWith(getPrefix()) || defaultCommand)) {
                try {
                    invoked = getContext().runInTransaction((context, dao) -> {
                        return processAction(roomId, roomMessage, body);
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
        C config = getContext().getConfig();
        return config.getPolicy() == null
            || AccessPolicy.ALL.equals(config.getPolicy())
            || (event instanceof RoomEvent && config.getOwner().equals(((RoomEvent) event).getSender()));
    }

    /**
     * Get bot's command prefix.
     *
     * @return command prefix.
     */
    public String getPrefix() {
        C config = getContext().getConfig();
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
    protected boolean processAction(String roomId, RoomEvent event, String content) {
        String contentWithoutPrefix = content.substring(getPrefix().length());
        String[] arguments = contentWithoutPrefix.trim().split("\\s");
        String commandName = arguments[0];
        Command<C, D, S, E> command = getCommands().get(commandName);
        C config = getContext().getConfig();
        String argument = Arrays.stream(arguments).skip(1).collect(Collectors.joining(" "));
        String defaultCommand = config.getDefaultCommand();
        if (command == null && defaultCommand != null && !defaultCommand.trim().isEmpty()) {
            command = getCommands().get(defaultCommand);
            argument = content;
        }
        if (command != null) {
            LOGGER.debug("invoke command: {}", command.getClass());
            return command.invoke(getContext(), roomId, event, argument);
        } else {
            getContext().getMatrixClient().event().sendNotice(roomId, "Unknown command: " + commandName);
            return false;
        }
    }
}
