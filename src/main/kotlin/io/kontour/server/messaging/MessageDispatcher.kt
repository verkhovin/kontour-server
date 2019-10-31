/*
 * Kontour Server
 * Copyright (C) 2019  Nikita Verkhovin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.kontour.server.messaging

import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.connection.OnlineInfoRepository
import io.kontour.server.messaging.messages.ChatMessageIncome
import io.kontour.server.messaging.messages.ChatMessageOutcome
import io.kontour.server.storage.chat.ChatMessage
import io.kontour.server.storage.chat.MessageRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class MessageDispatcher(
    private val connectionStore: ConnectionStore,
    private val onlineInfoRepository: OnlineInfoRepository,
    private val messageRepository: MessageRepository
) {
    suspend fun handleChatMessage(messageIncome: ChatMessageIncome) = coroutineScope {
        val userId = onlineInfoRepository.userIdByToken(messageIncome.token)
        onlineInfoRepository.userIds(messageIncome.chatId).forEach {
            connectionStore.connectionForUser(it)
                ?.send(ChatMessageOutcome(userId, messageIncome.chatId, messageIncome.text))
            launch {
                messageRepository.save(ChatMessage(userId, messageIncome.chatId, messageIncome.text)) //Insure that it is running async
            }
        }
    }
}
