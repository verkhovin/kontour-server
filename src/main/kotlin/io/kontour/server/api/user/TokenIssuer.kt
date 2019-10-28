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
import io.kontour.server.api.user.dto.TokenPair
import io.kontour.server.config.AuthProperties
import java.util.*

class TokenIssuer(private val authProperties: AuthProperties) {
    fun generateTokenPair(userId: String): TokenPair =
        tokenPair(userId)

    fun refreshFull(userId: String): TokenPair = tokenPair(userId)

    fun refresh(userId: String): TokenPair = TokenPair(
        accessToken(userId),
        "NotSupplied"
    )

    private fun tokenPair(userId: String): TokenPair {
        return TokenPair(
            accessToken(userId),
            refreshToken(userId)
        )
    }

    private fun accessToken(userId: String) =
        token(userId, "kontour-access", authProperties.accessExpiresAfterSeconds)

    private fun refreshToken(userId: String) =
        token(userId, "kontour-refresh", authProperties.refreshExpiresAfterSeconds)

    private fun token(userId: String, subject: String, expiresAfterSeconds: Int): String =
        JWT.create()
            .withSubject(subject)
            .withIssuer(authProperties.jwtIssuer)
            .withClaim("id", userId)
            .withExpiresAt(getExpiration(expiresAfterSeconds))
            .sign(Algorithm.HMAC512(authProperties.jwtSecret))

    private fun getExpiration(expiresAfterSeconds: Int) = Date(System.currentTimeMillis() + expiresAfterSeconds * 1000)
}