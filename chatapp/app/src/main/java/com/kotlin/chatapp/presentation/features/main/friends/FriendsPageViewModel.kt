package com.kotlin.chatapp.presentation.features.main.friends

import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.data.remote.dto.UserModelDto
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
}

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
        val errorMessage: String = "",
        val searchQuery: String = ""
    ) : ViewState

    private fun fetchFriends() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
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
                            errorMessage = response.data?.message ?: "Unknown error!"
                        )
                    )
                }
            }
        }
    }

    private fun fetchRequests() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
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
                            errorMessage = response.data?.message ?: "Unknown error!"
                        )
                    )
                }
            }
        }
    }

    private fun fetchUsers() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
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
                            errorMessage = response.data?.message ?: "Unknown error!"
                        )
                    )
                }
            }
        }
    }

    private fun sendRequest(user_to_send_uuid: String) {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
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
                            errorMessage = response.data?.message ?: "Unknown error!"
                        )
                    )
                }
            }
        }
    }

    private fun acceptOrDecline(user_to_send_uuid: String, accept: Boolean) {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
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
                            errorMessage = response.data?.message ?: "Unknown error!"
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
        }
    }

    sealed class Effect : ViewEffect
}