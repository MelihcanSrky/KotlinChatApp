package com.kotlin.chatapp.presentation.features.main.chats.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kotlin.chatapp.presentation.theme.ChatAppTypo

@Composable
fun ErrorText(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = text,
            style = ChatAppTypo.headlineSmall,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}