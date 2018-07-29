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

package io.github.ma1uta.jeon.server.service

import com.github.nitram509.jmacaroons.MacaroonsBuilder
import io.github.ma1uta.jeon.server.Query
import io.github.ma1uta.jeon.server.ServerProperties
import io.github.ma1uta.jeon.server.interaction.FlowProvider
import io.github.ma1uta.jeon.server.interaction.StageProvider
import io.github.ma1uta.matrix.ErrorResponse
import io.github.ma1uta.matrix.client.model.auth.AuthenticationFlows
import io.github.ma1uta.matrix.client.model.auth.AuthenticationStage
import io.github.ma1uta.matrix.client.model.auth.UserInteractiveData
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class UserInteractiveService(val query: Query, val namedParameterJdbcTemplate: NamedParameterJdbcTemplate,
                             val serverProperties: ServerProperties) {

    @Autowired(required = false)
    var flowProviders: List<FlowProvider>? = null

    @Autowired(required = false)
    var stageProviders: List<StageProvider>? = null

    fun validate(userInteractiveData: UserInteractiveData) {
        if (stageProviders == null || flowProviders == null) {
            return
        }

        var session = userInteractiveData.auth?.session
        if (userInteractiveData.auth == null || session.isNullOrBlank()) {
            session = MacaroonsBuilder.create(serverProperties.name, serverProperties.macaroon, "session").serialize()
            proceedWithUserInteractiveSession(session, null, null, null)
        }

        val newCompletedStage: String? = (0..stageProviders!!.size)
                .firstOrNull {
                    stageProviders!![it].stage() == userInteractiveData.auth.type && stageProviders!![it].authenticate(
                            userInteractiveData.auth)
                }
                ?.let { stageProviders!![it].stage() }

        val completed =
                namedParameterJdbcTemplate.queryForList(query.userInteractiveSession.read, mutableMapOf(Pair("session", session)),
                        String::class.java)

        if (newCompletedStage == null) {
            proceedWithUserInteractiveSession(session!!, completed.toTypedArray(), ErrorResponse.Code.M_FORBIDDEN,
                    "Wrong authentication")
        }

        completed.add(newCompletedStage)
        val completedFlow = (0..flowProviders!!.size).any {
            completed.containsAll(flowProviders!![it].stages.map { stageProvider -> stageProvider.stage() })
        }

        if (completedFlow) {
            namedParameterJdbcTemplate.update(query.userInteractiveSession.delete, mutableMapOf(Pair("session", session)))
        } else {
            namedParameterJdbcTemplate.update(query.userInteractiveSession.update,
                    mutableMapOf(Pair("session", session), Pair("completed", completed)))
            proceedWithUserInteractiveSession(session!!, completed.toTypedArray(), null, null)
        }
    }

    protected fun proceedWithUserInteractiveSession(session: String, completed: Array<String>?, errcode: String?, error: String?) {
        val authenticationFlows = AuthenticationFlows()
        authenticationFlows.session = session
        authenticationFlows.completed = completed
        authenticationFlows.errcode = errcode
        authenticationFlows.error = error
        authenticationFlows.flows = arrayOfNulls(flowProviders!!.size)
        authenticationFlows.params = mutableMapOf<String, MutableMap<String, String>>()
        for (i in 0..flowProviders!!.size) {
            val authenticationStage = AuthenticationStage()
            authenticationStage.stages = arrayOfNulls(flowProviders!![i].stages.size)
            for (j in 0..flowProviders!![i].stages.size) {
                val stageProvider = flowProviders!![i].stages[j]
                authenticationStage.stages[i] = stageProvider.stage()
                authenticationFlows.params[stageProvider.stage()] = stageProvider.params()
            }
            authenticationFlows.flows[i] = authenticationStage
        }

        throw UserInteractiveException(authenticationFlows)
    }
}
