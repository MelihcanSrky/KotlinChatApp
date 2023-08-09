package com.kotlin.chatapp.presentation.features.main.friends.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlin.chatapp.data.remote.dto.UserModelDto
import com.kotlin.chatapp.domain.model.TaskStateModel
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageAction
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageViewModel
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.utils.IsSuccess

@Composable
fun FriendsListTile(friend: UserModelDto, viewModel: FriendsPageViewModel) {
    var usernameText = remember {
        mutableStateOf(friend.username)
    }
    var nameText = remember {
        mutableStateOf(friend.firstname + " " + friend.lastname)
    }

    LaunchedEffect(viewModel.state.value) {
        if (viewModel.state.value.createChatState.isSuccess == IsSuccess.LOADING) {
            usernameText.value = "Loading..."
            nameText.value = ""
        } else if (viewModel.state.value.createChatState.isSuccess == IsSuccess.ERROR) {
            usernameText.value = viewModel.state.value.createChatState.errorMessage!!
            nameText.value = "Click again!"
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable {
                    viewModel.dispatch(FriendsPageAction.CreateChat(friend_uuid = friend.uuid))
                }
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = if (viewModel.state.value.createChatState.isSuccess != IsSuccess.ERROR) friend.username else usernameText.value,
                style = ChatAppTypo.titleMedium,
                color = if (viewModel.state.value.createChatState.errorMessage.isNullOrEmpty()) MaterialTheme.colorScheme.onTertiary
                else MaterialTheme.colorScheme.error
            )
            Text(
                text = if (viewModel.state.value.createChatState.isSuccess == IsSuccess.NONE) friend.firstname + " " + friend.lastname else nameText.value,
                style = ChatAppTypo.bodySmall,
                color = if (viewModel.state.value.createChatState.errorMessage.isNullOrEmpty()) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.error
            )
        }
        IconButton(onClick = {
            viewModel.dispatch(FriendsPageAction.DeleteFriend(friend.uuid))
        }) {
            if (viewModel.state.value.deleteFriendState.isSuccess != IsSuccess.LOADING) {
                Icon(
                    imageVector = Icons.Default.Close,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "deleteFriend"
                )
            } else {
                CircularProgressIndicator()
            }
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}