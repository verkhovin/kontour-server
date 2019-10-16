package io.kontour.server.api.user

import io.kontour.server.api.user.dto.CreateUserRequest
import io.kontour.server.api.user.dto.CreateUserResponse
import io.kontour.server.api.user.dto.UserDTO
import io.kontour.server.api.user.model.User

class UserService(private val userRepository: UserRepository) {
    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
        val user = User(
            createUserRequest.user.id,
            createUserRequest.user.login,
            createUserRequest.user.name,
            createUserRequest.user.email,
            "",
            true
        )
        val savedUser = userRepository.save(user)

        return CreateUserResponse(savedUser.toDTO())
    }

    fun User.toDTO() = UserDTO(id, login, name, email)
}