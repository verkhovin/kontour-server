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
import io.kontour.server.api.user.TokenIssuer
import io.kontour.server.api.user.UserService
import io.kontour.server.common.userId
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.basic
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
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
    val userService: UserService = get()
    val tokenIssuer: TokenIssuer = get()

    routing {

        post("/api/user") {
            call.respond(userService.createUser(call.receive()))
        }

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

        authenticate("access") {
            get("/api/version") {
                call.respondText("Alpha")
            }
            get("/api/user/{id}") {
                call.respond(userService.getUser(call.parameters["id"]!!))
            }
        }
    }
}


