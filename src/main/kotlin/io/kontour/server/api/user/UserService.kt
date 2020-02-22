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

package io.kontour.server.api.user

import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.impl.JWTParser
import io.kontour.server.api.user.dto.CreateUserRequest
import io.kontour.server.api.user.dto.CreateUserResponse
import io.kontour.server.api.user.dto.UserDTO
import io.kontour.server.common.validatePassword
import io.kontour.server.service.security.SetPasswordRequestService
import io.kontour.server.storage.user.model.Credentials
import io.kontour.server.storage.user.model.User
import io.kontour.server.storage.user.model.UserCredentialsNotFoundException
import io.kontour.server.storage.user.repo.MongoUserRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class UserService(
    private val mongoUserRepository: MongoUserRepository,
    private val setPasswordRequestService: SetPasswordRequestService,
    private val jwtVerifier: JWTVerifier
) {
    fun createUser(createUserRequest: CreateUserRequest): CreateUserResponse {
        val user = User(
            null,
            createUserRequest.user.login,
            createUserRequest.user.name,
            createUserRequest.user.email,
            "",
            true
        )
        val savedUserId = mongoUserRepository.save(user)
        val savedUser = mongoUserRepository.get(savedUserId)
        sendSetPasswordRequest(savedUser)
        return CreateUserResponse(savedUser.toDTO())
    }

    fun setPassword(token: String, password: String) {
        val decoded = jwtVerifier.verify(token)
        val userId = decoded.claims["id"]?.asString() ?: throw Exception("bad token. id not found in claims")

        try {
            val user = mongoUserRepository.getPasswordHash(userId)
        } catch (e: UserCredentialsNotFoundException) {
            setCredentials(userId, password)
            return
        }

        throw Exception("Password has been already set.")
    }

    fun setCredentials(userId: String, password: String) {
        validatePassword(password)
        mongoUserRepository.saveCredentials(
            Credentials(userId, password)
        )
    }

    fun getUser(id: String) = mongoUserRepository.get(id).toDTO()

    private fun User.toDTO() = UserDTO(id, login, name, email, pictureUrl, active)

    private fun sendSetPasswordRequest(user: User) {
        GlobalScope.launch {
            setPasswordRequestService.requestPassword(user)
        }
    }
}
