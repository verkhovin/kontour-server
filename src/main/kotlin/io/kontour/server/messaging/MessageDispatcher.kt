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
        /*
        1. get all users of the chat the message is addresed to
        2. get connections for that users
        3. send them message
         */
    }
}
