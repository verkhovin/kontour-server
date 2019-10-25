package io.kontour.server.messaging.connection

class ConnectionStore {
    private val connectionsMap = mutableMapOf<String, Connection>()

    fun registerConnection(userId: String, connection: Connection) {
        connectionsMap[userId] = connection
    }

    fun connectionForUser(userId: String) = connectionsMap[userId]

    fun removeConnection(userId: String) {
        connectionsMap.remove(userId)
    }
}
