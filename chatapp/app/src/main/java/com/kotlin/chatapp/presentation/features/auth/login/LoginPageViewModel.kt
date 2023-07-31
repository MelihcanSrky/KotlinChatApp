package com.kotlin.chatapp.presentation.features.auth.login

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.domain.model.LoginUserModel
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import com.kotlin.chatapp.storage.SharedPrefs
import com.kotlin.chatapp.utils.IsSuccess
import com.kotlin.chatapp.utils.Resource
import com.kotlin.chatapp.utils.extensions.isPasswordValid
import com.kotlin.chatapp.utils.extensions.isUsernameValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginAction : ViewAction {
    object LoginUser : LoginAction()
}

@HiltViewModel
class LoginPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService,
) : BaseViewModel<LoginPageViewModel.State, LoginAction, LoginPageViewModel.Effect>(
    initialState = State()
) {
    data class State(
        val username: String = "",
        val password: String = "",
        val user_uuid: String = "",
        val isSuccess: IsSuccess = IsSuccess.NONE,
        val isButtonEnabled: Boolean = false,
        val errorMessage: String = "",
        val token: String = ""
    ) : ViewState

    private fun loginUser() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
        viewModelScope.launch {
            val user = LoginUserModel(
                username = state.value.username,
                password = state.value.password
            )
            val response = chatAppService.loginUser(user)
            when (response) {
                is Resource.Success -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.SUCCESS,
                            token = response.data!!.data!!.token,
                            user_uuid = response.data.message
                        )
                    )
                }
                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            errorMessage = response.message ?: "Unknown error!"
                        )
                    )
                }
            }
        }
    }

    fun buttonState() {
        if (
            state.value.username.isUsernameValid() &&
            state.value.password.isPasswordValid()
        ) {
            commit(state.value.copy(isButtonEnabled = true))
        } else {
            commit(state.value.copy(isButtonEnabled = false))
        }
    }

    override fun dispatch(action: LoginAction) {
        when (action) {
            is LoginAction.LoginUser -> loginUser()
        }
    }

    sealed class Effect : ViewEffect
}