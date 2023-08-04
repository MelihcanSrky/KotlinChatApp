package com.kotlin.chatapp.presentation.features.main.friends

import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.data.remote.dto.UserModelDto
import com.kotlin.chatapp.domain.model.ChatInfo
import com.kotlin.chatapp.domain.model.CreateChatModel
import com.kotlin.chatapp.domain.model.SendRequestModel
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import com.kotlin.chatapp.utils.IsSuccess
import com.kotlin.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class FriendsPageAction : ViewAction {
    object GetFriends : FriendsPageAction()
    object GetRequests : FriendsPageAction()
    data class SendRequest(val user_to_send_uuid: String) : FriendsPageAction()
    data class AcceptOrDecline(val user_to_send_uuid: String, val accept: Boolean) :
        FriendsPageAction()

    object GetUsers : FriendsPageAction()
    data class CreateChat(val friend_uuid: String) : FriendsPageAction()
}

enum class Tasks { GET_FRIENDS, GET_REQUESTS, GET_USERS, SEND_REQUEST, CREATE_CHAT, ACCEPT_OR_DECLINE, NONE }

@HiltViewModel
class FriendsPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService
) : BaseViewModel<FriendsPageViewModel.State, FriendsPageAction, FriendsPageViewModel.Effect>(
    initialState = State()
) {
    data class State(
        val user_uuid: String = "",
        val token: String = "",
        val friends: List<UserModelDto> = emptyList(),
        val requests: List<UserModelDto> = emptyList(),
        val fetchedUsers: List<UserModelDto> = emptyList(),
        val isSuccess: IsSuccess = IsSuccess.NONE,
        val task: Tasks = Tasks.NONE,
        val getFriendsErrorMessage: String? = null,
        val getRequestsErrorMessage: String? = null,
        val getUsersErrorMessage: String? = null,
        val sendRequestErrorMessage: String? = null,
        val acceptOrDeclineErrorMessage: String? = null,
        val createChatErrorMessage: String? = null,
        val searchQuery: String = "",
        val chatInfo: ChatInfo? = null,
        val chatCreated: Boolean = false
    ) : ViewState

    private fun fetchFriends() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING, task = Tasks.GET_FRIENDS))
        viewModelScope.launch {
            val response = chatAppService.getFriends(
                user_uuid = state.value.user_uuid,
                token = state.value.token
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            friends = response.data!!.data ?: emptyList()
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            getFriendsErrorMessage = response.message ?: "Server side error!",
                            task = Tasks.NONE
                        )
                    )
                }
            }
        }
    }

    private fun fetchRequests() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING, task = Tasks.GET_REQUESTS))
        viewModelScope.launch {
            val response = chatAppService.getRequests(
                user_uuid = state.value.user_uuid,
                token = state.value.token
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            requests = response.data!!.data ?: emptyList()
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            getRequestsErrorMessage = response.message
                                ?: "Server side error!",
                            task = Tasks.NONE
                        )
                    )
                }
            }
        }
    }

    private fun fetchUsers() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING, task = Tasks.GET_USERS))
        viewModelScope.launch {
            val response = chatAppService.getUsers(
                user_uuid = state.value.user_uuid,
                token = state.value.token,
                searchQuery = state.value.searchQuery
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            fetchedUsers = response.data!!.data ?: emptyList()
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            getUsersErrorMessage = response.message ?: "Server side error!",
                            task = Tasks.NONE
                        )
                    )
                }
            }
        }
    }

    private fun sendRequest(user_to_send_uuid: String) {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING, task = Tasks.SEND_REQUEST))
        viewModelScope.launch {
            val response = chatAppService.sendRequest(
                user_uuid = user_to_send_uuid,
                sender_uuid = state.value.user_uuid,
                token = state.value.token
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            sendRequestErrorMessage = response.message
                                ?: "Server side error!",
                            task = Tasks.NONE
                        )
                    )
                }
            }
        }
    }

    private fun acceptOrDecline(user_to_send_uuid: String, accept: Boolean) {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING, task = Tasks.ACCEPT_OR_DECLINE))
        viewModelScope.launch {
            val reqBody = SendRequestModel(
                user_uuid = state.value.user_uuid,
                sender_uuid = user_to_send_uuid
            )
            val response =
                chatAppService.acceptOrDecline(reqBody, state.value.token, accept)
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            acceptOrDeclineErrorMessage = response.message
                                ?: "Server side error!",
                            task = Tasks.NONE
                        )
                    )
                }
            }
        }
    }

    private fun createChat(friend_uuid: String) {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING, task = Tasks.CREATE_CHAT))
        viewModelScope.launch {
            val reqBody = CreateChatModel(
                user_uuid = state.value.user_uuid,
                friend_uuid = friend_uuid
            )
            val response = chatAppService.createChat(reqBody, state.value.token)
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            chatCreated = true,
                            chatInfo = response.data?.data ?: null
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            createChatErrorMessage = response.message ?: "Server side error!",
                            task = Tasks.NONE
                        )
                    )
                }
            }
        }
    }

    override fun dispatch(action: FriendsPageAction) {
        when (action) {
            is FriendsPageAction.GetFriends -> fetchFriends()
            is FriendsPageAction.GetRequests -> fetchRequests()
            is FriendsPageAction.SendRequest -> sendRequest(action.user_to_send_uuid)
            is FriendsPageAction.AcceptOrDecline -> acceptOrDecline(
                action.user_to_send_uuid,
                action.accept
            )

            is FriendsPageAction.GetUsers -> fetchUsers()
            is FriendsPageAction.CreateChat -> createChat(action.friend_uuid)
        }
    }

    sealed class Effect : ViewEffect
}