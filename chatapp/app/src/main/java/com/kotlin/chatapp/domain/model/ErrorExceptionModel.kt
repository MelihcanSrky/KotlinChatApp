package com.kotlin.chatapp.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorExceptionModel(
    val status: Int,
    val message: String,
    val data: String? = null
)
