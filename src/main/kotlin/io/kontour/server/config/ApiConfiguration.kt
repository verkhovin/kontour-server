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

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.kontour.server.storage.user.repo.MongoUserRepository
import io.kontour.server.api.user.UserService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.jwt
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.koin.dsl.module
import org.koin.ktor.ext.get
import org.koin.ktor.ext.inject

fun Application.configureAuth(authProperties: AuthProperties) {
    install (Authentication) {
        jwt {
            val verifier: JWTVerifier = JWT.require(Algorithm.HMAC512(authProperties.jwtSecret))
                .withIssuer(authProperties.jwtIssuer)
                .build()
            verifier(verifier)
            realm = "kontour-api"
        }
    }
}

fun Application.configureApiRoutes() {
    val userService: UserService = get()

    routing {

        authenticate {
            get("/api/secret") {
                call.respondText("Success")
            }
        }

        post("/api/login") {

        }

        get("/api/version") {
            call.respondText("Alpha")
        }
        post("/api/user") {
            call.respond(userService.createUser(call.receive()))
        }
        get("/api/user/{id}") {
            call.respond(userService.getUser(call.parameters.get("id")!!))
        }
    }
}
