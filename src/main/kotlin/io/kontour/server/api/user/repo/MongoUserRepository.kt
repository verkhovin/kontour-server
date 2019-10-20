package io.kontour.server.api.user.repo

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import io.kontour.server.api.user.model.User
import io.kontour.server.common.objectId
import org.bson.Document
import org.bson.types.ObjectId

class MongoUserRepository(private val userCollection: MongoCollection<Document>): UserRepository {
    override fun save(user: User): User {
        userCollection.insertOne(user.toDocument(user.id))
        return user
    }

    override fun get(id: String): User {
        return userCollection.find(eq("_id", ObjectId(id))).first()?.toUser()
            ?: throw Exception("User with id $id not found")
    }

    fun User.toDocument(id: String?): Document =
        Document("_id", objectId(id))
            .append("login", login)
            .append("name", name)
            .append("email", email)
            .append("pictureUrl", pictureUrl)
            .append("active", active)

    fun Document.toUser(): User = User(
            getObjectId("_id").toHexString(),
            getString("login"),
            getString("name"),
            getString("email"),
            getString("pictureUrl"),
            getBoolean("active")
        )
}