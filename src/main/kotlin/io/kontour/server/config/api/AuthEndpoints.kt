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

package io.kontour.server.config.api

import io.kontour.server.api.user.TokenIssuer
import io.kontour.server.common.userId
import io.ktor.application.call
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.post
import org.koin.ktor.ext.get

fun Routing.authEndpoints() {
    val tokenIssuer: TokenIssuer = get()

    authenticate("get-token") {
        post("/auth/signin") {
            call.respond(
                tokenIssuer.generateTokenPair(call.authentication.principal<UserIdPrincipal>()!!.name)
            )
        }
    }

    authenticate("refresh") {
        post("/auth/refresh") {
            call.respond(
                tokenIssuer.refresh(call.authentication.principal<JWTPrincipal>()!!.userId)
            )
        }
        post("/auth/refresh-full") {
            call.respond(
                tokenIssuer.refreshFull(call.authentication.principal<JWTPrincipal>()!!.userId)
            )
        }
    }
}