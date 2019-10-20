package io.kontour.server.messaging.connection

import com.google.gson.Gson
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.messaging.messages.ChatMessage
import io.kontour.server.messaging.messages.WelcomeMessage
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.io.readUTF8Line

class Connection(private val socket: Socket, private val tokenStore: TokenStore) {
    private val input = socket.openReadChannel()
    private val output = socket.openWriteChannel(autoFlush = true)

    private val gson = Gson()

    private var opened = true

    suspend fun welcome(): String {
        val welcomeMessage = gson.fromJson(input.readUTF8Line(), WelcomeMessage::class.java)
        return tokenStore.getUserIdByToken(welcomeMessage.token)
    }

    suspend fun listen() {
        while (opened) {
            val message = gson.fromJson(input.readUTF8Line(), ChatMessage::class.java)

        }
    }

    fun close() {
        opened = false
    }
}
