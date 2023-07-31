package com.kotlin.chatapp.data.remote.dto

import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.domain.model.MessagesDataModel
import com.kotlin.chatapp.domain.model.MessagesModel
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Serializable
data class MessagesDto(
    val status: Int,
    val message: String,
    val data: MessagesDataDto
) {
    fun toMessagesModel() : MessagesModel {
        val formattedData = data.toMessagesData()
        return MessagesModel(
            status,
            message,
            formattedData
        )
    }
}

@Serializable
data class MessagesDataDto(
    val receivedMessages: List<MessageDto>,
    val lastMessages: List<MessageDto>
) {
    fun toMessagesData(): MessagesDataModel {
        val formattedReceivedMessages = receivedMessages.map { it.toMessage() }
        val formattedLastMessages = lastMessages.map { it.toMessage() }
        return MessagesDataModel(
            formattedReceivedMessages,
            formattedLastMessages
        )
    }
}

@Serializable
data class MessageDto(
    val user_uuid: String,
    val chat_uuid: String,
    val message: String,
    val send_at: Long,
    val status: String
) {
    fun toMessage(): Message {
        val sendDate = Date(send_at)
        val sdf = SimpleDateFormat("HH:mm")
        val formatDate = sdf.format(sendDate)
        return Message(
            user_uuid,
            chat_uuid,
            message,
            formatDate,
            status
        )
    }
}