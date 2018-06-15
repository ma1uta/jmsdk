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

import io.github.ma1uta.matrix.ErrorResponse
import io.github.ma1uta.matrix.client.model.auth.LoginRequest
import io.github.ma1uta.matrix.client.model.auth.LoginResponse
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional
import javax.inject.Inject

@Transactional
@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthTest {

    @Inject
    lateinit var restTemplate: TestRestTemplate

    @Before
    @Sql(scripts = ["/sql/loginPassword.sql"], executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    fun before() {

    }

    @Ignore
    @Test
    fun loginFailed() {
        val loginRequest = LoginRequest()
        loginRequest.type = "m.login.password"
        loginRequest.user = "dummy"
        loginRequest.password = "dummy"
        val loginResponse =
                restTemplate.postForObject("/_matrix/client/r0/login", loginRequest, ErrorResponse::class.java) ?: error("empty response")
        assert(loginResponse.errcode == ErrorResponse.Code.M_NOT_FOUND)
    }

    @Ignore
    @Test
    fun loginPassword() {
        val loginRequest = LoginRequest()
        loginRequest.type = "m.login.password"
        loginRequest.user = "dummy"
        loginRequest.password = "dummy"
        restTemplate.postForObject("/_matrix/client/r0/login", loginRequest, LoginResponse::class.java) ?: error("empty response")
    }
}
