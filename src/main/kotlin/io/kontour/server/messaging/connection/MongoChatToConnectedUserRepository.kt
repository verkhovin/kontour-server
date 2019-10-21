package io.kontour.server.messaging.connection

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import org.bson.Document

class MongoChatToConnectedUserRepository(private val mongo: MongoCollection<Document>) : ChatToConnectedUserRepository {
    override fun userIdsByChatId(chatId: String): Collection<String> {
        return userIdWhenStoredInOneCollection(chatId)
    }

    private fun userIdWhenStoredInOneCollection(chatId: String) = mongo.find(eq("chat_id", chatId)).first()?.getList("onlineUsers", String::class.java)!!

    private fun usersIdsWhenStoredInMultipleCollections(chatId: String)  = mongo.find(eq("chat_id", chatId)).toSet()

}