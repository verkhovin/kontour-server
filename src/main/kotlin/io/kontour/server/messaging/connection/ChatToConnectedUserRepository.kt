package io.kontour.server.messaging.connection


import redis.clients.jedis.Jedis

private const val SPACE_ID: Int = 512

interface ChatToConnectedUserRepository {
    //TODO find the most fast implementation
    fun userIdsByChatId(chatId: String): Collection<String>
}