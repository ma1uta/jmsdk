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

package io.github.ma1uta.jeon.server.exception

import io.github.ma1uta.jeon.server.service.UserInteractiveException
import io.github.ma1uta.matrix.ErrorResponse
import io.github.ma1uta.matrix.client.model.ErrorMessage
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class JServerExceptionHandler: io.github.ma1uta.jeon.exception.ExceptionHandler() {

    val logger = LoggerFactory.getLogger(ExceptionHandler::class.java)!!

    @ExceptionHandler(Throwable::class)
    @ResponseBody
    override fun handle(exception: Throwable): ErrorResponse {
        logger.error("Exception:", exception)
        return when (exception) {
            is UserInteractiveException -> {
                val authenticationFlows = exception.authenticationFlows
                ErrorMessage(authenticationFlows.errcode, exception.message,
                        authenticationFlows.completed, authenticationFlows.flows,
                        authenticationFlows.params, authenticationFlows.session)
            }
            else -> super.handle(exception)
        }
    }
}
