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
import io.kontour.server.api.user.AuthService
import io.kontour.server.api.user.TokenIssuer
import io.kontour.server.api.user.UserService
import io.kontour.server.common.hashPassword
import io.kontour.server.common.jwtVerifier
import io.kontour.server.common.validatePasswordHash
import io.kontour.server.config.AuthProperties
import io.kontour.server.config.configureApiRoutes
import io.kontour.server.config.configureAuth
import io.kontour.server.messaging.ConnectionDispatcher
import io.kontour.server.messaging.MessageDispatcher
import io.kontour.server.messaging.MessagingServer
import io.kontour.server.messaging.connection.OnlineInfoRepository
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.connection.RedisOnlineInfoRepository
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.storage.chat.ChatRepository
import io.kontour.server.storage.chat.MongoChatRepository
import io.kontour.server.storage.user.repo.MongoUserRepository
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import org.koin.core.scope.Scope
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import redis.clients.jedis.Jedis
import java.text.DateFormat

val kontourModule = module {
    fun Scope.mongoCollection(collectionName: String) = get<MongoDatabase>().getCollection(collectionName)

    //api
    single { MongoClients.create().getDatabase("kontour") }
    single { MongoUserRepository(mongoCollection("users"), mongoCollection("credentials")) }
    single { UserService(get()) { hashPassword(it) } }

    //auth
    single {
        AuthProperties(
            getProperty("jwt.secret"),
            getProperty("jwt.issuer"),
            getProperty("jwt.token.access.expiresAfterSeconds"),
            getProperty("jwt.token.refresh.expiresAfterSeconds")
        )
    }
    single { AuthService(get<MongoUserRepository>()) { pass, hash -> validatePasswordHash(pass, hash) } }
    single { TokenIssuer(get()) }

    //messaging
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

//Referred from resources/application.conf
fun Application.configureServer() {
    install(CallLogging) {
        level = org.slf4j.event.Level.DEBUG
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    configureAuth()
    configureApiRoutes()
}

fun main(args: Array<String>) {
    startKoin {
        fileProperties()
        environmentProperties()
        modules(kontourModule)
    }

    KontourServer().start()
}

class KontourServer() : KoinComponent {
    fun start() = runBlocking {
        val port = getKoin().getProperty("port", 8080)
        embeddedServer(Netty, port) {
            configureServer()
        }.start()
        val messagingServer: MessagingServer by inject()
        try {
            messagingServer.start()
        } finally {
            messagingServer.stop()
        }
    }
}