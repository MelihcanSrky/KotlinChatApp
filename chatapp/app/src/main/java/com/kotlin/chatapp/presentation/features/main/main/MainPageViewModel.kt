package com.kotlin.chatapp.presentation.features.main.main

import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class MainPageAction : ViewAction {

}

@HiltViewModel
class MainPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService
) : BaseViewModel<MainPageViewModel.State, MainPageAction, MainPageViewModel.Effect>(
    initialState = State()
) {
    data class State(
        val token: String = ""
    ) : ViewState

    override fun dispatch(action: MainPageAction) {

    }

    sealed class Effect : ViewEffect
}