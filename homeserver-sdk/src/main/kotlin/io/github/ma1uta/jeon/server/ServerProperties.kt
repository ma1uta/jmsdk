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

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jeon")
class ServerProperties {
    val versions = ArrayList<String>()

    var name = "localhost"

    var macaroon = "macaroon_seed"

    var updateLastSeen = true

    var reCaptchaKey = ""

    var reCaptchaSecretKey = ""

    var reCaptchaUrl = ""
}
