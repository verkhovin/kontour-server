package io.kontour.server.api.user.repo

import io.kontour.server.api.user.model.User

interface UserRepository {
    fun save(user: User): User
    fun get(id: String): User
}