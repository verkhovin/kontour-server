package io.kontour.server.messaging.connection

import redis.clients.jedis.Jedis

class RedisChatToConnectedUserRepository(private val jedis: Jedis) : ChatToConnectedUserRepository {
    override fun userIds(chatId: String): Set<String> = jedis.smembers("chat_user:$chatId")

    override fun addUser(userId: String, chatIds: Collection<String>) {
        chatIds.forEach { chatId ->
            jedis.sadd("chat_user:$chatId", userId)
        }
    }
}