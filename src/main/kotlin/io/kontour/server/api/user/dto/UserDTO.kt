package io.kontour.server.api.user.dto

data class UserDTO(
    var _id: String?,
    var login: String,
    var name: String,
    var email: String,
    var pictureUrl: String?,
    var active: Boolean?
)