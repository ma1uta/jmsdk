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

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import javax.inject.Inject

@PropertySource("classpath:/query.properties")
@Configuration
class QueryConfiguration {

    @Inject
    lateinit var env: Environment

    @Bean
    fun query(): Query {
        val user = Query.User(env.getProperty("user.read")!!,
                              env.getProperty("user.insert")!!)

        val device = Query.Device(env.getProperty("device.insertOrUpdate")!!,
                                  env.getProperty("device.updateLastSeen")!!,
                                  env.getProperty("device.findByToken")!!,
                                  env.getProperty("device.deleteToken")!!)

        val userInteractiveSession = Query.UserInteractiveSession(env.getProperty("userInteractiveSession.create")!!,
                                                                  env.getProperty("userInteractiveSession.read")!!,
                                                                  env.getProperty("userInteractiveSession.update")!!,
                                                                  env.getProperty("userInteractiveSession.delete")!!)


        return Query(user, device, userInteractiveSession)
    }
}
