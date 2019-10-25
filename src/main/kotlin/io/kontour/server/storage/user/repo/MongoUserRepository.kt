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

package io.kontour.server.storage.user.repo

import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters.eq
import io.kontour.server.storage.user.model.User
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
