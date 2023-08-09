package com.kotlin.chatapp.data.remote

import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun initSession(
        chatUuid: String,
        userUuid: String
    ) : Resource<Unit>

    suspend fun sendMessage(message: String)

    fun observeMessages(): Flow<Message>

    suspend fun closeSession()

    companion object {
        const val BASE_URL = "ws://192.168.1.86:5000"
    }

    sealed class Endpoints(val url: String) {
        object ChatSocket: Endpoints("$BASE_URL/ws/{chat_uuid}")
    }
}