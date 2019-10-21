package io.kontour.server.messaging.connection

interface ChatToConnectedUserRepository {
    fun userIds(chatId: String): Collection<String>

    fun addUser(userId: String, chatIds: Collection<String>)
}