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

import io.kontour.server.config.configureApiRoutes
import io.kontour.server.config.configureAuth
import io.kontour.server.messaging.MessagingServer
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.text.DateFormat

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

}