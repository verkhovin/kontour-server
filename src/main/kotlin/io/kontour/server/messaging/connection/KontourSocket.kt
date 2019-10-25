package io.kontour.server.messaging.connection

import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.coroutines.io.ByteWriteChannel

class KontourSocket(socket: Socket) : Socket by socket {
    val input: ByteReadChannel by lazy {
        socket.openReadChannel()
    }

    val output: ByteWriteChannel by lazy {
        socket.openWriteChannel(autoFlush = true)
    }

    val alive: Boolean
        get() = !input.isClosedForRead && !output.isClosedForWrite

    suspend fun discardInput() = input.discard(input.availableForRead.toLong())
}