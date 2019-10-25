package io.kontour.server.messaging.connection

import redis.clients.jedis.Jedis

class RedisChatConnectedUsersRepository(private val jedis: Jedis) : ChatConnectedMembersRepository {
    override fun userIds(chatId: String): Set<String> = jedis.smembers("chat_user:$chatId")

    override fun addUser(userId: String, chatIds: Collection<String>) {
        chatIds.forEach { chatId ->
            jedis.sadd("chat_user:$chatId", userId)
        }
    }

    override fun deleteUser(userId: String, chatIds: Collection<String>) {
        chatIds.forEach { chatId ->
            jedis.srem("chat_user:$chatId", userId)
        }
    }
}