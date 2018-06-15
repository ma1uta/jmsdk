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
import io.github.ma1uta.jeon.server.model.Device
import io.github.ma1uta.jeon.server.model.User
import org.springframework.dao.IncorrectResultSizeDataAccessException
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service
import java.sql.ResultSet

@Service
class DeviceService(val query: Query, val template: NamedParameterJdbcTemplate) {

    fun findByToken(token: String): Device? = try {
        template.queryForObject(query.device.findByToken, mutableMapOf(Pair("token", token)), DeviceRowMapper())
    } catch (e: IncorrectResultSizeDataAccessException) {
        null
    }

    fun deleteToken(device: Device) {
        template.update(query.device.deleteToken, mutableMapOf(Pair("device_id", device.deviceId)))
    }

    fun insertOrUpdate(device: Device) =
            template.update(query.device.insertOrUpdate,
                    mutableMapOf(Pair("device_id", device.deviceId),
                            Pair("user_id", device.user.id),
                            Pair("display_name", device.displayName),
                            Pair("last_seen_ip", device.lastSeenIp),
                            Pair("last_seen_ts", device.lastSeenTs)))

    fun updateLastSeen(device: Device) = template.update(query.device.updateLastSeen,
            mutableMapOf(Pair("last_seen_ip", device.lastSeenIp),
                    Pair("last_seen_ts", device.lastSeenTs),
                    Pair("device_id", device.deviceId),
                    Pair("user_id", device.user.id)))

    class DeviceRowMapper : RowMapper<Device> {
        override fun mapRow(rs: ResultSet?, rowNum: Int) = Device(rs!!.getString("d_device_id"),
                rs.getString("d_token"), User(rs.getString("u_id"), rs.getString("u_password"),
                rs.getString("u_display_name"), rs.getString("u_avatar_url"), rs.getString("u_kind")),
                rs.getString("d_display_name"), rs.getString("d_last_seen_ip"),
                rs.getLong("d_last_seen_ts"))

    }
}
