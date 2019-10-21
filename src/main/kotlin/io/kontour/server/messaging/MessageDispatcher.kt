package io.kontour.server.messaging

import io.kontour.server.messaging.connection.ChatToConnectedUserRepository
import io.kontour.server.messaging.connection.Connection
import io.kontour.server.messaging.messages.ChatMessage
import io.kontour.server.messaging.connection.ConnectionRepository

class MessageDispatcher(
    private val connectionRepository: ConnectionRepository,
    private val chatToConnectedUserRepository: ChatToConnectedUserRepository
){
    fun registerConnection(userId: String, connection: Connection) {
        connectionRepository.registerConnection(userId, connection)
    }

    suspend fun handleChatMessage(message: ChatMessage) {
        chatToConnectedUserRepository.userIds(message.chatId).forEach {
            connectionRepository.connectionForUser(it).send(message)
        }
    }
}
