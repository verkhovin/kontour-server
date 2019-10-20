package io.kontour.server.api.user.model

data class User(
    val id: String?,
    val login: String,
    val name: String,
    val email: String,
    val pictureUrl: String = "",
    val active: Boolean = true
)