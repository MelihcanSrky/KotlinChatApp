package com.kotlin.chatapp.data.remote

import com.kotlin.chatapp.data.remote.dto.MessageDto
import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.utils.Resource
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.*
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient
): ChatSocketService {

    private var socket: WebSocketSession? = null

    override suspend fun initSession(chatUuid: String, userUuid: String): Resource<Unit> {
        return try {
            val url = ChatSocketService.Endpoints.ChatSocket.url.replace("{chat_uuid}", chatUuid)
            socket = client.webSocketSession {
                url("$url?user_uuid=$userUuid")
            }
            if (socket?.isActive == true) {
                Resource.Success(Unit)
            } else Resource.Error("Couldn't establish a connection.")
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(message: String) {
        try {
            socket?.send(Frame.Text(message))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDto = Json.decodeFromString<MessageDto>(json)
                    messageDto.toMessage()
                } ?: flow {  }
        } catch (e: Exception) {
            e.printStackTrace()
            flow {  }
        }
    }

    override suspend fun closeSession() {
        socket?.close()
    }

}