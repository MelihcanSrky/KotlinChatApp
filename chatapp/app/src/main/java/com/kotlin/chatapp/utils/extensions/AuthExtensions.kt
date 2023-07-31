package com.kotlin.chatapp.utils.extensions

fun String?.isUsernameValid(): Boolean {
    return !isNullOrEmpty() && this.length >= 3
}

fun String?.isPasswordValid(): Boolean {
    return !isNullOrEmpty() && this.length >= 6
}