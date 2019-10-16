package io.kontour.server.api.user.dto

data class UserDTO(
    val id: String,
    val login: String,
    val name: String,
    val email: String
)