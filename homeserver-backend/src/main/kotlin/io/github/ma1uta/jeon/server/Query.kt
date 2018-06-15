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

package io.github.ma1uta.jeon.server

class Query(val user: User,
            val device: Device,
            val userInteractiveSession: UserInteractiveSession) {

    class User(val read: String,
               val insert: String)

    class Device(val insertOrUpdate: String,
                 val updateLastSeen: String,
                 val findByToken: String,
                 val deleteToken: String)

    class UserInteractiveSession(val create: String,
                                 val read:   String,
                                 val update: String,
                                 val delete: String)
}
