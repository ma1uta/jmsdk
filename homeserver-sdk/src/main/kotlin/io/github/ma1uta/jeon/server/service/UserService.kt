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

import io.github.ma1uta.jeon.server.Query
import io.github.ma1uta.jeon.server.model.User
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.sql.ResultSet

@Service
class UserService(val query: Query, val template: NamedParameterJdbcTemplate, val passwordEncoder: BCryptPasswordEncoder) {

    fun read(userId: String): User? {
        return try {
            template.queryForObject(query.user.read, mutableMapOf(Pair("id", userId)), UserRowMapper())
        } catch (e: EmptyResultDataAccessException) {
            null
        }
    }

    fun insert(user: User) {
        template.update(query.user.insert, mutableMapOf(Pair("id", user.id),
                Pair("password", passwordEncoder.encode(user.password.trim())),
                Pair("display_name", user.displayName),
                Pair("avatar_url", user.avatarUrl),
                Pair("kind", user.kind)))
    }

    class UserRowMapper : RowMapper<User> {
        override fun mapRow(rs: ResultSet?, rowNum: Int) = User(
                rs!!.getString("id"),
                rs.getString("password"),
                rs.getString("display_name"),
                rs.getString("avatar_url")
        )
    }
}
