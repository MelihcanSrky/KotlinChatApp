package com.kotlin.chatapp.presentation.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kotlin.chatapp.presentation.features.auth.login.LoginPage
import com.kotlin.chatapp.presentation.features.auth.register.RegisterPage
import com.kotlin.chatapp.presentation.features.main.chat.ChatPage
import com.kotlin.chatapp.presentation.features.main.main.MainPage
import com.kotlin.chatapp.storage.SharedPrefs

@Composable
fun Navigator(
    context: Context,
) {
    val navController = rememberNavController()

    val startDest =
        if (SharedPrefs.getInstance(context).token != "null") Screens.MainPage.route else Screens.Login.route

    NavHost(
        navController = navController,
        startDestination = startDest
    ) {
        composable(Screens.Register.route) {
            RegisterPage(navController = navController)
        }
        composable(Screens.Login.route) {
            LoginPage(navController = navController)
        }
        composable(Screens.MainPage.route) {
            MainPage(navController = navController)
        }
        composable(Screens.ChatPage.route + "/{chat_uuid}/{chatname}", arguments = listOf(
            navArgument(name = "chat_uuid") {
                type = NavType.StringType
                nullable = false
            },
            navArgument(name = "chatname") {
                type = NavType.StringType
                nullable = false
            }
        )) {
            val chat_uuid = it.arguments!!.getString("chat_uuid")
            val chatname = it.arguments!!.getString("chatname")
            ChatPage(navController = navController, chat_uuid = chat_uuid!!, chatname = chatname!!)
        }
    }
}