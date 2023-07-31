package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageModel(
    val user_uuid: String,
    val chat_uuid: String,
    val message: String
)
