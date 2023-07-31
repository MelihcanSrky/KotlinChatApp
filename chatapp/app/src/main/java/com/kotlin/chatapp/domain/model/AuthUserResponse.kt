package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthUserResponse(
    val status: Int,
    val message: String,
    val data: ResponseUser?
)

@Serializable
data class ResponseUser(
    val uuid: String,
    val username: String,
    val firstname: String,
    val lastname: String
)