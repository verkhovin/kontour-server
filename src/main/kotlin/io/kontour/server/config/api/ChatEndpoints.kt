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

package io.kontour.server.config.api

import io.kontour.server.api.chat.ChatService
import io.ktor.application.call
import io.ktor.auth.authenticate
import io.ktor.request.receive
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.ktor.ext.get

fun Routing.chatEndpoints() {
    val chatService: ChatService = get()

    authenticate("access") {
        post("/api/chat") {
            chatService.createChat(call.receive())
        }

        get("/api/chat/{id}") {
            chatService.getChat(call.parameters["id"]!!)
        }

        post("/api/chat/{chatId}/user/{userId}") {
            chatService.addUserToChat(call.parameters["chatId"]!!, call.parameters["userId"]!!)
        }

        post("/api/chat/{chatId}/childChat/{childChatId}") {
            chatService.addChildChat(call.parameters["chatId"]!!, call.parameters["childChatId"]!!)
        }
    }
}
