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

package io.kontour.server.messaging

import io.kontour.server.messaging.connection.KontourSocket
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import io.ktor.util.error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class MessagingServer(
    private val port: Int,
    private val connectionDispatcher: ConnectionDispatcher
) {
    private var keepRunning = true
    suspend fun start() = runBlocking {
        val server = upServer()
        while (keepRunning) {
            val socket = server.accept()
            launch {
                try {
                    val kontourSocket = KontourSocket(socket)
                    val connection = connectionDispatcher.openConnection(kontourSocket)
                    try {
                        connection.listen()
                    } finally {
                        connectionDispatcher.closeConnection(connection)
                    }
                } catch (e: Throwable) {
                    logger.error(e)
                } finally {
                    socket.close()
                }
            }
        }
    }

    fun stop() {
        keepRunning = false;
    }

    private fun upServer() = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(port))


    companion object {
        val logger = LoggerFactory.getLogger(MessagingServer::class.java)
    }

}
