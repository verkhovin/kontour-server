package io.kontour.server.storage.chat

data class Chat (
    val id: String?,
    val chatType: ChatType,
    val name: String,
    val userIds: Set<String>
)

enum class ChatType {
    DIRECT, THREAD, CHANNEL
}
