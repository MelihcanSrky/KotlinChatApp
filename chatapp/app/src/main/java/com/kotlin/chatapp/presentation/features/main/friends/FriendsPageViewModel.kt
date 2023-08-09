package com.kotlin.chatapp.presentation.features.main.friends

import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.data.remote.dto.UserModelDto
import com.kotlin.chatapp.domain.model.ChatInfo
import com.kotlin.chatapp.domain.model.CreateChatModel
import com.kotlin.chatapp.domain.model.SendRequestModel
import com.kotlin.chatapp.domain.model.TaskStateModel
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
    data class DeleteFriend(val friend_uuid: String) : FriendsPageAction()
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

        val getFriendsState: TaskStateModel = TaskStateModel(),
        val getRequestsState: TaskStateModel = TaskStateModel(),
        val getUsersState: TaskStateModel = TaskStateModel(),
        val sendRequestState: TaskStateModel = TaskStateModel(),
        val acceptOrDeclineState: TaskStateModel = TaskStateModel(),
        val createChatState: TaskStateModel = TaskStateModel(),
        val deleteFriendState: TaskStateModel = TaskStateModel(),

        val searchQuery: String = "",
        val chatInfo: ChatInfo? = null,
        val chatCreated: Boolean = false
    ) : ViewState


    private fun fetchFriends() {
        commit(
            state.value.copy(
                getFriendsState = TaskStateModel(
                    isSuccess = IsSuccess.LOADING,
                ),
            )
        )
        viewModelScope.launch {
            val response = chatAppService.getFriends(
                user_uuid = state.value.user_uuid,
                token = state.value.token
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            getFriendsState = TaskStateModel(
                                isSuccess = IsSuccess.SUCCESS,
                            ),
                            friends = response.data!!.data ?: emptyList(),
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            getFriendsState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
                        )
                    )
                }
            }
        }
    }

    private fun fetchRequests() {
        commit(state.value.copy(getRequestsState = TaskStateModel(isSuccess = IsSuccess.LOADING)))
        viewModelScope.launch {
            val response = chatAppService.getRequests(
                user_uuid = state.value.user_uuid,
                token = state.value.token
            )
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            getRequestsState = TaskStateModel(
                                isSuccess = IsSuccess.SUCCESS,
                            ),
                            requests = response.data!!.data ?: emptyList(),
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            getRequestsState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
                        )
                    )
                }
            }
        }
    }

    private fun fetchUsers() {
        commit(state.value.copy(getUsersState = TaskStateModel(isSuccess = IsSuccess.LOADING)))
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
                            getUsersState = TaskStateModel(
                                isSuccess = IsSuccess.SUCCESS,
                            ),
                            fetchedUsers = response.data!!.data ?: emptyList(),
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            getUsersState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
                        )
                    )
                }
            }
        }
    }

    private fun sendRequest(user_to_send_uuid: String) {
        commit(state.value.copy(sendRequestState = TaskStateModel(isSuccess = IsSuccess.LOADING)))
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
                            sendRequestState = TaskStateModel(
                                isSuccess = IsSuccess.SUCCESS,
                            ),
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            sendRequestState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
                        )
                    )
                }
            }
        }
    }

    private fun acceptOrDecline(user_to_send_uuid: String, accept: Boolean) {
        commit(state.value.copy(acceptOrDeclineState = TaskStateModel(isSuccess = IsSuccess.LOADING)))
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
                            acceptOrDeclineState = TaskStateModel(
                                isSuccess = IsSuccess.SUCCESS,
                            ),
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            acceptOrDeclineState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
                        )
                    )
                }
            }
        }
    }

    private fun createChat(friend_uuid: String) {
        commit(state.value.copy(createChatState = TaskStateModel(isSuccess = IsSuccess.LOADING)))
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
                            createChatState = TaskStateModel(
                                isSuccess = IsSuccess.SUCCESS,
                            ),
                            chatCreated = true,
                            chatInfo = response.data?.data ?: null,
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            createChatState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
                        )
                    )
                }
            }
        }
    }

    private fun deleteFriend(friend_uuid: String) {
        commit(state.value.copy(deleteFriendState = TaskStateModel(isSuccess = IsSuccess.LOADING)))
        viewModelScope.launch {
            val reqBody = CreateChatModel(
                user_uuid = state.value.user_uuid,
                friend_uuid = friend_uuid
            )
            val response = chatAppService.deleteFriend(reqBody, state.value.token)
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            deleteFriendState = TaskStateModel(isSuccess = IsSuccess.SUCCESS),
                            friends = state.value.friends.filter {
                                it.uuid != friend_uuid
                            }
                        )
                    )
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            deleteFriendState = TaskStateModel(
                                isSuccess = IsSuccess.ERROR,
                                errorMessage = response.message
                            )
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
            is FriendsPageAction.DeleteFriend -> deleteFriend(action.friend_uuid)
        }
    }

    sealed class Effect : ViewEffect
}