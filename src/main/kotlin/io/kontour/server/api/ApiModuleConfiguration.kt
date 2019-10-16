package io.kontour.server.api

import io.kontour.server.api.user.UserRepository
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
import org.koin.ktor.ext.inject

val apiModule = module {
    single { UserRepository() }
    single { UserService(get()) }
}

fun Application.apiRoutes() {
    val userService by inject<UserService>()

    routing {
        get("/api/version") {
            call.respondText("Alpha")
        }
        post("/api/user") {
            call.respond(userService.createUser(call.receive()))
        }
    }
}