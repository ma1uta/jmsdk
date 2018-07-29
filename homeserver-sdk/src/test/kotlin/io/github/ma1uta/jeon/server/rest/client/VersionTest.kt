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

import io.github.ma1uta.matrix.client.model.version.VersionsResponse
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.test.context.junit4.SpringRunner
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VersionTest {

    @Inject
    lateinit var restTemplate: TestRestTemplate

    @Ignore
    @Test
    fun version() {
        val versionsResponse = restTemplate.getForObject("/_matrix/client/versions", VersionsResponse::class.java) ?: error("empty response")
        assert(versionsResponse.versions.contentEquals(arrayOf("r0.3.0")), { "wrong version" })
    }
}
