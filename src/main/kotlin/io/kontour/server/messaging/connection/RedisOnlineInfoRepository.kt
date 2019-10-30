/*
 * Kontour Server
 * Copyright (C) 2019  Nikita Verkhovin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.kontour.server.messaging.connection

import redis.clients.jedis.Jedis

class RedisOnlineInfoRepository(private val jedis: Jedis) : OnlineInfoRepository {
    override fun userIds(chatId: String): Set<String> = jedis.smembers("chat_user:$chatId")

    override fun addUser(userId: String, chatIds: Collection<String>, connectionToken: String) {
        chatIds.forEach { chatId ->
            jedis.sadd("chat_user:$chatId", userId)
        }
        jedis.set("connection_token:$connectionToken", userId)
    }

    override fun deleteUser(userId: String, chatIds: Collection<String>, connectionToken: String) {
        chatIds.forEach { chatId ->
            jedis.srem("chat_user:$chatId", userId)
        }
        jedis.del("connection_token:$connectionToken")
    }

    override fun userIdByToken(connectionToken: String): String =
        jedis.get("connection_token:$connectionToken") ?: throw Exception("Token $connectionToken not found")
}