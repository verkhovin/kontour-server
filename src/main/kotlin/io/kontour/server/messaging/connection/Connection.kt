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

import com.google.gson.Gson
import io.kontour.server.messaging.MessageDispatcher
import io.kontour.server.messaging.MessagingServer
import io.kontour.server.messaging.messages.ChatMessageIncome
import io.kontour.server.messaging.messages.ChatMessageOutcome
import io.ktor.util.cio.write
import io.ktor.util.error
import kotlinx.coroutines.io.readUTF8Line
import org.slf4j.LoggerFactory
import java.io.Closeable

class Connection(
    val userId: String,
    val token: String,
    private val socket: KontourSocket,
    private val messageDispatcher: MessageDispatcher
) : Closeable {
    private val gson = Gson() //TODO DI?

    private var opened = true

    suspend fun listen() {
        while (opened && socket.alive) {
            try {
                gson.fromJson(socket.input.readUTF8Line(), ChatMessageIncome::class.java).let { message ->
                    if(message.token == this.token) {
                        messageDispatcher.handleChatMessage(message)
                    } else {
                        throw Exception("Unexpected token for current connection")
                    }
                }
            } catch (e: Throwable) {
                logger.error(e)
                socket.discardInput()
            }
        }
    }

    suspend fun send(chatMessageOutcome: ChatMessageOutcome) {
        socket.output.write(gson.toJson(chatMessageOutcome))
    }

    override fun close() {
        opened = false
    }

    companion object {
        val logger = LoggerFactory.getLogger(MessagingServer::class.java)
    }
}
