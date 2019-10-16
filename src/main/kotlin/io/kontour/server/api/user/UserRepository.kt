package io.kontour.server.api.user

import io.kontour.server.api.user.model.User

class UserRepository {
    fun save(user: User): User {
        println("TODO user saved ${user.login}")
        return user
    }
}