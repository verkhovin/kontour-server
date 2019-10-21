package io.kontour.server.storage.chat

interface ChatRepository {
    fun getChatIdsByUserId(userId: String): Collection<String>
}