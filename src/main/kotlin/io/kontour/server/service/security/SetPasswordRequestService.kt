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

package io.kontour.server.service.security

import io.kontour.server.service.mail.MailNotificationService
import io.kontour.server.service.mail.setPasswordRequestMail
import io.kontour.server.storage.user.model.User

class SetPasswordRequestService(
    private val mailNotificationService: MailNotificationService,
    private val tokenIssuer: TokenIssuer,
    private val viewHost: String,
    private val workspaceName: String
) {
    fun requestPassword(user: User) {
        user.id ?: throw Exception("Can't generate password set token for user without id. User: $user")
        val url = "$viewHost/pass?token=${tokenIssuer.generateSetPasswordToken(user.id)}"
        mailNotificationService.send(user.email,
            setPasswordRequestMail(user, url, workspaceName)
        )
    }
}