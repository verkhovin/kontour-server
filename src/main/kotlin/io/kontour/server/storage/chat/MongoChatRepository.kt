package io.kontour.server.storage.chat

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class MongoChatRepository(private val chatCollection: MongoCollection<Document>): ChatRepository {
    override fun getChatIdsByUserId(userId: String): List<String> =
        chatCollection
            .find(eq("userIds", userId))
            .map { it.getObjectId("_id").toHexString() }
            .toList()
}