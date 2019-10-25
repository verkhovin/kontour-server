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

import io.kontour.server.messaging.connection.ChatConnectedMembersRepository
import io.kontour.server.messaging.connection.ConnectionStore
import io.kontour.server.messaging.messages.ChatMessage
import org.slf4j.LoggerFactory

class MessageDispatcher(
    private val connectionStore: ConnectionStore,
    private val chatConnectedMembersRepository: ChatConnectedMembersRepository
) {
    suspend fun handleChatMessage(message: ChatMessage) {
        chatConnectedMembersRepository.userIds(message.chatId).forEach {
            connectionStore.connectionForUser(it)?.send(message)
        }
    }
}
