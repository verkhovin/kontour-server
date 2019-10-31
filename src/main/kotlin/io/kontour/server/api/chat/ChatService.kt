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

package io.kontour.server.api.chat

import io.kontour.server.api.chat.dto.ChatDTO
import io.kontour.server.api.chat.dto.CreateChatRequest
import io.kontour.server.api.chat.dto.CreateChatResponse
import io.kontour.server.api.chat.dto.GetChatResponse
import io.kontour.server.api.chat.dto.MessageDTO
import io.kontour.server.storage.chat.Chat
import io.kontour.server.storage.chat.ChatMessage
import io.kontour.server.storage.chat.ChatRepository
import io.kontour.server.storage.chat.MessageRepository

class ChatService(
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository
) {
    fun createChat(createChatRequest: CreateChatRequest): CreateChatResponse {
        val chat = createChatRequest.chatDTO.toEntity()
        val savedChatId = chatRepository.save(chat)
        return CreateChatResponse(chatRepository.find(savedChatId).toDTO())
    }

    fun getChat(chatId: String): GetChatResponse = GetChatResponse(
        chatRepository.find(chatId).toDTO(),
        messageRepository.getLastMessages(chatId, 20).map { it.toDTO() }
    )


    fun addUserToChat(chatId: String, userId: String) {
        chatRepository.addUser(chatId, userId)
    }

    fun addChildChat(parentChatId: String, childChatId: String) {
        chatRepository.addChat(parentChatId, childChatId)
    }


    private fun ChatDTO.toEntity() = Chat(id, chatType, name, userIds, chatIds)
    private fun Chat.toDTO() = ChatDTO(id, chatType, name, userIds, chatIds)
    private fun ChatMessage.toDTO() = MessageDTO(authorId, text, date, edited)
}
