package com.kotlin.chatapp.presentation.features.main.chats.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kotlin.chatapp.domain.model.ChatsDataModel
import com.kotlin.chatapp.presentation.navigation.Screens
import com.kotlin.chatapp.presentation.theme.ChatAppTypo

@Composable
fun ChatsListTile(navController: NavController, chat: ChatsDataModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Screens.ChatPage.route + "/${chat.chatUuid}/${chat.chatname}")
            }
            .padding(vertical = 8.dp),
    ) {
        Text(
            text = chat.chatname, color = MaterialTheme.colorScheme.onTertiary, style = ChatAppTypo.headlineSmall
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = chat.lastMessage ?: "...",
                color = MaterialTheme.colorScheme.onTertiary,
                style = ChatAppTypo.bodyMedium
            )
            Text(
                text = chat.lastMessageAt ?: "...",
                color = MaterialTheme.colorScheme.onTertiary,
                style = ChatAppTypo.bodySmall
            )
        }
    }
    Divider(color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
}