package io.kontour.server.messaging.connection

import com.google.gson.Gson
import io.kontour.server.messaging.MessageDispatcher
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.messaging.messages.ChatMessage
import io.kontour.server.messaging.messages.WelcomeMessage
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import io.ktor.util.cio.write
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel
import kotlinx.coroutines.io.readUTF8Line

class Connection(
    private val input: ByteReadChannel,
    private val output: ByteWriteChannel,
    private val messageDispatcher: MessageDispatcher
) {
    private val gson = Gson() //TODO DI?

    private var opened = true

    suspend fun listen() {
        while (opened) {
            val message = gson.fromJson(input.readUTF8Line(), ChatMessage::class.java)
            messageDispatcher.handleChatMessage(message)
        }
    }

    suspend fun send(chatMessage: ChatMessage) {
        output.write(gson.toJson(chatMessage))
    }

    fun close() {
        opened = false
    }
}
