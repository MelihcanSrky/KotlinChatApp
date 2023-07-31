package com.kotlin.chatapp.domain.model

import com.kotlin.chatapp.data.remote.dto.UserModelDto
import kotlinx.serialization.Serializable

@Serializable
data class FriendsModel(
    val status: Int,
    val message: String,
    val data: List<UserModelDto>?
)
