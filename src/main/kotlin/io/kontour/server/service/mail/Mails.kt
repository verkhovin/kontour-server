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

package io.kontour.server.service.mail

import io.kontour.server.storage.user.model.User
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.p
import kotlinx.html.stream.createHTML

fun setPasswordRequestMail(user: User, setPasswordUrl: String, workspaceName: String) =
    Mail(
        "Welcome to $workspaceName",
        createHTML().body {
            p {
                +"Hello ${user.login}."
            }
            p {
                +"Kontour is messaging service. You were added to $workspaceName workspace. Now you need "
                +"to set password for your account. You can do it "
                a(setPasswordUrl) { +"here" }
                +"."
            }
            p {
                +"Thank you!"
                br
                +"Kontour team."
            }
        }
    )