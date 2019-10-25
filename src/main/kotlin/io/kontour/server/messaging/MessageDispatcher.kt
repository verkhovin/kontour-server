package io.kontour.server.messaging

import io.kontour.server.messaging.connection.ChatConnectedMembersRepository
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.messages.ChatMessage
import org.slf4j.LoggerFactory

class MessageDispatcher(
    private val connectionStore: ConnectionStore,
    private val chatConnectedMembersRepository: ChatConnectedMembersRepository
) {
    suspend fun handleChatMessage(message: ChatMessage) {
        chatConnectedMembersRepository.userIds(message.chatId).forEach {
            connectionStore.connectionForUser(it)?.send(message)
        }
    }
}
