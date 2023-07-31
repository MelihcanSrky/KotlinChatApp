package com.kotlin.chatapp.presentation.features.main.chat

import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.data.remote.ChatSocketService
import com.kotlin.chatapp.data.remote.dto.MessageDto
import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.domain.model.SendMessageModel
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import com.kotlin.chatapp.presentation.features.main.chats.ChatsPageAction
import com.kotlin.chatapp.utils.IsSuccess
import com.kotlin.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

sealed class ChatPageAction : ViewAction {
    object ConnectToChat : ChatPageAction()
    object SendMessage : ChatPageAction()
    object Disconnect : ChatPageAction()
}

@HiltViewModel
class ChatPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService,
    private val chatSocketService: ChatSocketService
) : BaseViewModel<ChatPageViewModel.State, ChatPageAction, ChatPageViewModel.Effect>(
    initialState = State()
) {

    data class State(
        val user_uuid: String = "",
        val chat_uuid: String = "",
        val token: String = "",
        val message: String = "",
        val receivedMessages: List<Message> = emptyList(),
        val lastMessages: List<Message> = emptyList(),
        val observedMessages: List<Message> = emptyList(),
        val isSuccess: IsSuccess = IsSuccess.NONE,
        val errorMessage: String = "",
        val connectionError: String = "",
    ) : ViewState

    private fun connectToChat() {
        getAllMessages()
        viewModelScope.launch {
            val response =
                chatSocketService.initSession(state.value.chat_uuid, state.value.user_uuid)
            when (response) {
                is Resource.Success -> {
                    chatSocketService.observeMessages()
                        .onEach { message ->
                            val newList = state.value.observedMessages.toMutableList().apply {
                                add(0, message)
                            }
                            commit(
                                state.value.copy(
                                    observedMessages = newList
                                )
                            )
                        }.launchIn(viewModelScope)
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            connectionError = response.message ?: "Unknown error!"
                        )
                    )
                }
            }
        }
    }

    private fun getAllMessages() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
        viewModelScope.launch {
            val response = chatAppService.getAllMessages(
                state.value.chat_uuid,
                state.value.user_uuid,
                state.value.token
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            receivedMessages = response.data!!.data?.receivedMessages ?: emptyList(),
                            lastMessages = response.data.data?.lastMessages ?: emptyList()
                        )
                    )
                }
                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            errorMessage = response.data!!.message
                        )
                    )
                }
            }
        }
    }

    private fun disconnect() {
        viewModelScope.launch {
            chatSocketService.closeSession()
        }
    }

    private fun sendMessage() {
        viewModelScope.launch {
            if (state.value.message.isNotBlank()) {
                val objMessage = SendMessageModel(
                    state.value.user_uuid,
                    state.value.chat_uuid,
                    state.value.message
                )
                val stringMessage = Json.encodeToString(objMessage)
                chatSocketService.sendMessage(stringMessage)
                val sendDate = Date().time
                val sdf = SimpleDateFormat("HH:mm")
                val formattedSendDate = sdf.format(sendDate)
                val status = "received"
                val message = Message(
                    state.value.user_uuid,
                    state.value.user_uuid,
                    state.value.message,
                    formattedSendDate,
                    status
                )
                val newList = state.value.observedMessages.toMutableList().apply {
                    add(0, message)
                }
                commit(
                    state.value.copy(
                        observedMessages = newList,
                        message = ""
                    )
                )
            }
        }
    }

    override fun dispatch(action: ChatPageAction) {
        when (action) {
            is ChatPageAction.ConnectToChat -> connectToChat()
            is ChatPageAction.SendMessage -> sendMessage()
            is ChatPageAction.Disconnect -> disconnect()
        }
    }

    sealed class Effect : ViewEffect
}