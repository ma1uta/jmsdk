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

package io.github.ma1uta.jeon.server.interaction.stage

import io.github.ma1uta.jeon.server.ServerProperties
import io.github.ma1uta.jeon.server.interaction.StageProvider
import io.github.ma1uta.matrix.client.model.account.AuthenticationData
import io.github.ma1uta.matrix.client.model.auth.AuthType
import org.springframework.web.client.RestTemplate
import java.net.URL

class ReCaptchaStageProvider(private val serverProperties: ServerProperties, private val restTemplate: RestTemplate) : StageProvider {
    override fun stage() = AuthType.RECAPTCHA

    override fun params() = mutableMapOf(Pair("public_key", serverProperties.reCaptchaKey))

    override fun authenticate(authenticationData: AuthenticationData): Boolean {
        if (authenticationData.type != AuthType.RECAPTCHA || authenticationData.response.isNullOrBlank()) {
            return false
        }

        val url = URL(serverProperties.reCaptchaUrl + "?secret={secret}&response={response}&remoteip={remoteip}").toURI()
        val response = restTemplate.postForEntity(url,
                mutableMapOf(Pair("secret", serverProperties.reCaptchaSecretKey), Pair("response", authenticationData.response),
                        Pair("remoteip", "")), Map::class.java)
        val success = response.body?.get("success") ?: false

        return success is Boolean && success
    }
}
