package io.kontour.server.messaging

import com.google.gson.Gson
import io.kontour.server.messaging.connection.ChatToConnectedUserRepository
import io.kontour.server.messaging.connection.Connection
import io.kontour.server.messaging.connection.ConnectionRepository
import io.kontour.server.messaging.messages.WelcomeMessage
import io.kontour.server.messaging.user.TokenStore
import io.kontour.server.storage.chat.ChatRepository
import io.ktor.network.selector.ActorSelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.io.readUTF8Line
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.net.InetSocketAddress

class MessagingServer(
    private val port: Int,
    private val tokenStore: TokenStore,
    private val messageDispatcher: MessageDispatcher,
    private val chatToConnectedUserRepository: ChatToConnectedUserRepository,
    private val chatRepository: ChatRepository,
    private val connectionRepository: ConnectionRepository,
    private val gson: Gson
) {
    private var keepRunning = true
    suspend fun start() = runBlocking {
        val server = upServer()
        while(keepRunning) {
            val socket = server.accept()
            launch {
                try {
                    val connection = openConnection(socket)
                    connection.listen()
                } catch (e: Exception) {
                    socket.close()
                }
            }
        }
    }

    fun stop() {
        keepRunning = false;
    }

    private suspend fun openConnection(socket: Socket): Connection {
        //TODO REFACTOR
        val input = socket.openReadChannel()
        val output = socket.openWriteChannel(autoFlush = true)
        val plainMessage = input.readUTF8Line()
        val welcomeMessage = gson.fromJson(plainMessage, WelcomeMessage::class.java)
        val userId = tokenStore.getUserIdByToken(welcomeMessage.token) //here we need to validate if user can connect to chat workspace
        val chatIds = chatRepository.getChatIdsByUserId(userId)
        chatToConnectedUserRepository.addUser(userId, chatIds)
        return Connection(socket, input, output, messageDispatcher).also {
            connectionRepository.registerConnection(userId, it)
        }
    }

    private fun upServer() = aSocket(ActorSelectorManager(Dispatchers.IO)).tcp().bind(InetSocketAddress(port))
}
