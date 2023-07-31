package com.kotlin.chatapp.presentation.navigation

sealed class Screens(val route: String) {
    object Register : Screens("register")
    object Login : Screens("login")
    object MainPage : Screens("mainpage")
    object ChatsPage : Screens("chats")
    object ChatPage : Screens("chat")
    object FriendsPage : Screens("friends")
}
