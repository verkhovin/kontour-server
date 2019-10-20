package io.kontour.server.common

import org.bson.types.ObjectId

fun objectId(id: String?) = if(id == null) ObjectId() else ObjectId(id)