package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateUser(
    val username: String,
    val firstname: String,
    val lastname: String,
    val password: String
)
