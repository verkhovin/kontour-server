package io.kontour.server.storage.chat

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.eq

class MongoChatRepository(private val mongo: MongoDatabase): ChatRepository {
    override fun getChatIdsByUserId(userId: String): List<String> =
        mongo.getCollection("chat_user")
            .find(eq("user_id", userId))
            .map { it.getString("chat_id") }
            .toList()
}