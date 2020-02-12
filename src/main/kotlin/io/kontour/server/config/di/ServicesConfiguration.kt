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

package io.kontour.server.config.di

import io.kontour.server.api.chat.ChatService
import io.kontour.server.api.user.AuthService
import io.kontour.server.api.user.TokenIssuer
import io.kontour.server.api.user.UserService
import io.kontour.server.common.hashPassword
import io.kontour.server.common.validatePasswordHash
import io.kontour.server.storage.user.repo.MongoUserRepository
import org.koin.core.module.Module


fun Module.configureServices() {
    single { UserService(get()) { hashPassword(it) } }
    single { AuthService(get<MongoUserRepository>()) { pass, hash -> validatePasswordHash(pass, hash) } }
    single { TokenIssuer(get()) }
    single { ChatService(get(), get()) }
}
