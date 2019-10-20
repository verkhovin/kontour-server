package io.kontour.server.storage.user.repo

import io.kontour.server.storage.user.model.User

interface UserRepository {
    fun save(user: User): User
    fun get(id: String): User
}
