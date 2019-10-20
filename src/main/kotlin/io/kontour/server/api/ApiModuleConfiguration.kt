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

val apiModule = module {
    single { MongoClients.create().getDatabase("kontour") }
    single { MongoUserRepository(get<MongoDatabase>().getCollection("users")) }
    single { UserService(get()) }
}

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
