package io.kontour.server

import io.kontour.server.api.apiModule
import io.kontour.server.api.apiRoutes
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.server.engine.commandLineEnvironment
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.context.startKoin
import java.text.DateFormat
import java.util.logging.Level

fun Application.configuration() {
    install(CallLogging) {
        level = org.slf4j.event.Level.DEBUG
    }
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    apiRoutes()
}

fun main(args: Array<String>) {
    startKoin {
        modules(apiModule)
    }

    embeddedServer(Netty, commandLineEnvironment(args)).start()
}