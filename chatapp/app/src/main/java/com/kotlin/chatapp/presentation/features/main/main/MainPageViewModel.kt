package com.kotlin.chatapp.presentation.features.main.main

import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.domain.model.TaskStateModel
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import com.kotlin.chatapp.utils.IsSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class MainPageAction : ViewAction {
    object Logout : MainPageAction()
}

@HiltViewModel
class MainPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService
) : BaseViewModel<MainPageViewModel.State, MainPageAction, MainPageViewModel.Effect>(
    initialState = State()
) {
    data class State(
        val token: String = "",
        val logoutState: TaskStateModel = TaskStateModel()
    ) : ViewState

    private fun logout() {
        commit(state.value.copy(logoutState = TaskStateModel(isSuccess = IsSuccess.SUCCESS)))
    }

    override fun dispatch(action: MainPageAction) {
        when (action) {
            is MainPageAction.Logout -> logout()
        }
    }

    sealed class Effect : ViewEffect
}