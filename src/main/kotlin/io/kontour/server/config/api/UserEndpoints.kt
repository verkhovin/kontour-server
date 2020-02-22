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

import io.kontour.server.api.user.UserService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.auth.authentication
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.put
import org.koin.ktor.ext.get

fun Routing.userSecuredEndpoints() {
    val userService: UserService = get()
    authenticate("access") {
        get("/api/users/{id}") {
            call.respond(userService.getUser(call.parameters["id"]!!))
        }
    }
    authenticate("password-set") {
        put("/api/users/password") {
            val token = call.parameters["token"] ?: throw Exception("bad token")
            val password = call.parameters["password"] ?: throw Exception("password was not set")
            call.respond(userService.setPassword(token, password))
        }
    }
}

fun Routing.userPublicEndpoints() {
    val userService: UserService = get()

    post("/api/users") { //TODO need to be secured under workspace admin
        call.respond(userService.createUser(call.receive()))
    }

    get("/api/version") {
        call.respondText("Alpha")
    }
}
