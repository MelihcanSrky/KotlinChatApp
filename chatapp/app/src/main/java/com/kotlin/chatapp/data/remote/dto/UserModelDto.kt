package com.kotlin.chatapp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserModelDto(
    val uuid: String,
    val username: String,
    val firstname: String,
    val lastname: String
)
