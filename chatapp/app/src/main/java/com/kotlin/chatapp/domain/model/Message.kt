package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val userUuid: String,
    val chatUuid: String,
    val message: String,
    val sendAt: String,
    val status: String
)