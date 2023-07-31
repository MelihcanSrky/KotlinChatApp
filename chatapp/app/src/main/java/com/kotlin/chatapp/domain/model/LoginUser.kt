package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserModel(
    val username: String,
    val password: String
)
