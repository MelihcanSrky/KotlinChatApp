package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatsModel(
    val status: Int,
    val message: String,
    val data: List<ChatsDataModel>?
)

@Serializable
data class ChatsDataModel(
    val chatUuid: String,
    val userUuid: String,
    val chatname: String,
    val createdAt: String,
    val lastMessage: String?,
    val lastMessageAt: String?
)