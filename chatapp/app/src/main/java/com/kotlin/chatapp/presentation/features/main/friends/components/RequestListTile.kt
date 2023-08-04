package com.kotlin.chatapp.presentation.features.main.friends.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
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
fun RequestsListTile(sender: UserModelDto, viewModel: FriendsPageViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = sender.username,
            style = ChatAppTypo.titleMedium,
            color = MaterialTheme.colorScheme.onTertiary
        )
        Row {
            IconButton(onClick = {
                viewModel.dispatch(
                    FriendsPageAction.AcceptOrDecline(
                        sender.uuid,
                        accept = true
                    )
                )
            }) {
                Icon(
                    imageVector = Icons.Default.Check,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Send"
                )
            }
            IconButton(onClick = {
                viewModel.dispatch(
                    FriendsPageAction.AcceptOrDecline(
                        sender.uuid,
                        accept = false
                    )
                )
            }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = "Send"
                )
            }
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}