package com.kotlin.chatapp.data.remote.dto

import com.kotlin.chatapp.domain.model.ChatsDataModel
import com.kotlin.chatapp.domain.model.ChatsModel
import kotlinx.serialization.Serializable
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date

@Serializable
data class ChatsModelDto(
    val status: Int,
    val message: String,
    val data: List<ChatsDataModelDto>
) {
    fun toChats() : ChatsModel {
        val data = data.map { it.toChats() }
        return ChatsModel(
            status,
            message,
            data
        )
    }
}

@Serializable
data class ChatsDataModelDto(
    val chat_uuid: String,
    val user_uuid: String,
    val chatname: String,
    val created_at: Long,
    val last_message: String?,
    val last_message_at: Long?
) {
    fun toChats() : ChatsDataModel {
        val date = Date(created_at)
        val formatDate = DateFormat
            .getDateInstance(DateFormat.DEFAULT)
            .format(date)
        var sendDate: Date?
        var formattedSendDate: String? = null
        if (last_message != null && last_message_at != null) {
            sendDate = Date(last_message_at)
            val sdf = SimpleDateFormat("HH:mm")
            formattedSendDate = sdf.format(sendDate)
        }
        return ChatsDataModel(
            chat_uuid,
            user_uuid,
            chatname,
            formatDate,
            last_message,
            formattedSendDate
        )
    }
}