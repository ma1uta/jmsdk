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

package io.github.ma1uta.jeon.server.auth

import io.github.ma1uta.jeon.exception.MatrixException
import io.github.ma1uta.jeon.server.ServerProperties
import io.github.ma1uta.jeon.server.service.DeviceService
import io.github.ma1uta.jeon.server.service.UserService
import io.github.ma1uta.matrix.ErrorResponse
import io.github.ma1uta.matrix.client.model.auth.AuthType
import io.github.ma1uta.matrix.client.model.auth.LoginRequest
import io.github.ma1uta.matrix.client.model.auth.LoginResponse
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
class TokenLoginProvider(passwordEncoder: BCryptPasswordEncoder, userService: UserService,
                         deviceService: DeviceService, serverProperties: ServerProperties) :
        AbstractPasswordLoginProvider(passwordEncoder, userService, deviceService, serverProperties) {

    override fun login(loginRequest: LoginRequest, request: HttpServletRequest): LoginResponse? {
        if (loginRequest.type.isNullOrBlank() || AuthType.TOKEN != loginRequest.type) {
            return null
        }
        if (loginRequest.token.isNullOrBlank()) {
            throw MatrixException(ErrorResponse.Code.M_FORBIDDEN, "Invalid token", null, 403)
        }

        //TODO receive username and password by token via identity server
        val username = "dummy"
        val password = "dummy"

        return authenticate(username, password, loginRequest, request)
    }
}
