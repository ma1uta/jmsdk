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

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import io.github.ma1uta.jeon.server.model.User
import io.github.ma1uta.jeon.server.service.UserService
import org.glassfish.jersey.server.ResourceConfig
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.client.RestTemplate
import java.security.SecureRandom

@Configuration
@EnableConfigurationProperties(value = [ServerProperties::class])
@EnableGlobalMethodSecurity
class ServerConfiguration {

    @Bean
    fun jerseyContext() = ResourceConfig()

    @Bean
    fun mapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.propertyNamingStrategy = PropertyNamingStrategy.LOWER_CAMEL_CASE
        return mapper
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        val secureRandom = SecureRandom(byteArrayOf(10, 20))
        secureRandom.setSeed(10)
        return BCryptPasswordEncoder(10, secureRandom)
    }

    @Bean
    fun propertySourcesPlaceholderConfigurer() = PropertySourcesPlaceholderConfigurer().apply { setPlaceholderPrefix("%{") }

    @Bean
    fun initialUserCreation(userService: UserService) = InitializingBean {
        if (userService.read("dummy") == null) {
            userService.insert(User("dummy", "dummy"))
        }
    }

    @Bean
    fun restTemplate() = RestTemplate()
}
