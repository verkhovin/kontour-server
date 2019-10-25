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

package io.kontour.server

import com.google.gson.Gson
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.kontour.server.api.apiRoutes
import io.kontour.server.api.user.UserService
import io.kontour.server.messaging.ConnectionDispatcher
import io.kontour.server.messaging.MessageDispatcher
import io.kontour.server.messaging.MessagingServer
import io.kontour.server.messaging.connection.ChatConnectedMembersRepository
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.connection.RedisChatConnectedUsersRepository
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.storage.chat.ChatRepository
import io.kontour.server.storage.chat.MongoChatRepository
import io.kontour.server.storage.user.repo.MongoUserRepository
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.dsl.module
import redis.clients.jedis.Jedis
import java.text.DateFormat

val kontourModule = module {
    //api
    single { MongoClients.create().getDatabase("kontour") }
    single { MongoUserRepository(get<MongoDatabase>().getCollection("users")) }
    single { UserService(get()) }

    //messaging
    single { TokenStore() }
    single { Jedis() }
    single { MongoChatRepository(get<MongoDatabase>().getCollection("chats")) as ChatRepository}
    single { RedisChatConnectedUsersRepository(get()) as ChatConnectedMembersRepository }
    single { ConnectionStore() }
    single { MessageDispatcher(get(), get()) }
    single { ConnectionDispatcher(get(), get(), get(), get(), get(), Gson()) }
    single { MessagingServer(8082, get()) }
}

fun Application.configuration() {
    install(CallLogging) {
        level = org.slf4j.event.Level.DEBUG
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    apiRoutes()
}

fun main(args: Array<String>) {
    startKoin {
        modules(kontourModule)
    }

    KontourServer().start(args)
}

class KontourServer() : KoinComponent {
    fun start(args: Array<String>) = runBlocking {
        embeddedServer(Netty, commandLineEnvironment(args)).start()
        val messagingServer: MessagingServer by inject()
        try {
            messagingServer.start()
        } finally {
            messagingServer.stop()
        }
    }
}