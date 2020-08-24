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

public class SimpleExample {

    public static void main(String[] args) {
        String domain = "ru-matrix.org";
        String localpart = "ma1uta";
        char[] password = "my_very_secret_password".toCharArray();

        StandaloneClient mxClient = new StandaloneClient.Builder().domain(domain).build();

        // login
        String userId = mxClient.auth().login(localpart, password).getUserId();

        // set display name via profile api
        System.out.println(mxClient.profile().showDisplayName(userId));

        // retrieve all joined rooms
        mxClient.room().joinedRooms().getJoinedRooms().forEach(System.out::println);

        // logout
        mxClient.auth().logout();
    }
}
