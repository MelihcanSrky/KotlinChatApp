package com.kotlin.chatapp.presentation.features.main.chats

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.kotlin.chatapp.domain.model.ChatsDataModel
import com.kotlin.chatapp.presentation.features.main.chats.components.ChatsListTile
import com.kotlin.chatapp.presentation.features.main.chats.components.ErrorText
import com.kotlin.chatapp.presentation.navigation.Screens
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.storage.SharedPrefs
import com.kotlin.chatapp.utils.IsSuccess

@Composable
fun ChatsPage(
    navController: NavController, viewModel: ChatsPageViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val ctx = LocalContext.current

    LaunchedEffect(state) {
        if (state.token == "" && state.user_uuid == "") {
            val token = SharedPrefs.getInstance(ctx).token
            val user_uuid = SharedPrefs.getInstance(ctx).user_uuid
            viewModel.commit(state.copy(user_uuid = user_uuid, token = token))
        }
        if (state.isSuccess == IsSuccess.NONE && state.token != "") {
            viewModel.dispatch(ChatsPageAction.GetChats(state.user_uuid, state.token))
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        if (state.chats.isNotEmpty()) {
            items(state.chats) { chat ->
                ChatsListTile(navController = navController, chat = chat)
            }
        } else if (state.errorMessage.isNotEmpty()) {
            item {
                ErrorText(text = state.errorMessage)
            }
        }
    }
}

