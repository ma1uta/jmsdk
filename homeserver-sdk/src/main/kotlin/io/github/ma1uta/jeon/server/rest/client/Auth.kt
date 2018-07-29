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

package io.github.ma1uta.jeon.server.rest.client

import io.github.ma1uta.jeon.exception.MatrixException
import io.github.ma1uta.jeon.server.service.LoginService
import io.github.ma1uta.matrix.ErrorResponse
import io.github.ma1uta.matrix.client.api.AuthApi
import io.github.ma1uta.matrix.EmptyResponse
import io.github.ma1uta.matrix.client.model.auth.LoginRequest
import io.github.ma1uta.matrix.client.model.auth.LoginResponse
import org.springframework.stereotype.Component
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.core.Context

@Component
class Auth : AuthApi {

    @Inject
    lateinit var loginService: LoginService

    @Context
    lateinit var request: HttpServletRequest

    override fun login(loginRequest: LoginRequest?): LoginResponse {
        if (loginRequest == null) {
            throw MatrixException(ErrorResponse.Code.M_NOT_JSON, "Missing json.")
        }

        return loginService.login(loginRequest, request)
    }

    override fun logout(): EmptyResponse {
        loginService.logout()
        return EmptyResponse()
    }
}
