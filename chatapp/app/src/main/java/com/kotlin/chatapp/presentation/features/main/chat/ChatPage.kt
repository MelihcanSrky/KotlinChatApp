package com.kotlin.chatapp.presentation.features.main.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.kotlin.chatapp.domain.model.Message
import com.kotlin.chatapp.presentation.features.main.chats.ChatsPageAction
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.storage.SharedPrefs
import com.kotlin.chatapp.utils.IsSuccess

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatPage(
    navController: NavController,
    chat_uuid: String,
    chatname: String,
    viewModel: ChatPageViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val observedMessagesLength: Int = state.observedMessages.size
    val ctx = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver {_, event ->
            if (event == Lifecycle.Event.ON_START) {
                val token = SharedPrefs.getInstance(ctx).token
                val user_uuid = SharedPrefs.getInstance(ctx).user_uuid
                viewModel.commit(
                    state.copy(
                        user_uuid = user_uuid,
                        token = token,
                        chat_uuid = chat_uuid
                    )
                )
            } else if (event == Lifecycle.Event.ON_STOP) {
                viewModel.dispatch(ChatPageAction.Disconnect)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(state) {
        if (state.isSuccess == IsSuccess.NONE && state.token != "") {
            viewModel.dispatch(ChatPageAction.ConnectToChat)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.97f),
                    titleContentColor = MaterialTheme.colorScheme.onTertiary
                ),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = chatname, style = ChatAppTypo.headlineSmall)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            if (state.connectionError == "")
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    reverseLayout = true
                ) {
                    items(state.observedMessages) { lastMessage ->
                        MessageBox(message = lastMessage, user_uuid = state.user_uuid)
                    }
                    if (state.lastMessages.isNotEmpty()) {
                        items(state.lastMessages) { receivedMessages ->
                            MessageBox(message = receivedMessages, user_uuid = state.user_uuid)
                        }
                        if (observedMessagesLength == state.observedMessages.size) {
                            item {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Unreaded Messages",
                                        style = ChatAppTypo.bodySmall,
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
                                }
                            }
                        }
                    }
                    items(state.receivedMessages) { receivedMessages ->
                        MessageBox(message = receivedMessages, user_uuid = state.user_uuid)
                    }
                }
            else {
                Text(
                    text = state.connectionError,
                    style = ChatAppTypo.headlineLarge,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            OutlinedTextField(
                value = viewModel.state.value.message,
                onValueChange = {
                    viewModel.commit(state.copy(message = it))
                },
                label = {
                    Text(text = "Enter a message")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                maxLines = 2,
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.dispatch(ChatPageAction.SendMessage)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Send"
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBox(message: Message, user_uuid: String) {
    val isOwnMessage = message.userUuid == user_uuid
    Box(
        contentAlignment = if (isOwnMessage) {
            Alignment.CenterEnd
        } else Alignment.CenterStart,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            if (isOwnMessage)
                Text(
                    text = message.sendAt,
                    color = MaterialTheme.colorScheme.secondary,
                    style = ChatAppTypo.bodySmall
                )
            Column(
                modifier = Modifier
                    .widthIn(0.dp, 200.dp)
                    .background(
                        color = if (isOwnMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary,
                        shape = if (isOwnMessage) RoundedCornerShape(
                            20.dp,
                            8.dp,
                            20.dp,
                            20.dp
                        ) else RoundedCornerShape(8.dp, 20.dp, 20.dp, 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = message.message,
                    style = ChatAppTypo.bodyMedium.copy(fontSize = 16.sp),
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            if (!isOwnMessage)
                Text(
                    text = message.sendAt,
                    color = MaterialTheme.colorScheme.secondary,
                    style = ChatAppTypo.bodySmall
                )
        }
    }
    Spacer(modifier = Modifier.height(2.dp))
}