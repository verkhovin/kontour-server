package io.kontour.server.messaging

import io.kontour.server.messaging.connection.Connection
import io.kontour.server.messaging.user.TokenStore
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.aSocket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

class MessagingServer(
    private val port: Int,
    private val tokenStore: TokenStore,
    private val messageDispatcher: MessageDispatcher
) {
    private var keepRunning = true
    suspend fun start() = runBlocking {
        val server = upServer()
        while(keepRunning) {
            val socket = server.accept()
            launch {
                val connection = Connection(socket, tokenStore)
                val userId = connection.welcome()
                messageDispatcher.registerConnection(userId, connection)
            }
        }
    }

    fun stop() {
        keepRunning = false;
    }

    private fun upServer() = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(port))
}
