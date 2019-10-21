package io.kontour.server.messaging.connection

import redis.clients.jedis.Jedis

class RedisChatToConnectedUserRepository(private val jedis: Jedis): ChatToConnectedUserRepository {
    override fun userIdsByChatId(chatId: String): Set<String> = jedis.smembers("chat_user:$chatId")

}