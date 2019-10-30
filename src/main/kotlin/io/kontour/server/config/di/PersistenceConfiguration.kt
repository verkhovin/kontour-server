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

import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import io.kontour.server.storage.user.repo.MongoUserRepository
import org.koin.core.module.Module
import org.koin.core.scope.Scope

fun Module.configurePersistenceComponents() {
    fun Scope.mongoCollection(collectionName: String) = get<MongoDatabase>().getCollection(collectionName)
    single { MongoClients.create().getDatabase("kontour") }
    single { MongoUserRepository(mongoCollection("users"), mongoCollection("credentials")) }
}