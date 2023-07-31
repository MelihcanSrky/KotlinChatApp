package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatCreatedModel(
    val status: Int,
    val message: String,
    val data: ChatInfo
)

@Serializable
data class ChatInfo(
    val chat_uuid: String,
    val user_uuid: String,
    val chatname: String,
    val created_at: Long
)
