/*
 * Copyright Anatoliy Sablin tolya@sablin.xyz
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

package io.github.ma1uta.matrix.example;

import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.model.sync.AccountData;
import io.github.ma1uta.matrix.client.model.sync.DeviceLists;
import io.github.ma1uta.matrix.client.model.sync.InvitedRoom;
import io.github.ma1uta.matrix.client.model.sync.JoinedRoom;
import io.github.ma1uta.matrix.client.model.sync.LeftRoom;
import io.github.ma1uta.matrix.client.model.sync.Presence;
import io.github.ma1uta.matrix.client.model.sync.Rooms;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import io.github.ma1uta.matrix.client.model.sync.Timeline;
import io.github.ma1uta.matrix.client.sync.SyncLoop;
import io.github.ma1uta.matrix.client.sync.SyncParams;
import io.github.ma1uta.matrix.event.Event;
import io.github.ma1uta.matrix.event.RoomEvent;
import io.github.ma1uta.matrix.event.StateEvent;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.event.content.RoomMessageContent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncExample {

    public static final long TIMEOUT = 1000L;

    public static void main(String[] args) {
        String domain = "ru-matrix.org";
        String localpart = "ma1uta";
        char[] password = "password".toCharArray();

        StandaloneClient mxClient = new StandaloneClient.Builder().domain(domain).build();

        // login
        mxClient.auth().login(localpart, password);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        SyncLoop syncLoop = new SyncLoop(mxClient.sync(), SyncExample::processIncomingEvents);
        syncLoop.setInit(SyncParams.builder().fullState(true).timeout(TIMEOUT).build());
        executor.execute(syncLoop);
    }

    public static void processIncomingEvents(SyncResponse syncResponse, SyncParams syncParams) {
        System.out.println("Next batch: " + syncParams.getNextBatch());
        if (syncParams.isFullState()) {
            syncParams.setFullState(false);
        }

        AccountData accountData = syncResponse.getAccountData();
        if (accountData != null) {
            System.out.println("=== Account data ===");
            accountData.getEvents().forEach(SyncExample::printEvent);
        }

        DeviceLists deviceLists = syncResponse.getDeviceLists();
        if (deviceLists != null) {
            System.out.println("=== Changed devices ===");
            deviceLists.getChanged().forEach(System.out::println);

            System.out.println("=== Left devices ===");
            deviceLists.getLeft().forEach(System.out::println);

            System.out.println("=== Changed devices ===");
            deviceLists.getChanged().forEach(System.out::println);
        }

        Presence presence = syncResponse.getPresence();
        if (presence != null) {
            presence.getEvents().forEach(SyncExample::printEvent);
        }

        Rooms rooms = syncResponse.getRooms();
        if (rooms != null) {
            Map<String, InvitedRoom> invite = rooms.getInvite();
            if (invite != null) {
                System.out.println("=== Invites ===");
                for (Map.Entry<String, InvitedRoom> inviteEntry : invite.entrySet()) {
                    System.out.println("Invite into the room: " + inviteEntry.getKey());
                    InvitedRoom invitedRoom = inviteEntry.getValue();
                    if (invitedRoom != null && invitedRoom.getInviteState() != null) {
                        List<Event> inviteEvents = invitedRoom.getInviteState().getEvents();
                        if (inviteEvents != null) {
                            inviteEvents.forEach(SyncExample::printEvent);
                        }
                    }
                }
            }

            Map<String, LeftRoom> leave = rooms.getLeave();
            if (leave != null) {
                System.out.println("=== Left rooms ===");
                for (Map.Entry<String, LeftRoom> leftEntry : leave.entrySet()) {
                    System.out.println("Left from the room: " + leftEntry.getKey());
                }
            }

            Map<String, JoinedRoom> join = rooms.getJoin();
            if (join != null) {
                System.out.println("=== Joined rooms ===");
                for (Map.Entry<String, JoinedRoom> joinEntry : join.entrySet()) {
                    System.out.println("Joined room: " + joinEntry.getKey());
                    JoinedRoom joinedRoom = joinEntry.getValue();

                    System.out.println("= State =");
                    if (joinedRoom.getState() != null && joinedRoom.getState().getEvents() != null) {
                        joinedRoom.getState().getEvents().forEach(SyncExample::printEvent);
                    }

                    Timeline timeline = joinedRoom.getTimeline();
                    if (timeline != null && timeline.getEvents() != null) {
                        System.out.println("= Timeline =");
                        timeline.getEvents().forEach(SyncExample::printEvent);
                    }
                }
            }
        }
    }

    public static void printEvent(Event<?> event) {
        System.out.println("Type: " + event.getType());

        if (event instanceof RoomEvent) {
            RoomEvent<?> roomEvent = (RoomEvent<?>) event;

            System.out.println("Event ID: " + roomEvent.getEventId());
            System.out.println("Room ID: " + roomEvent.getRoomId());
            System.out.println("Sender: " + roomEvent.getSender());
            System.out.println("Origin server TS: " + roomEvent.getOriginServerTs());

            if (roomEvent instanceof StateEvent) {
                StateEvent<?> stateEvent = (StateEvent<?>) roomEvent;

                System.out.println("State key: " + stateEvent.getStateKey());
            }
        }

        EventContent content = event.getContent();
        if (content instanceof RoomMessageContent) {
            RoomMessageContent roomMessageContent = (RoomMessageContent) content;

            System.out.println("MSG type: " + roomMessageContent.getMsgtype());
            System.out.println("Body: " + roomMessageContent.getBody());
        }
    }
}
