package com.kotlin.chatapp.presentation.features.auth.register

import androidx.lifecycle.viewModelScope
import com.kotlin.chatapp.data.remote.ChatAppService
import com.kotlin.chatapp.domain.model.CreateUser
import com.kotlin.chatapp.presentation.core.BaseViewModel
import com.kotlin.chatapp.presentation.core.ViewAction
import com.kotlin.chatapp.presentation.core.ViewEffect
import com.kotlin.chatapp.presentation.core.ViewState
import com.kotlin.chatapp.utils.IsSuccess
import com.kotlin.chatapp.utils.Resource
import com.kotlin.chatapp.utils.extensions.isPasswordValid
import com.kotlin.chatapp.utils.extensions.isUsernameValid
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RegisterAction : ViewAction {
    object RegisterUser : RegisterAction()
}

@HiltViewModel
class RegisterPageViewModel @Inject constructor(
    private val chatAppService: ChatAppService
) : BaseViewModel<RegisterPageViewModel.State, RegisterAction, RegisterPageViewModel.Effect>(
    initialState = State()
) {
    data class State(
        val username: String = "",
        val firstname: String = "",
        val lastname: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isButtonEnabled: Boolean = false,
        val isSuccess: IsSuccess = IsSuccess.NONE,
        val errorMessage: String = ""
    ) : ViewState

    private fun registerUser() {
        commit(state.value.copy(isSuccess = IsSuccess.LOADING))
        viewModelScope.launch {
            val user = CreateUser(
                username = state.value.username,
                firstname = state.value.firstname,
                lastname = state.value.lastname,
                password = state.value.password,
            )
            val response = chatAppService.registerUser(user)
            when (response) {
                is Resource.Success -> {
                    println("Success!")
                    commit(state.value.copy(isSuccess = IsSuccess.SUCCESS))
                }

                is Resource.Error -> {
                    commit(
                        state.value.copy(
                            isSuccess = IsSuccess.ERROR,
                            errorMessage = response.message ?: "Somethings go wrong"
                        )
                    )
                }
            }
        }
    }

    fun buttonState() {
        if (
            state.value.username.isUsernameValid() &&
            state.value.firstname.length >= 3 &&
            state.value.lastname.length >= 3 &&
            state.value.password.isPasswordValid() &&
            state.value.password == state.value.confirmPassword
        ) {
            commit(state.value.copy(isButtonEnabled = true))
        } else {
            commit(state.value.copy(isButtonEnabled = false))
        }
    }

    override fun dispatch(action: RegisterAction) {
        when (action) {
            is RegisterAction.RegisterUser -> registerUser()
        }
    }

    sealed class Effect : ViewEffect
}
