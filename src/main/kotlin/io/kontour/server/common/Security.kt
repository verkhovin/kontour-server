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

package io.kontour.server.common

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import io.kontour.server.config.AuthProperties
import io.ktor.auth.jwt.JWTPrincipal
import org.mindrot.jbcrypt.BCrypt

fun jwtVerifier(authProperties: AuthProperties) = JWT.require(Algorithm.HMAC512(authProperties.jwtSecret))
    .withIssuer(authProperties.jwtIssuer)
    .build()


fun validatePasswordHash(password: String, hash: String) = BCrypt.checkpw(password, hash)

fun hashPassword(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())!!

fun validatePassword(password: String) {
    if (password.isNullOrEmpty() || password.length < 8) throw WeakPasswordException()
}

val Payload.userId: String
    get() = this.claims["id"]?.asString() ?: throw Exception("Field id not found for the token")

val JWTPrincipal.userId: String
    get() = this.payload.userId