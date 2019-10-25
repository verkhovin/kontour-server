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

import com.google.gson.Gson
import io.kontour.server.messaging.connection.ChatConnectedMembersRepository
import io.kontour.server.messaging.connection.Connection
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.connection.KontourSocket
import io.kontour.server.messaging.messages.WelcomeMessage
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.storage.chat.ChatRepository
import kotlinx.coroutines.io.readUTF8Line

class ConnectionDispatcher(
    private val tokenStore: TokenStore,
    private val chatConnectedMembersRepository: ChatConnectedMembersRepository,
    private val chatRepository: ChatRepository,
    private val connectionStore: ConnectionStore,
    private val messageDispatcher: MessageDispatcher,
    private val gson: Gson
) {
    suspend fun openConnection(socket: KontourSocket): Connection {
        val userId = welcome(socket.input.readUTF8Line()!!) //here we need to validate if user can connect to chat workspace
        chatConnectedMembersRepository.addUser(userId, chatRepository.getChatIdsByUserId(userId))
        return Connection(userId, socket, messageDispatcher).also {
            connectionStore.registerConnection(userId, it)
        }
    }

    fun closeConnection(connection: Connection) {
        connection.close()
        val userId = connection.userId
        chatConnectedMembersRepository.deleteUser(userId, chatRepository.getChatIdsByUserId(userId))
        connectionStore.removeConnection(userId)
    }

    private fun welcome(plainMessage: String): String {
        val welcomeMessage = gson.fromJson(plainMessage, WelcomeMessage::class.java)
        return tokenStore.getUserIdByToken(welcomeMessage.token)
    }
}