package com.kotlin.chatapp.presentation.features.main.friends.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kotlin.chatapp.data.remote.dto.UserModelDto
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageAction
import com.kotlin.chatapp.presentation.features.main.friends.FriendsPageViewModel
import com.kotlin.chatapp.presentation.theme.ChatAppTypo

@Composable
fun FetchedUsersListTile(user: UserModelDto, viewModel: FriendsPageViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (viewModel.state.value.friends.map { it.username }.contains(user.username))
            IconButton(enabled = false, onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = "exists"
                )
            }
        else
            IconButton(onClick = {
                viewModel.dispatch(FriendsPageAction.SendRequest(user.uuid))
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Send"
                )
            }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
        ) {
            Text(
                text = user.username,
                style = ChatAppTypo.titleMedium,
                color = MaterialTheme.colorScheme.onTertiary
            )
            Text(
                text = user.firstname + " " + user.lastname,
                style = ChatAppTypo.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}