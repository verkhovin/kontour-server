package io.kontour.server.api.user

import io.kontour.server.api.user.dto.CreateUserRequest
import io.kontour.server.api.user.dto.CreateUserResponse
import io.kontour.server.api.user.dto.UserDTO
import io.kontour.server.storage.user.model.User
import io.kontour.server.storage.user.repo.MongoUserRepository

class UserService(private val mongoUserRepository: MongoUserRepository) {
    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
        val user = User(
            null,
            createUserRequest.user.login,
            createUserRequest.user.name,
            createUserRequest.user.email,
            "",
            true
        )
        val savedUser = mongoUserRepository.save(user)

        return CreateUserResponse(savedUser.toDTO())
    }

    fun getUser(id: String) = mongoUserRepository.get(id).toDTO()

    fun User.toDTO() = UserDTO(id, login, name, email, pictureUrl, active)
}
