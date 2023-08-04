package com.kotlin.chatapp.presentation.features.main.friends.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlin.chatapp.data.remote.dto.UserModelDto
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageAction
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageViewModel
import com.kotlin.chatapp.presentation.features.main.friends.Tasks
import com.kotlin.chatapp.presentation.theme.ChatAppTypo
import com.kotlin.chatapp.utils.IsSuccess

@Composable
fun FriendsListTile(friend: UserModelDto, viewModel: FriendsPageViewModel) {
    if (viewModel.state.value.task == Tasks.CREATE_CHAT && viewModel.state.value.isSuccess == IsSuccess.LOADING) {
        Box(modifier = Modifier.fillMaxWidth()) {
            CircularProgressIndicator()
        }
    } else if (viewModel.state.value.isSuccess == IsSuccess.ERROR && !viewModel.state.value.createChatErrorMessage.isNullOrEmpty()) {
        Text(
            text = viewModel.state.value.createChatErrorMessage!!,
            style = ChatAppTypo.titleMedium,
            color = MaterialTheme.colorScheme.error
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.dispatch(FriendsPageAction.CreateChat(friend_uuid = friend.uuid))
                }
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = friend.username,
                style = ChatAppTypo.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = friend.firstname + " " + friend.lastname,
                style = ChatAppTypo.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}