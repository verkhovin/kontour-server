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

package io.kontour.server.api

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.kontour.server.storage.user.repo.MongoUserRepository
import io.kontour.server.api.user.UserService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.koin.dsl.module
import org.koin.ktor.ext.get

fun Application.apiRoutes() {
    val userService: UserService = get()

    routing {
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
