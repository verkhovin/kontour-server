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

import io.kontour.server.storage.user.repo.UserRepository
import kotlin.Exception

class AuthService(
    private val userRepository: UserRepository,
    private val passwordHashValidator: (password: String, hash: String) -> Boolean
) {
    fun authenticate(username: String, password: String): String {
        val user = userRepository.findByUsername(username)
        if (passwordHashValidator(password, userRepository.getPasswordHash(user.id!!)))return user.id
        else throw Exception("Not authenticated")
    }
}