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

package io.kontour.server.config.di

import com.google.gson.Gson
import com.mongodb.client.MongoDatabase
import io.kontour.server.common.jwtVerifier
import io.kontour.server.messaging.ConnectionDispatcher
import io.kontour.server.messaging.MessageDispatcher
import io.kontour.server.messaging.MessagingServer
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.connection.OnlineInfoRepository
import io.kontour.server.messaging.connection.RedisOnlineInfoRepository
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.storage.chat.ChatRepository
import io.kontour.server.storage.chat.MongoChatRepository
import org.koin.core.module.Module
import redis.clients.jedis.Jedis

fun Module.configureMessagingComponents() {
    single { TokenStore() }
    single { Jedis() }
    single { jwtVerifier(get()) }
    single { MongoChatRepository(get<MongoDatabase>().getCollection("chats")) as ChatRepository }
    single { RedisOnlineInfoRepository(get()) as OnlineInfoRepository }
    single { ConnectionStore() }
    single { MessageDispatcher(get(), get()) }
    single { ConnectionDispatcher(get(), get(), get(), get(), get(), Gson()) }
    single { MessagingServer(getProperty("messaging.server.port"), get()) }
}
