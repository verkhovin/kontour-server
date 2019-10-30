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

import com.auth0.jwt.JWTVerifier
import com.google.gson.Gson
import io.kontour.server.common.userId
import io.kontour.server.messaging.connection.Connection
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.connection.KontourSocket
import io.kontour.server.messaging.connection.OnlineInfoRepository
import io.kontour.server.messaging.messages.WelcomeMessage
import io.kontour.server.storage.chat.ChatRepository
import io.ktor.util.cio.write
import kotlinx.coroutines.io.readUTF8Line
import java.util.*

class ConnectionDispatcher(
    private val onlineInfoRepository: OnlineInfoRepository,
    private val chatRepository: ChatRepository,
    private val connectionStore: ConnectionStore,
    private val messageDispatcher: MessageDispatcher,
    private val tokenVerifier: JWTVerifier,
    private val gson: Gson
) {
    suspend fun openConnection(socket: KontourSocket): Connection {
        val userId =
            welcome(socket.input.readUTF8Line()!!) //here we need to validate if user can connect to chat workspace
        val connectionToken = UUID.randomUUID().toString()
        onlineInfoRepository.addUser(userId, chatRepository.getChatIdsByUserId(userId), connectionToken)
        socket.output.write(connectionToken)
        return Connection(userId, connectionToken, socket, messageDispatcher).also {
            connectionStore.registerConnection(userId, it)
        }
    }

    fun closeConnection(connection: Connection) {
        connection.close()
        val userId = connection.userId
        onlineInfoRepository.deleteUser(
            userId,
            chatRepository.getChatIdsByUserId(userId),
            onlineInfoRepository.userIdByToken(connection.token)
        )
        connectionStore.removeConnection(userId)
    }

    private fun welcome(plainMessage: String): String {
        val welcomeMessage = gson.fromJson(plainMessage, WelcomeMessage::class.java)
        return tokenVerifier.verify(welcomeMessage.token).userId
    }
}