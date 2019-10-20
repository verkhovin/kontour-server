package io.kontour.server.messaging

import io.kontour.server.messaging.connection.Connection
import io.kontour.server.messaging.messages.ChatMessage
import io.kontour.server.messaging.connection.ConnectionRepository

class MessageDispatcher(
    private val connectionRepository: ConnectionRepository
){
    fun registerConnection(userId: String, connection: Connection) {
        connectionRepository.registerConnection(userId, connection)
    }

    fun handleChatMessage(message: ChatMessage) {

    }
}
