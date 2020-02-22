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

package io.kontour.server.api.pages

import com.auth0.jwt.JWTVerifier
import io.kontour.server.api.pages.dto.SetPasswordPage
import io.kontour.server.common.BadSetPasswordTokenException
import io.kontour.server.storage.user.repo.UserRepository
import kotlin.Exception

class SetPasswordService(
    private val jwtVerifier: JWTVerifier,
    private val userRepository: UserRepository,
    private val workspaceName: String
) {
    fun getPage(token: String): SetPasswordPage {
        val decoded = try {
            jwtVerifier.verify(token)
        }  catch (e: Exception) {
            throw BadSetPasswordTokenException("token not verified", e)
        }
        val userId = decoded.claims["userId"]?.asString()
            ?: throw BadSetPasswordTokenException("userId claims not found")
        val user = userRepository.get(userId)

        return SetPasswordPage(user.login, workspaceName)
    }
}