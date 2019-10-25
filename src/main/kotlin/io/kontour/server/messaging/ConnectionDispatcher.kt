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