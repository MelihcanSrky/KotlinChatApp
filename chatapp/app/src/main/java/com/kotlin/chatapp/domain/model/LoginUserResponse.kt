package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LoginUserResponse(
    val status: Int,
    val message: String,
    val data : TokenData?
)

@Serializable
data class TokenData(
    val token: String
)