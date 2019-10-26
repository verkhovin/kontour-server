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

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.kontour.server.config.AuthProperties
import java.util.*
import kotlin.Exception

class AuthService(private val authProperties: AuthProperties) {
    fun authenticate(username: String, password: String): String {
        //password check here
        if(username == "verkhovin" && password == "pass123") {
            val userId = "5dab0f9f91cad227618f6ee1" //fetch user from mongo
            return JWT.create()
                .withSubject("Kontour auth")
                .withIssuer(authProperties.jwtIssuer)
                .withClaim("id", userId)
                .withExpiresAt(getExpiration())
                .sign(Algorithm.HMAC512(authProperties.jwtSecret))
        }
        throw Exception("Failed to authenticate")
    }

    private fun getExpiration() = Date(System.currentTimeMillis() + authProperties.expiresAfterSeconds)
}