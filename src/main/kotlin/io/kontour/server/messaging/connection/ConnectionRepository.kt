package io.kontour.server.messaging.connection

import io.kontour.server.messaging.connection.Connection

class ConnectionRepository {
    private val connectionsMap = mutableMapOf<String, Connection>()

    fun registerConnection(userId: String, socket: Connection) {
        connectionsMap[userId] = socket
    }

    fun connectionForUser(userId: String) = connectionsMap[userId] ?: throw Exception("User is offline")
}
