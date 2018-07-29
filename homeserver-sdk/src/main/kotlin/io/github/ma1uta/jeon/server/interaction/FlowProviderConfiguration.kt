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

package io.github.ma1uta.jeon.server.interaction

import io.github.ma1uta.jeon.server.ServerProperties
import io.github.ma1uta.jeon.server.auth.UsernamePasswordLoginProvider
import io.github.ma1uta.jeon.server.interaction.stage.DummyStageProvider
import io.github.ma1uta.jeon.server.interaction.stage.EmailIdentityStageProvider
import io.github.ma1uta.jeon.server.interaction.stage.OAuth2StageProvider
import io.github.ma1uta.jeon.server.interaction.stage.PasswordStageProvider
import io.github.ma1uta.jeon.server.interaction.stage.ReCaptchaStageProvider
import io.github.ma1uta.jeon.server.interaction.stage.TokenStageProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(value = [FlowProviderProperties::class])
class FlowProviderConfiguration {

    @Bean
    fun flowProviders(flowProviderProperties: FlowProviderProperties, stageProviders: List<StageProvider>?): List<FlowProvider>? {
        if (stageProviders == null) {
            return null
        }

        val stageMap = stageProviders.map { sp -> Pair(sp.stage(), sp) }.toMap()
        return flowProviderProperties.flows.map { f -> FlowProvider(f.stages.map { s -> stageMap[s] }.requireNoNulls()) }
    }

    @Configuration
    @ConditionalOnProperty(name = ["jeon.stage.password.enabled"], havingValue = "true")
    class PasswordStageProviderConfiguration {

        @Bean
        fun passwordStageProvider(usernamePasswordLoginProvider: UsernamePasswordLoginProvider) =
                PasswordStageProvider(usernamePasswordLoginProvider)
    }

    @Configuration
    @ConditionalOnProperty(name = ["jeon.stage.recaptcha.enabled"], havingValue = "true")
    class ReCaptchaStageProviderConfiguration {

        @Bean
        fun reCaptchaStageProvider(serverProperties: ServerProperties, restTemplate: RestTemplate) =
                ReCaptchaStageProvider(serverProperties, restTemplate)
    }

    @Configuration
    @ConditionalOnProperty(name = ["jeon.stage.token.enabled"], havingValue = "true")
    class TokenStageProviderConfiguration {

        @Bean
        fun tokenStageProvider() = TokenStageProvider()
    }

    @Configuration
    @ConditionalOnProperty(name = ["jeon.stage.oauth2.enabled"], havingValue = "true")
    class OAuth2StageProviderConfiguration {

        @Bean
        fun oauth2StageProvider() = OAuth2StageProvider()
    }

    @Configuration
    @ConditionalOnProperty(name = ["jeon.stage.emailidentity.enabled"], havingValue = "true")
    class EmailIdentityStageProviderConfiguration {

        @Bean
        fun emailIdentityStageProvider() = EmailIdentityStageProvider()
    }

    @Configuration
    @ConditionalOnProperty(name = ["jeon.stage.dummy.enabled"], havingValue = "true")
    class DummyStageProviderConfiguration {

        @Bean
        fun dummyStageProvider() = DummyStageProvider()
    }
}
