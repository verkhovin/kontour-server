package io.kontour.server.messaging.connection

interface ChatConnectedMembersRepository {
    fun userIds(chatId: String): Collection<String>

    fun addUser(userId: String, chatIds: Collection<String>)

    fun deleteUser(userId: String, chatIds: Collection<String>)
}