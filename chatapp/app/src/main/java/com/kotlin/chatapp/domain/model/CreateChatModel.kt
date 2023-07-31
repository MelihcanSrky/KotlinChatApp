package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatModel(
    val user_uuid: String,
    val friend_uuid: String
)
