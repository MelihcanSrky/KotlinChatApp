package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SendRequestModel(
    val user_uuid: String,
    val sender_uuid: String
)
