package com.kotlin.chatapp.presentation.features.main.chats

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.domain.model.ChatsDataModel
import com.kotlin.chatapp.domain.model.ChatsModel
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import com.kotlin.chatapp.utils.IsSuccess
import com.kotlin.chatapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatsPageAction : ViewAction {
    data class GetChats(val user_uuid: String, val token: String) : ChatsPageAction()
}

@HiltViewModel
class ChatsPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService,
) : BaseViewModel<ChatsPageViewModel.State, ChatsPageAction, ChatsPageViewModel.Effect>(
    initialState = State()
){
    data class State(
        val user_uuid: String = "",
        val token: String = "",
        val chats: List<ChatsDataModel> = emptyList(),
        val isSuccess: IsSuccess = IsSuccess.NONE,
        val errorMessage: String = "",

    ) : ViewState

    private fun fetchChats(user_uuid: String, token: String) {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
        viewModelScope.launch {
            val response = chatAppService.getChats(user_uuid, token)
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            chats = response.data!!.data ?: emptyList()
                        )
                    )
                }
                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            errorMessage = response.data?.message ?: "Server side error!"
                        )
                    )
                }
            }
        }
    }

    override fun dispatch(action: ChatsPageAction) {
        when(action) {
            is ChatsPageAction.GetChats -> fetchChats(action.user_uuid, action.token)
        }
    }

    sealed class Effect : ViewEffect
}
