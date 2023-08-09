package com.kotlin.chatapp.domain.model

import com.kotlin.chatapp.utils.IsSuccess

data class TaskStateModel(
    val isSuccess: IsSuccess = IsSuccess.NONE,
    val errorMessage: String? = null
)
