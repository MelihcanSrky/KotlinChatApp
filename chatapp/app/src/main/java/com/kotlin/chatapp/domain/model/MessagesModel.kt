package com.kotlin.chatapp.domain.model

import com.kotlin.chatapp.data.remote.dto.MessageDto
import kotlinx.serialization.Serializable

@Serializable
data class MessagesModel(
    val status: Int,
    val message: String,
    val data: MessagesDataModel?
)

@Serializable
data class MessagesDataModel(
    val receivedMessages: List<Message>,
    val lastMessages: List<Message>
)