/*
 * Kontour Server
 * Copyright (C) 2019  Nikita Verkhovin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.kontour.server.storage.chat

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.client.model.Sorts.orderBy
import io.kontour.server.common.objectId
import org.bson.BasicBSONObject
import org.bson.Document
import org.bson.types.ObjectId

class MongoMessageRepository(private val messageCollection: MongoCollection<Document>) : MessageRepository {
    override fun save(chatMessage: ChatMessage) {
        messageCollection.insertOne(chatMessage.toDocument(objectId(chatMessage.id)))
    }

    override fun getLastMessages(chatId: String, count: Int): List<ChatMessage> =
        messageCollection
            .find(eq("chatId", chatId))
            .sort(orderBy(ascending("date")))
            .limit(count)
            .toList()
            .map { it.toChatMessage() }

    private fun ChatMessage.toDocument(id: ObjectId) = Document("_id", id)
        .append("authorId", authorId)
        .append("chatId", chatId)
        .append("text", text)
        .append("date", date)
        .append("edited", Boolean)

    private fun Document.toChatMessage() = ChatMessage(
        getObjectId("_id").toHexString(),
        getString("authorId"),
        getString("chatId"),
        getString("text"),
        getDate("date"),
        getBoolean("edited")
    )
}
