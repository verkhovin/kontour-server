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
import com.mongodb.client.model.Updates.combine
import com.mongodb.client.model.Updates.push
import io.kontour.server.common.objectId
import org.bson.Document
import org.bson.types.ObjectId

class MongoChatRepository(private val chatCollection: MongoCollection<Document>) : ChatRepository {
    override fun save(chat: Chat): String {
        val chatId = objectId(chat.id)
        chatCollection.insertOne(chat.toDocument(chatId))
        return chatId.toHexString()
    }

    override fun find(chatId: String): Chat =
        chatCollection.find(eq("_id", ObjectId(chatId))).first()?.toChat() ?: throw Exception("Chat with id $chatId not found")


    override fun addUser(chatId: String, userId: String) {
        chatCollection.updateOne(eq("_id", chatId), combine(push("usersId", userId)))
    }

    override fun addChat(parentChatId: String, childChatId: String) {
        chatCollection.updateOne(eq("_id", parentChatId), combine(push("chatIds", childChatId)))
    }

    override fun getChatIdsByUserId(userId: String): List<String> =
        chatCollection
            .find(eq("userIds", userId))
            .map { it.getObjectId("_id").toHexString() }
            .toList()

    private fun Document.toChat() = Chat(
        getObjectId("_id").toHexString(),
        ChatType.valueOf(getString("chatType")),
        getString("name"),
        getList("userIds", String::class.java),
        getList("chatIds", String::class.java)
    )

    private fun Chat.toDocument(id: ObjectId) = Document("_id", id)
        .append("chatType", chatType.toString())
        .append("name", name)
        .append("userIds", userIds)
        .append("chatIds", chatIds)
}
