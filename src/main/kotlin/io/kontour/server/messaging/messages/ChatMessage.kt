package io.kontour.server.messaging.messages

class ChatMessage(
    val id: String?,
    val authorId: String,
    val chatId: String,
    val text: String
)
