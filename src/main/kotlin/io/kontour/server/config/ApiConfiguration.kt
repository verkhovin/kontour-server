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

package io.kontour.server.config

import com.auth0.jwt.JWTVerifier
import io.kontour.server.api.user.AuthService
import io.kontour.server.config.api.authEndpoints
import io.kontour.server.config.api.chatEndpoints
import io.kontour.server.config.api.userPublicEndpoints
import io.kontour.server.config.api.userSecuredEndpoints
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.basic
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.routing.routing
import org.koin.ktor.ext.get

fun Application.configureAuth() {
    install(Authentication) {
        val jwtVerifier = get<JWTVerifier>()

        jwt(name = "access") {
            verifier(jwtVerifier)
            validate {
                if (it.payload.subject == "kontour-access") JWTPrincipal(it.payload) else null
            }
            realm = "Kontour Server"
        }

        jwt(name = "refresh") {
            verifier(jwtVerifier)
            validate {
                if (it.payload.subject == "kontour-refresh") JWTPrincipal(it.payload) else null
            }
            realm = "Kontour Server"
        }

        val authService = get<AuthService>()
        basic("get-token") {
            realm = "Kontour Server Token Pair"
            validate {
                try {
                    UserIdPrincipal(authService.authenticate(it.name, it.password))
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
}

fun Application.configureApiRoutes() {
    routing {
        authEndpoints()
        userPublicEndpoints()
        userSecuredEndpoints()
        chatEndpoints()
    }
}


